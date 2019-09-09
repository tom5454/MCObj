package com.tom.mcobj;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.Face;
import net.minecraftforge.client.model.obj.OBJModel.Material;
import net.minecraftforge.client.model.obj.OBJModel.MaterialLibrary;
import net.minecraftforge.client.model.obj.OBJModel.OBJBakedModel;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.IClip;

import com.google.common.collect.ImmutableMap;

import cpw.mods.modlauncher.TransformingClassLoader;

public class Remap {
	private static final List<BlockPart> EMPTY = new ArrayList<>();
	private static Field field_217849_F;
	private static Field matLib;
	private static Field resourceManager;
	private static Field manager;
	//private static Set<ResourceLocation> missingTexs = new HashSet<>();

	public static void init() {
		try {
			/*System.out.println("ModelBakery");
			for (Method m : ModelBakery.class.getDeclaredMethods()) {
				System.out.println(m.getReturnType() + " " + m.getName() + Arrays.toString(m.getParameterTypes()));
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
			cw.visitEnd();
			Object access = ((TransformingClassLoader)BlockModel.class.getClassLoader()).getClass(name, cw.toByteArray()).newInstance();
			//System.out.println(access);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static void addTextures(TextureStitchEvent.Pre event){
		//missingTexs.forEach(event::addSprite);
	}
	@SuppressWarnings("unchecked")
	public static IUnbakedModel getUnbakedModel(ResourceLocation modelLocation, ModelLoader s) {
		try {
			//System.out.println("Remap.getUnbakedModel()");
			IUnbakedModel missing = ((Map<ResourceLocation, IUnbakedModel>)field_217849_F.get(s)).get(ModelLoader.MODEL_MISSING);
			IUnbakedModel model = Access.SUPERgetUnbakedModel(s, modelLocation);
			if(model == missing && modelLocation.getPath().endsWith(".obj")){
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
			}
			return model;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	public static IBakedModel bake(ModelBakery bakery, BlockModel p_217644_2_, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format, BlockModel caller){
		//System.out.println("Remap.bake()");
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
			//System.out.println("Baking OBJ");
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
	@SuppressWarnings("deprecation")
	public static class BlockModelObj extends BlockModel {
		private OBJModel parent;

		public BlockModelObj(OBJModel parent) {
			super(new ResourceLocation("block/block"), Collections.emptyList(), Collections.emptyMap(), true, false, ItemCameraTransforms.DEFAULT, Collections.emptyList());
			this.parent = parent;
		}
		@Override
		public Collection<ResourceLocation> getDependencies() {
			return parent.getDependencies();
		}
		@Override
		public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
			return parent.getTextures(modelGetter, missingTextureErrors);
		}
		@Override
		public IBakedModel bake(ModelBakery p_217641_1_, Function<ResourceLocation, TextureAtlasSprite> p_217641_2_, ISprite p_217641_3_) {
			return parent.bake(p_217641_1_, p_217641_2_, p_217641_3_);
		}
		@Override
		public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
			return bake(bakery, spriteGetter, sprite, format, this);
		}
		public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format, BlockModel caller) {
			ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
			builder.put(ModelLoader.White.LOCATION.toString(), ModelLoader.White.INSTANCE);
			TextureAtlasSprite missing = spriteGetter.apply(new ResourceLocation("missingno"));
			try {
				MaterialLibrary ml = (MaterialLibrary) matLib.get(parent);
				for (String mat : ml.getMaterialNames()) {
					Material m = ml.getMaterial(mat);
					ResourceLocation rl = new ResourceLocation(caller.resolveTextureName(m.getTexture().getPath()));
					TextureAtlasSprite tex = spriteGetter.apply(rl);
					if(tex == missing && !rl.getPath().equals("missingno")){
						//missingTexs.add(rl);
					}
					builder.put(mat, tex);
				}
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
			builder.put("missingno", missing);
			TextureAtlasSprite particle;
			ResourceLocation rl = new ResourceLocation(caller.resolveTextureName("particle"));
			particle = spriteGetter.apply(rl);
			if(particle == missing && !rl.getPath().equals("missingno")){
				//missingTexs.add(rl);
			}
			OBJBakedModel baked = parent.new OBJBakedModel(parent, sprite.getState(), format, builder.build());
			return new BakedObjModel(baked, particle);
		}
		@Override
		public IModelState getDefaultState() {
			return parent.getDefaultState();
		}
		@Override
		public Optional<? extends IClip> getClip(String name) {
			return parent.getClip(name);
		}
		@Override
		public IUnbakedModel process(ImmutableMap<String, String> customData) {
			return parent.process(customData);
		}
		@Override
		public IUnbakedModel smoothLighting(boolean value) {
			return parent.smoothLighting(value);
		}
		@Override
		public IUnbakedModel gui3d(boolean value) {
			return parent.gui3d(value);
		}
		@Override
		public IUnbakedModel retexture(ImmutableMap<String, String> textures) {
			return parent.retexture(textures);
		}
		@Override
		public List<BlockPart> getElements() {
			return EMPTY;
		}
		public OBJModel getObjModel(){
			return parent;
		}
	}
	public static class BakedObjModel implements IBakedModel {
		private IBakedModel parent;
		private TextureAtlasSprite particle;
		public BakedObjModel(OBJBakedModel model, TextureAtlasSprite particle) {
			this.parent = model;
			this.particle = particle;
		}
		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
			return parent.getQuads(state, side, rand);
		}
		@Override
		public boolean isAmbientOcclusion() {
			return parent.isAmbientOcclusion();
		}
		@Override
		public boolean isGui3d() {
			return parent.isGui3d();
		}
		@Override
		public boolean isBuiltInRenderer() {
			return parent.isBuiltInRenderer();
		}
		@Override
		public TextureAtlasSprite getParticleTexture() {
			return particle;
		}
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return parent.getItemCameraTransforms();
		}
		@Override
		public ItemOverrideList getOverrides() {
			return parent.getOverrides();
		}
		@Override
		public IBakedModel getBakedModel() {
			return parent.getBakedModel();
		}
		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
			return parent.getQuads(state, side, rand, extraData);
		}
		@Override
		public boolean isAmbientOcclusion(BlockState state) {
			return parent.isAmbientOcclusion(state);
		}
		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			return parent.handlePerspective(cameraTransformType);
		}
		@Override
		public IModelData getModelData(IEnviromentBlockReader world, BlockPos pos, BlockState state, IModelData tileData) {
			return parent.getModelData(world, pos, state, tileData);
		}
		@Override
		public TextureAtlasSprite getParticleTexture(IModelData data) {
			return particle;
		}

	}
}
