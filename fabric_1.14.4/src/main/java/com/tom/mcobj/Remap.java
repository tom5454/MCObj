package com.tom.mcobj;

import static com.tom.mcobj.MCObjInit.log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Box;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

import com.mojang.blaze3d.platform.GlStateManager;

import com.tom.mcobj.forge.BakedQuadBuilder;
import com.tom.mcobj.forge.Fabric;
import com.tom.mcobj.forge.Fabric.OBJLoader;
import com.tom.mcobj.forge.OBJModel;
import com.tom.mcobj.forge.OBJModel.Face;
import com.tom.mcobj.forge.OBJModel.Material;
import com.tom.mcobj.forge.OBJModel.MaterialLibrary;
import com.tom.mcobj.proxy.ClientProxy;

public class Remap {
	private static final boolean LOG_MODELS = false;
	public static boolean RELOAD_KEY_ENABLE = false;
	private static ConcurrentHashMap<ModelName, Cuboid> modelsMap = new ConcurrentHashMap<>();
	protected static final List<ModelElement> EMPTY = new ArrayList<>();
	private static Field field_217849_F;
	private static Field resourceManager;
	private static Field modifiersField;
	private static Field boxName;
	private static Field textureLocation;
	private static Map<String, String> names;
	private static List<Cuboid> modellist;
	private static List<List<String>> log_info;
	private static Map<String, String> fieldRemapper = new HashMap<>(), clazzNames = new HashMap<>();
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
			try {
				field_217849_F = ModelLoader.class.getDeclaredField("MISSING");
			} catch (Exception e) {
				field_217849_F = ModelLoader.class.getDeclaredField("field_5374");
			}
			field_217849_F.setAccessible(true);

			try {
				resourceManager = ModelLoader.class.getDeclaredField("field_5379");
			} catch (Exception e) {
				resourceManager = ModelLoader.class.getDeclaredField("resourceManager");
			}
			resourceManager.setAccessible(true);

			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);

			for(Field f : ResourceTexture.class.getDeclaredFields()){
				if(f.getType() == Identifier.class){
					textureLocation = f;
				}
			}
			textureLocation.setAccessible(true);

			try {
				boxName = Cuboid.class.getDeclaredField("field_3662");
			} catch (Exception e) {
				boxName = Cuboid.class.getDeclaredField("name");
			}

			/*ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
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
					+ "Lnet/minecraft/client/renderer/model/JsonUnbakedModel;"
					+ "Ljava/util/function/Function;"
					+ "Lnet/minecraft/client/renderer/texture/ISprite;"
					+ "Lnet/minecraft/client/renderer/vertex/VertexFormat;"
					+ "Lnet/minecraft/client/renderer/model/JsonUnbakedModel;)"
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
			desc = "(Ljava/util/Set;Lnet/minecraft/client/renderer/model/JsonUnbakedModel;)V";
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
			desc = "(Lnet/minecraft/client/renderer/entity/model/Cuboid;F)V";
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
			desc = "(Lnet/minecraft/client/renderer/entity/model/Cuboid;)V";
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
			Object access = ((TransformingClassLoader)JsonUnbakedModel.class.getClassLoader()).getClass(name, cw.toByteArray()).newInstance();
			//log.info(access);*/
			Optional<Function<String, String>> srgNameMapper = Fabric.findNameMapping();
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
					String remap = srgNameMapper.map(m -> m.apply(fsrg)).orElse(srg);
					if(add != null)remap += add;
					fieldRemapper.put(remap, sp[1]);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try (BufferedReader r = new BufferedReader(new InputStreamReader(Remap.class.getResourceAsStream("/com/tom/mcobj/classnames.txt")))){
			String ln;
			while((ln = r.readLine()) != null){
				String[] sp = ln.split(";");
				String fn = sp[0];
				String rn = sp[1];
				String on = sp[2];
				clazzNames.put(fn, rn);
				clazzNames.put(on, rn);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static UnbakedModel getUnbakedModel(Identifier modelLocation, Object s) {
		try {
			//log.info("Remap.getUnbakedModel()");
			UnbakedModel model = null;
			if(modelLocation.getPath().endsWith(".obj")){
				try {
					if(OBJLoader.INSTANCE.manager == null){
						OBJLoader.INSTANCE.manager = (ResourceManager) resourceManager.get(s);
					}
					UnbakedModel m = OBJLoader.INSTANCE.loadModel(Fabric.getActualLocation(modelLocation));
					if(m instanceof OBJModel){
						model = new BlockModelObj((OBJModel) m);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				return null;
			}
			return model;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	public static BakedModel bake(ModelLoader bakery, Function<Identifier, Sprite> spriteGetter, ModelBakeSettings sprite, JsonUnbakedModel caller){
		//log.info("Remap.bake() " + caller.id);
		if(caller.getElements() == EMPTY){
			BlockModelObj orig = null;
			JsonUnbakedModel c = Access.Fparent(caller);
			while(true){
				if(c instanceof BlockModelObj){
					orig = (BlockModelObj) c;
					break;
				}
				c = Access.Fparent(c);
			}
			//log.info("Baking OBJ");
			return orig.bake(bakery, spriteGetter, sprite, caller);
		}
		else return null;
	}
	public static void bakeObjQuads(BakedQuadBuilder builder, Face f, OBJModel model){
		Material mat = model.getMatLib().getMaterial(f.getMaterialName());
		String name = mat.getName();
		int ind = name.indexOf("#tint");
		if(ind > -1){
			String tintInd = name.substring(ind + 5);
			builder.setTint(Integer.parseInt(tintInd));
		}
	}
	public static void getTextures(Set<Identifier> tex, JsonUnbakedModel model){
		getTextures0(tex, model, model);
	}
	private static void getTextures0(Set<Identifier> tex, JsonUnbakedModel model, JsonUnbakedModel top){
		if(model instanceof BlockModelObj){
			BlockModelObj bmo = (BlockModelObj) model;
			OBJModel obj = bmo.getObjModel();
			try {
				MaterialLibrary ml = obj.getMatLib();
				for (String mat : ml.getMaterialNames()) {
					Material m = ml.getMaterial(mat);
					Identifier rl = new Identifier(top.resolveTexture(m.getTexture().getPath()));
					tex.add(rl);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}else if(model != null) {
			getTextures0(tex, Access.Fparent(model), top);
		}
	}

	public static boolean compileDisplayList(Cuboid model, float scale){
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
				return false;
			else {
				int displayList = GlAllocationUtils.genLists(1);
				synchronized (model) {
					Access.FdisplayListS(model, displayList);
				}
				GlStateManager.newList(displayList, GL11.GL_COMPILE);
				GlStateManager.pushMatrix();
				GlStateManager.scaled(scale, scale, scale);
				BufferBuilder bufferbuilder = Tessellator.getInstance().getBufferBuilder();
				for(int i = 0; i < model.boxes.size(); ++i) {
					GlStateManager.pushMatrix();
					Box box = model.boxes.get(i);
					float delta = Access.Fdelta(box);
					float dx = box.xMax - box.xMin;
					float dy = box.yMax - box.yMin;
					float dz = box.zMax - box.zMin;
					GlStateManager.translated(box.xMin - delta, box.yMin - delta, box.zMin - delta);
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
			int displayList = GlAllocationUtils.genLists(1);
			synchronized (model) {
				Access.FdisplayListS(model, displayList);
			}
			GlStateManager.newList(displayList, GL11.GL_COMPILE);
			GlStateManager.pushMatrix();
			GlStateManager.scaled(scale*16, scale*16, scale*16);
			GlStateManager.translated(-model.rotationPointX / 16, -model.rotationPointY / 16, -model.rotationPointZ / 16);
			if(!m.renderNormal(fieldRemapper.getOrDefault(model.name, model.name), Tessellator.getInstance().getBufferBuilder()))
				m.renderNormal(model.name, Tessellator.getInstance().getBufferBuilder());
			GlStateManager.popMatrix();
			GlStateManager.endList();
			Access.FcompiledS(model, true);
		}
		return true;
	}

	public static void clearCache() {
		MinecraftClient.getInstance().execute(() -> {
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
	public static ModelName toName(Cuboid model) {
		Model p = Access.FparentModel(model);
		if(model.name == null){
			try {
				Class clazz = p.getClass();
				while(clazz != Object.class){
					for(Field f : clazz.getDeclaredFields()) {
						if(f.getType() == Cuboid.class){
							f.setAccessible(true);
							if(f.get(p) == model){
								setFinalField(boxName, model, f.getName());
								clazz = Object.class;
								break;
							}
						}else if(f.getType() == Cuboid[].class){
							f.setAccessible(true);
							Cuboid[] ms = (Cuboid[]) f.get(p);
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
			if(model.name == null){
				Cuboid parent = Access.FparentRenderer(model);
				if(parent != null){
					ModelName parentName = toName(parent);
					if(parent.children != null){
						for (int i = 0; i < parent.children.size(); i++) {
							if(model == parent.children.get(i)){
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
		if(p == null)log.error(model + " is missing its parent! " + model.name);
		if(model.name == null)log.error(model + " is missing its name!");
		Identifier to = Access.FcurrTex(MinecraftClient.getInstance().getTextureManager());
		ModelName mn = new ModelName();
		mn.name = model.name;
		String clazz = p.getClass().getSimpleName().toLowerCase();
		mn.model = (p == null ? "null" : clazzNames.getOrDefault(clazz, clazz));
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
	public static void logNames(Cuboid rm){
		if(LOG_MODELS)modellist.add(rm);
	}
	public static void writeName(Cuboid rm){
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
}
