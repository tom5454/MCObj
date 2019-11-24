package com.tom.mcobj;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.Material;
import net.minecraftforge.client.model.obj.OBJModel.MaterialLibrary;
import net.minecraftforge.client.model.obj.OBJModel.OBJBakedModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import com.google.common.collect.ImmutableMap;

public class BlockModelObj extends ModelBlock {
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
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
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
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			return PerspectiveMapWrapper.handlePerspective(this, mtr, cameraTransformType);
		}
	}
	private OBJModel parent;

	public BlockModelObj(OBJModel parent) {
		super(new ResourceLocation("block/block"), Collections.emptyList(), Collections.emptyMap(), true, false, ItemCameraTransforms.DEFAULT, Collections.emptyList());
		this.parent = parent;
	}
	public IBakedModel bake(IModelState state, final VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ModelBlock caller) {
		ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
		builder.put(ModelLoader.White.LOCATION.toString(), ModelLoader.White.INSTANCE);
		TextureAtlasSprite missing = spriteGetter.apply(new ResourceLocation("missingno"));
		MaterialLibrary ml = parent.getMatLib();
		for (String mat : ml.getMaterialNames()) {
			Material m = ml.getMaterial(mat);
			ResourceLocation rl = new ResourceLocation(caller.resolveTextureName(m.getTexture().getPath()));
			TextureAtlasSprite tex = spriteGetter.apply(rl);
			if(tex == missing && !rl.getResourcePath().equals("missingno")){
				//missingTexs.add(rl);
			}
			builder.put(mat, tex);
		}
		builder.put("missingno", missing);
		TextureAtlasSprite particle;
		ResourceLocation rl = new ResourceLocation(caller.resolveTextureName("particle"));
		particle = spriteGetter.apply(rl);
		if(particle == missing && !rl.getResourcePath().equals("missingno")){
			//missingTexs.add(rl);
		}
		OBJBakedModel baked = parent.new OBJBakedModel(parent, state, format, builder.build());
		return new BlockModelObj.BakedObjModel(baked, particle, caller.getAllTransforms());
	}
	@Override
	public List<BlockPart> getElements() {
		return Remap.EMPTY;
	}
	public OBJModel getObjModel(){
		return parent;
	}
}