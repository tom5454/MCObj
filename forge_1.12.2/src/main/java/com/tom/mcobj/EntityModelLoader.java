package com.tom.mcobj;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EntityModelLoader {
	private static final Gson gson = new GsonBuilder().create();
	private Map<String, Model> models;
	private IResourceManager resourceManager;

	private static Field domainResourceManagers, resourcePacks, resourcePackZipFile, resourcePackFile;
	static {
		try {
			/*printFields(SimpleReloadableResourceManager.class);
			printFields(FallbackResourceManager.class);
			printFields(FileResourcePack.class);
			printFields(AbstractResourcePack.class);*/
			try {
				domainResourceManagers = SimpleReloadableResourceManager.class.getDeclaredField("field_110548_a");
			} catch(Exception e) {
				domainResourceManagers = SimpleReloadableResourceManager.class.getDeclaredField("domainResourceManagers");
			}
			domainResourceManagers.setAccessible(true);
			try {
				resourcePacks = FallbackResourceManager.class.getDeclaredField("field_110540_a");
			} catch(Exception e) {
				resourcePacks = FallbackResourceManager.class.getDeclaredField("resourcePacks");
			}
			resourcePacks.setAccessible(true);
			try {
				resourcePackZipFile = FileResourcePack.class.getDeclaredField("field_110600_d");
			} catch(Exception e) {
				resourcePackZipFile = FileResourcePack.class.getDeclaredField("resourcePackZipFile");
			}
			resourcePackZipFile.setAccessible(true);
			try {
				resourcePackFile = AbstractResourcePack.class.getDeclaredField("field_110597_b");
			} catch(Exception e) {
				resourcePackFile = AbstractResourcePack.class.getDeclaredField("resourcePackFile");
			}
			resourcePackFile.setAccessible(true);
		} catch(Exception e) {
			throw new RuntimeException("Failed to load MCOBJ mod", e);
		}
	}

	/*private static void printFields(Class clazz) {
		System.out.println(clazz.toString());
		Arrays.stream(clazz.getDeclaredFields()).map(f -> f.getName() + " " + f.getType().toString()).forEach(System.out::println);
	}*/

	public EntityModelLoader(IResourceManager resourceManagerIn) {
		models = new HashMap<>();
		this.resourceManager = resourceManagerIn;
		loadModels();
	}

	@SuppressWarnings("unchecked")
	public void loadModels(){
		Reader reader = null;
		IResource iresource = null;
		MCObjInit.log.info("Loading entity models");
		for(ResourceLocation entry : getAllResources(this.resourceManager, "models/tesr", s -> s.endsWith(".json"))){
			MCObjInit.log.info("Loading model: " + entry);
			if(!entry.getResourceDomain().equals("mcobj")){
				MCObjInit.log.error("TESR model file in wrong place: {}, must be in mcobj/models/tesr directory", entry);
				continue;
			}
			String key = entry.getResourcePath();
			int ind = key.lastIndexOf('/');
			String name = key.substring(ind+1, key.length() - 5);
			reader = null;
			iresource = null;
			try {
				iresource = this.resourceManager.getResource(entry);
				reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
				Map<String, Object> json = gson.fromJson(reader, Map.class);

				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(iresource);
				reader = null;
				iresource = null;

				if(json.containsKey("model")){
					ResourceLocation rl = new ResourceLocation(String.valueOf(json.get("model")));
					if(rl.getResourcePath().endsWith(".obj")){
						iresource = this.resourceManager.getResource(new ResourceLocation(rl.getResourceDomain(), "models/redef/" + rl.getResourcePath()));
						reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
						models.put(name, new ObjModel(new BufferedReader(reader)));
					} else {
						/*iresource = this.resourceManager.getResource(new ResourceLocation(rl.getNamespace(), "models/redef/" + rl.getPath() + ".json"));
						reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
						Map<String, Object> modelJson = gson.fromJson(reader, Map.class);
						models.put(name, new JsonModel(modelJson));*/
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(iresource);
			}
		}
	}

	private List<ResourceLocation> getAllResources(IResourceManager resourceManager, String folder, Predicate<String> checker) {
		List<ResourceLocation> list = new ArrayList<>();
		try {
			getAllResources(resourceManager, folder, list, checker);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	private void getAllResources(IResourceManager resourceManager, String folder, List<ResourceLocation> rl, Predicate<String> checker) throws Exception {
		if(resourceManager instanceof SimpleReloadableResourceManager) {
			Map<String, FallbackResourceManager> drms = (Map<String, FallbackResourceManager>) domainResourceManagers.get(resourceManager);
			for (FallbackResourceManager r : drms.values()) {
				getAllResources(r, folder, rl, checker);
			}
		}else if(resourceManager instanceof FallbackResourceManager){
			List<IResourcePack> rps = (List<IResourcePack>) resourcePacks.get(resourceManager);
			for (IResourcePack iResourcePack : rps) {
				getAllResources(iResourcePack, folder, rl, checker);
			}
		}
	}

	private void getAllResources(IResourcePack pack, String folder, List<ResourceLocation> rl, Predicate<String> checker) throws Exception {
		if(pack instanceof FileResourcePack) {
			ZipFile zf = (ZipFile) resourcePackZipFile.get(pack);
			Enumeration<? extends ZipEntry> ze = zf.entries();
			while (ze.hasMoreElements()) {
				ZipEntry zipEntry = ze.nextElement();
				String name = zipEntry.getName();
				if(name.startsWith("assets/")) {
					name = name.substring(7);
					int i = name.indexOf('/');
					if(i != -1){
						String path = name.substring(i+1);
						if(path.startsWith(folder) && checker.test(path)) {
							rl.add(new ResourceLocation(name.substring(0, i), path));
						}
					}
				}
			}
		}else if(pack instanceof FolderResourcePack) {
			File rf = (File) resourcePackFile.get(pack);
			File assets = new File(rf, "assets");
			if(assets.exists()) {
				for(File f : assets.listFiles()) {
					File ff = new File(f, folder);
					if(ff.exists()) {
						for(File a : ff.listFiles()) {
							if(checker.test(a.getName()))rl.add(new ResourceLocation(f.getName(), folder + "/" + a.getName()));
						}
					}
				}
			}
		}
	}

	public Model getModel(String name) {
		return models.get(name);
	}
	public abstract static class Model {
		public abstract boolean renderNormal(String name, BufferBuilder bb);
		public abstract void renderReUV(BufferBuilder bb, UnaryOperator<Vec2f> remap);
	}
}