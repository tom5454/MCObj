package com.tom.mcobj;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

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
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.Material;
import net.minecraftforge.client.model.obj.OBJModel.MaterialLibrary;
import net.minecraftforge.client.model.obj.OBJModel.OBJBakedModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.model.animation.IClip;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("deprecation")
public class BlockModelObj extends BlockModel {
	public static class BakedObjModel implements IBakedModel {
		private IBakedModel parent;
		private TextureAtlasSprite particle;
		private ItemCameraTransforms tr;
		private ImmutableMap<TransformType, TRSRTransformation> mtr;
		public BakedObjModel(OBJBakedModel model, TextureAtlasSprite particle, ItemCameraTransforms tr) {
			this.parent = model;
			this.particle = particle;
			this.tr = tr;
			this.mtr = PerspectiveMapWrapper.getTransforms(tr);
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
			return tr;
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
			return PerspectiveMapWrapper.handlePerspective(this, mtr, cameraTransformType);
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
			MaterialLibrary ml = (MaterialLibrary) Remap.matLib.get(parent);
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
		return new BlockModelObj.BakedObjModel(baked, particle, caller.getAllTransforms());
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
		return Remap.EMPTY;
	}
	public OBJModel getObjModel(){
		return parent;
	}
}