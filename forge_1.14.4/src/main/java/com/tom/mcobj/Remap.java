package com.tom.mcobj;

import static com.tom.mcobj.MCObjInit.log;
import static org.objectweb.asm.Opcodes.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jline.utils.InputStreamReader;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.Face;
import net.minecraftforge.client.model.obj.OBJModel.Material;
import net.minecraftforge.client.model.obj.OBJModel.MaterialLibrary;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import com.mojang.blaze3d.platform.GlStateManager;

import com.tom.mcobj.proxy.ClientProxy;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.INameMappingService.Domain;

public class Remap {
	private static final boolean LOG_MODELS = false;
	public static boolean RELOAD_KEY_ENABLE = false;
	private static ConcurrentHashMap<ModelName, RendererModel> modelsMap = new ConcurrentHashMap<>();
	protected static final List<BlockPart> EMPTY = new ArrayList<>();
	private static Field field_217849_F;
	public static Field matLib;
	private static Field resourceManager;
	private static Field manager;
	private static Field modifiersField;
	private static Field boxName;
	private static Field textureLocation;
	private static Map<String, String> names;
	private static List<RendererModel> modellist;
	private static List<List<String>> log_info;
	private static Map<String, String> fieldRemapper = new HashMap<>();
	static {
		if(LOG_MODELS){
			names = new HashMap<>();
			modellist = new ArrayList<>();
			log_info = new ArrayList<>();
		}
	}
	public static void init() {
		try {
			/*log.info("ModelBakery");
			for (Method m : ModelBakery.class.getDeclaredMethods()) {
				log.info(m.getReturnType() + " " + m.getName() + Arrays.toString(m.getParameterTypes()));
			}*/
			field_217849_F = ModelBakery.class.getDeclaredField("field_217849_F");
			field_217849_F.setAccessible(true);

			matLib = OBJModel.class.getDeclaredField("matLib");
			matLib.setAccessible(true);

			try {
				resourceManager = ModelBakery.class.getDeclaredField("field_177598_f");
			} catch (Exception e) {
				resourceManager = ModelBakery.class.getDeclaredField("resourceManager");
			}
			resourceManager.setAccessible(true);

			manager = OBJLoader.class.getDeclaredField("manager");
			manager.setAccessible(true);

			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);

			for(Field f : SimpleTexture.class.getDeclaredFields()){
				if(f.getType() == ResourceLocation.class){
					textureLocation = f;
				}
			}
			textureLocation.setAccessible(true);

			try {
				boxName = RendererModel.class.getDeclaredField("field_78802_n");
			} catch (Exception e) {
				boxName = RendererModel.class.getDeclaredField("boxName");
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			MethodVisitor mv;

			String name = "MCOBJAccessDyn";
			cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER, name, null, "java/lang/Object", new String[]{});
			cw.visitSource(".dynamic", null);
			{
				mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
			}
			String desc = "(Lnet/minecraft/client/renderer/model/ModelBakery;"
					+ "Lnet/minecraft/client/renderer/model/BlockModel;"
					+ "Ljava/util/function/Function;"
					+ "Lnet/minecraft/client/renderer/texture/ISprite;"
					+ "Lnet/minecraft/client/renderer/vertex/VertexFormat;"
					+ "Lnet/minecraft/client/renderer/model/BlockModel;)"
					+ "Lnet/minecraft/client/renderer/model/IBakedModel;";
			{
				mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "bake", desc, null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitVarInsn(Opcodes.ALOAD, 2);
				mv.visitVarInsn(Opcodes.ALOAD, 3);
				mv.visitVarInsn(Opcodes.ALOAD, 4);
				mv.visitVarInsn(Opcodes.ALOAD, 5);
				mv.visitMethodInsn(INVOKESTATIC, "com/tom/mcobj/Remap", "bake", desc, false);
				mv.visitInsn(ARETURN);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
			desc = "(Ljava/util/Set;Lnet/minecraft/client/renderer/model/BlockModel;)V";
			{
				mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "getTextures", desc, null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitMethodInsn(INVOKESTATIC, "com/tom/mcobj/Remap", "getTextures", desc, false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
			desc = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;F)V";
			{
				mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "compileDisplayList", desc, null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitVarInsn(Opcodes.FLOAD, 1);
				mv.visitMethodInsn(INVOKESTATIC, "com/tom/mcobj/Remap", "compileDisplayList", desc, false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
			desc = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;)V";
			{
				mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "logNames", desc, null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitMethodInsn(INVOKESTATIC, "com/tom/mcobj/Remap", "logNames", desc, false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
			cw.visitEnd();
			Object access = ((TransformingClassLoader)BlockModel.class.getClassLoader()).getClass(name, cw.toByteArray()).newInstance();
			//log.info(access);
			Optional<BiFunction<Domain, String, String>> srgNameMapper = Launcher.INSTANCE.environment().findNameMapping("srg");
			try (BufferedReader r = new BufferedReader(new InputStreamReader(Remap.class.getResourceAsStream("/com/tom/mcobj/modelnames.txt")))){
				String ln;
				while((ln = r.readLine()) != null){
					String pln = ln;
					int commentStart = pln.indexOf("//");
					if(commentStart > -1){
						pln = pln.substring(0, commentStart).trim();
					}
					String[] sp = pln.split(";");
					String srg = sp[0];
					String add = null;
					if(srg.contains("[")){
						int ind = srg.indexOf('[');
						add = srg.substring(ind);
						srg = srg.substring(0, ind);
					}
					if(srg.contains("/")){
						int ind = srg.indexOf('/');
						add = srg.substring(ind);
						srg = srg.substring(0, ind);
					}
					final String fsrg = srg;
					String remap = srgNameMapper.map(m -> m.apply(Domain.FIELD, fsrg)).orElse(srg);
					if(add != null)remap += add;
					fieldRemapper.put(remap, sp[1]);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public static IUnbakedModel getUnbakedModel(ResourceLocation modelLocation, ModelLoader s) {
		try {
			//log.info("Remap.getUnbakedModel()");
			IUnbakedModel missing = ((Map<ResourceLocation, IUnbakedModel>)field_217849_F.get(s)).get(ModelLoader.MODEL_MISSING);
			IUnbakedModel model = missing;
			if(modelLocation.getPath().endsWith(".obj")){
				try {
					if(manager.get(OBJLoader.INSTANCE) == null){
						OBJLoader.INSTANCE.onResourceManagerReload((IResourceManager) resourceManager.get(s));
					}
					IUnbakedModel m = OBJLoader.INSTANCE.loadModel(ModelLoaderRegistry.getActualLocation(modelLocation));
					if(m instanceof OBJModel){
						model = new BlockModelObj((OBJModel) m);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				model = Access.SUPERgetUnbakedModel(s, modelLocation);
			}
			return model;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	public static IBakedModel bake(ModelBakery bakery, BlockModel p_217644_2_, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format, BlockModel caller){
		//log.info("Remap.bake()");
		if(caller.getElements() == EMPTY){
			BlockModelObj orig = null;
			BlockModel c = caller.parent;
			while(true){
				if(c instanceof BlockModelObj){
					orig = (BlockModelObj) c;
					break;
				}
				c = c.parent;
			}
			//log.info("Baking OBJ");
			return orig.bake(bakery, spriteGetter, sprite, format, caller);
		}
		else return Access.NORMbake(caller, bakery, p_217644_2_, spriteGetter, sprite, format);
	}
	public static void bakeObjQuads(UnpackedBakedQuad.Builder builder, Face f, OBJModel model){
		Material mat = model.getMatLib().getMaterial(f.getMaterialName());
		String name = mat.getName();
		int ind = name.indexOf("#tint");
		if(ind > -1){
			String tintInd = name.substring(ind + 5);
			builder.setQuadTint(Integer.parseInt(tintInd));
		}
	}
	public static void getTextures(Set<ResourceLocation> tex, BlockModel model){
		getTextures0(tex, model, model);
	}
	private static void getTextures0(Set<ResourceLocation> tex, BlockModel model, BlockModel top){
		if(model instanceof BlockModelObj){
			BlockModelObj bmo = (BlockModelObj) model;
			OBJModel obj = bmo.getObjModel();
			try {
				MaterialLibrary ml = (MaterialLibrary) matLib.get(obj);
				for (String mat : ml.getMaterialNames()) {
					Material m = ml.getMaterial(mat);
					ResourceLocation rl = new ResourceLocation(top.resolveTextureName(m.getTexture().getPath()));
					tex.add(rl);
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}else if(model != null) {
			getTextures0(tex, model.parent, top);
		}
	}

	public static void compileDisplayList(RendererModel model, float scale){
		ModelName name = toName(model);
		log.info("Compiling: " + name + " scale: " + scale);
		if(modelsMap.contains(name)){
			log.error("Duplicated model: " + name);
		}
		modelsMap.put(name, model);
		com.tom.mcobj.EntityModelLoader.Model m = ClientProxy.loader.getModel(name.model);
		if(m == null){
			m = ClientProxy.loader.getModel("model_box");
			if(m == null)
				Access.NORMcompileDisplayList(model, scale);
			else {
				int displayList = GLAllocation.generateDisplayLists(1);
				synchronized (model) {
					Access.FdisplayListS(model, displayList);
				}
				GlStateManager.newList(displayList, GL11.GL_COMPILE);
				GlStateManager.pushMatrix();
				GlStateManager.scaled(scale, scale, scale);
				BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
				for(int i = 0; i < model.cubeList.size(); ++i) {
					GlStateManager.pushMatrix();
					ModelBox box = model.cubeList.get(i);
					float delta = Access.Fdelta(box);
					float dx = box.posX2 - box.posX1;
					float dy = box.posY2 - box.posY1;
					float dz = box.posZ2 - box.posZ1;
					GlStateManager.translated(box.posX1 - delta, box.posY1 - delta, box.posZ1 - delta);
					GlStateManager.scaled(dx + delta*2, dy + delta*2, dz + delta*2);
					int texU = Access.FtexU(box);
					int texV = Access.FtexV(box);
					float w = dx * 2 + dz * 2;
					float h = (dy + dz) * 2;//log.info(uv.x + " " + uv.y);
					m.renderReUV(bufferbuilder, uv -> {
						float x = uv.x;
						float y = 1 - uv.y;
						float qx = (x % .25f) * 4;
						float qy = (y % .25f) * 4;
						float u = texU + (x) * w;
						float v = texV + (y) * h;
						String block = null;
						if(x >= .25f && x < .5f && y < .25f){//xW zH
							block = "top";
							u = texU + dz +  qx * dx;
							v = texV +       qy * dz;
						}else if(x >= .5f && x < .75f && y < .25f){//xW zH
							block = "bottom";
							u = texU + dz + dx +  qx * dx;
							v = texV +            qy * dz;
						}else if(x < .25f && y < .5f && y >= .25f){//zW yH
							block = "left";
							u = texU +       qx * dz;
							v = texV + dz +  qy * dy;
						}else if(x < .5f && x >= .25f && y < .5f && y >= .25f){//xW yH
							block = "front";
							u = texU + dz +  qx * dx;
							v = texV + dz +  qy * dy;
						}else if(x < .75f && x >= .5f && y < .5f && y >= .25f){//zW yH
							block = "right";
							u = texU + dz + dx +  qx * dz;
							v = texV + dz +       qy * dy;
						}else if(x >= .75f && y < .5f && y >= .25f){//xW yH
							block = "back";
							u = texU + dz * 2 + dx +  qx * dx;
							v = texV + dz +           qy * dy;
						}
						/*if(block != null){
							log.info(block + " " + texU + " " + texV + " " + x + " " + y + " " + dx + " " + dy + " " + dz);
							log.info(u + " " + v);
						}*/
						return new Vec2f(
								u / model.textureWidth,//x,//
								v / model.textureHeight//-y//
								);
					});
					GlStateManager.popMatrix();
				}
				//Tessellator.getInstance().draw();
				GlStateManager.popMatrix();
				GlStateManager.endList();
				Access.FcompiledS(model, true);
			}
		} else {
			int displayList = GLAllocation.generateDisplayLists(1);
			synchronized (model) {
				Access.FdisplayListS(model, displayList);
			}
			GlStateManager.newList(displayList, GL11.GL_COMPILE);
			GlStateManager.pushMatrix();
			GlStateManager.scaled(scale*16, scale*16, scale*16);
			GlStateManager.translated(-model.rotationPointX / 16, -model.rotationPointY / 16, -model.rotationPointZ / 16);
			if(!m.renderNormal(fieldRemapper.getOrDefault(model.boxName, model.boxName), Tessellator.getInstance().getBuffer()))
				m.renderNormal(model.boxName, Tessellator.getInstance().getBuffer());
			GlStateManager.popMatrix();
			GlStateManager.endList();
			Access.FcompiledS(model, true);
		}
	}

	public static void clearCache() {
		Minecraft.getInstance().execute(() -> {
			log.info("TESR model ids: ");
			log.info("model_box");
			if(modellist != null){
				modellist.forEach(Remap::writeName);
				log_info.forEach(l -> l.forEach(log::info));
				try (PrintWriter wr = new PrintWriter("modelnames.txt")){
					names.entrySet().stream().sorted((a, b) -> a.getValue().compareTo(b.getValue())).forEach(e -> {
						wr.println(e.getKey() + " //" + e.getValue());
					});
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				modellist = null;
				log_info = null;
			}
			modelsMap.keySet().forEach(log::info);
			modelsMap.values().forEach(e -> {
				GlStateManager.deleteLists(Access.FdisplayList(e), 1);
				Access.FcompiledS(e, false);
				Access.FdisplayListS(e, 0);
			});
			modelsMap.clear();
		});
	}
	public static void setFinalField(Field field, Object ins, Object value) throws Exception {
		field.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(ins, value);
	}
	public static ModelName toName(RendererModel model) {
		Model p = Access.FparentModel(model);
		if(model.boxName == null){
			try {
				Class clazz = p.getClass();
				while(clazz != Object.class){
					for(Field f : clazz.getDeclaredFields()) {
						if(f.getType() == RendererModel.class){
							f.setAccessible(true);
							if(f.get(p) == model){
								setFinalField(boxName, model, f.getName());
								clazz = Object.class;
								break;
							}
						}else if(f.getType() == RendererModel[].class){
							f.setAccessible(true);
							RendererModel[] ms = (RendererModel[]) f.get(p);
							if(ms != null){
								for (int i = 0; i < ms.length; i++) {
									if(ms[i] == model){
										setFinalField(boxName, model, f.getName() + "[" + i + "]");
										clazz = Object.class;
										break;
									}
								}
							}
						}
					}
					clazz = clazz.getSuperclass();
					if(clazz == null)clazz = Object.class;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(model.boxName == null){
				RendererModel parent = Access.FparentRenderer(model);
				if(parent != null){
					ModelName parentName = toName(parent);
					if(parent.childModels != null){
						for (int i = 0; i < parent.childModels.size(); i++) {
							if(model == parent.childModels.get(i)){
								try {
									setFinalField(boxName, model, parentName.name + "/" + i);
								} catch (Exception e) {
									e.printStackTrace();
								}
								break;
							}
						}
					}
				}
			}
		}
		if(p == null)log.error(model + " is missing its parent! " + model.boxName);
		if(model.boxName == null)log.error(model + " is missing its name!");
		ResourceLocation to = Access.FcurrTex(Minecraft.getInstance().textureManager);
		ModelName mn = new ModelName();
		mn.name = model.boxName;
		mn.model = (p == null ? "null" : p.getClass().getSimpleName().toLowerCase());
		if(to != null)
			mn.tex = to.getNamespace() + "/" + to.getPath();
		else mn.tex = "error";
		return mn;
	}
	private static class ModelName {
		String model;
		String name;
		String tex;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((model == null) ? 0 : model.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((tex == null) ? 0 : tex.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ModelName other = (ModelName) obj;
			if (model == null) {
				if (other.model != null) return false;
			} else if (!model.equals(other.model)) return false;
			if (name == null) {
				if (other.name != null) return false;
			} else if (!name.equals(other.name)) return false;
			if (tex == null) {
				if (other.tex != null) return false;
			} else if (!tex.equals(other.tex)) return false;
			return true;
		}

		@Override
		public String toString() {
			return String.format("%s %s %s", model, fieldRemapper.getOrDefault(name, name), tex);
		}
	}
	public static void logNames(RendererModel rm){
		if(LOG_MODELS)modellist.add(rm);
	}
	public static void writeName(RendererModel rm){
		ModelName name = toName(rm);
		log.info(name);
		if(name.name != null){
			if(name.name.startsWith("field_")){
				names.put(name.name + ";", name.model);
			}else{
				names.put(";" + name.name, name.model);
			}
		}
	}
	public static void main(String[] args) {
		//SRG, normal
		//BiMap<String, String> mapping = HashBiMap.create();
		//normal, SRGs
		Map<String, List<String>> mappings = new HashMap<>();
		System.out.println("Loading mappings");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try (BufferedReader r = new BufferedReader(new FileReader("fields.csv"))){
			r.readLine();
			String ln;
			while((ln = r.readLine()) != null){
				String[] sp = ln.split(",", 4);
				if("0".equals(sp[2])){
					//mapping.put(sp[0], sp[1]);
					mappings.computeIfAbsent(sp[1], k -> new ArrayList<>()).add(sp[0]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Loaded mappings");
		try (BufferedReader r = new BufferedReader(new InputStreamReader(Remap.class.getResourceAsStream("/com/tom/mcobj/modelnames.txt")));
				PrintWriter w = new PrintWriter("modelexport.txt")){
			String ln;
			while((ln = r.readLine()) != null){
				String pln = ln;
				String comment = "";
				int commentStart = pln.indexOf("//");
				if(commentStart > -1){
					pln = pln.substring(0, commentStart).trim();
					comment = ln.substring(commentStart + 2).trim();
				}
				String[] sp = pln.split(";");
				if(sp.length < 2){
					sp = new String[]{sp[0], ""};
				}
				boolean askRename = false;
				if(sp[0].isEmpty()){//SRG
					String name = sp[1];
					String add = null;
					if(name.contains("[")){
						int ind = name.indexOf('[');
						add = name.substring(ind);
						name = name.substring(0, ind);
						askRename = true;
					}
					if(name.contains("/")){
						int ind = name.indexOf('/');
						add = name.substring(ind);
						name = name.substring(0, ind);
						askRename = true;
					}
					List<String> srg = mappings.get(name);
					if(srg == null){
						System.out.println("Enter SRG name for " + ln);
						sp[0] = in.readLine();
					}else if(srg.size() > 1){
						System.out.println("Select mapping for: " + ln);
						for (int i = 0; i < srg.size(); i++) {
							System.out.println(i + ": " + srg.get(i));
						}
						int id = -1;
						while(id == -1){
							try {
								id = Integer.parseInt(in.readLine());
								if(id < 0 || id >= srg.size()){
									System.err.println("Out of bounds");
									id = -1;
								}
							} catch (NumberFormatException e) {
								id = -1;
							}
						}
						sp[0] = srg.get(id);
					}else{
						sp[0] = srg.get(0);
					}
					if(add != null)sp[0] += add;
				}else if(sp[1].isEmpty()){//normal
					System.out.println("Enter name for " + ln);
					String name = in.readLine();
					if(!name.trim().isEmpty())
						sp[1] = name;
					else
						sp[1] = sp[0];
					askRename = false;
				}
				if(askRename){
					System.out.println("Rename? " + ln + " SRG: " + sp[0]);
					String name = in.readLine();
					if(!name.trim().isEmpty()){
						comment = comment + " " + sp[1];
						sp[1] = name;
					}
				}
				w.println(sp[0] + ";" + sp[1] + " //" + comment);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
