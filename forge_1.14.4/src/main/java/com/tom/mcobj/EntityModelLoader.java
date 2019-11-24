package com.tom.mcobj;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EntityModelLoader {
	private static final Gson gson = new GsonBuilder().create();
	private Map<String, Model> models;
	private IResourceManager resourceManager;

	public EntityModelLoader(IResourceManager resourceManagerIn) {
		models = new HashMap<>();
		this.resourceManager = resourceManagerIn;
		loadModels();
	}

	@SuppressWarnings("unchecked")
	public void loadModels(){
		Reader reader = null;
		IResource iresource = null;
		for(ResourceLocation entry : this.resourceManager.getAllResourceLocations("models/tesr", s -> s.endsWith(".json"))){
			if(!entry.getNamespace().equals("mcobj")){
				MCObjInit.log.error("TESR model file in wrong place: {}, must be in mcobj/models/tesr directory", entry);
				continue;
			}
			String key = entry.getPath();
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
					if(rl.getPath().endsWith(".obj")){
						iresource = this.resourceManager.getResource(new ResourceLocation(rl.getNamespace(), "models/redef/" + rl.getPath()));
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

	public Model getModel(String name) {
		return models.get(name);
	}
	public abstract static class Model {
		public abstract boolean renderNormal(String name, BufferBuilder bb);
		public abstract void renderReUV(BufferBuilder bb, UnaryOperator<Vec2f> remap);
	}
}