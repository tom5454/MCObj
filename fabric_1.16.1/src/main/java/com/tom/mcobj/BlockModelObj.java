package com.tom.mcobj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import com.mojang.datafixers.util.Pair;

import com.google.common.collect.ImmutableMap;

import com.tom.mcobj.forge.BakedQuadBuilder.NBakedQuad;
import com.tom.mcobj.forge.OBJModel;
import com.tom.mcobj.forge.OBJModel.FMR;
import com.tom.mcobj.forge.OBJModel.Material;
import com.tom.mcobj.forge.OBJModel.MaterialLibrary;
import com.tom.mcobj.forge.OBJModel.OBJBakedModel;
import com.tom.mcobj.forge.TRSRTransformation;

public class BlockModelObj extends JsonUnbakedModel {
	public static class BakedObjModel implements BakedModel, FabricBakedModel {
		private BakedModel parent;
		private Sprite particle;
		private ModelTransformation tr;
		private Map<Direction, List<BakedQuad>> quads;
		private Optional<TRSRTransformation> cullTr;
		public BakedObjModel(OBJBakedModel model, Sprite particle, ModelTransformation tr, Optional<TRSRTransformation> cullTr) {
			this.parent = model;
			this.particle = particle;
			this.tr = tr;
			this.cullTr = cullTr;
		}
		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
			if(quads == null) {
				quads = new HashMap<>();
				quads.put(null, new ArrayList<>());
				for (Direction d : Direction.values()) {
					quads.put(d, new ArrayList<>());
				}
				List<BakedQuad> qs = parent.getQuads(state, null, rand);
				for (BakedQuad bakedQuad : qs) {
					Direction cull = ((NBakedQuad) bakedQuad).cull;
					quads.get(cull == null ? null : cullTr.map(trsr -> trsr.rotateTransform(cull)).orElse(cull)).add(bakedQuad);
				}
			}
			return quads.get(side);
		}
		@Override
		public boolean useAmbientOcclusion() {
			return parent.useAmbientOcclusion();
		}
		@Override
		public boolean hasDepth() {
			return parent.hasDepth();
		}
		@Override
		public boolean isBuiltin() {
			return parent.isBuiltin();
		}
		@Override
		public Sprite getSprite() {
			return particle;
		}
		@Override
		public ModelTransformation getTransformation() {
			return tr;
		}
		@Override
		public ModelOverrideList getOverrides() {
			return parent.getOverrides();
		}
		@Override
		public boolean isVanillaAdapter() {
			return false;
		}
		@Override
		public void emitBlockQuads(BlockRenderView blockView, BlockState blockState, BlockPos pos,
				Supplier<Random> random, RenderContext context) {
			Access.FobjRenderer(context).accept(parent);
		}

		@Override
		public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
			context.fallbackConsumer().accept(parent);
		}
		@Override
		public boolean isSideLit() {
			return parent.isSideLit();
		}
	}
	private OBJModel parent;

	public BlockModelObj(OBJModel parent) {
		super(new Identifier("block/block"), Collections.emptyList(), Collections.emptyMap(), true, GuiLight.field_21858, ModelTransformation.NONE, Collections.emptyList());
		this.parent = parent;
	}
	@Override
	public Collection<Identifier> getModelDependencies() {
		return parent.getModelDependencies();
	}
	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> modelGetter,
			Set<Pair<String, String>> missingTextureErrors) {
		return parent.getTextureDependencies(modelGetter, missingTextureErrors);
	}
	@Override
	public BakedModel bake(ModelLoader bakery, JsonUnbakedModel caller, Function<SpriteIdentifier, Sprite> spriteGetter,
			ModelBakeSettings sprite, Identifier identifier_1, boolean boolean_1) {
		ImmutableMap.Builder<String, Sprite> builder = ImmutableMap.builder();
		Sprite missing = spriteGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("missingno")));
		try {
			MaterialLibrary ml = parent.getMatLib();
			for (String mat : ml.getMaterialNames()) {
				Material m = ml.getMaterial(mat);
				SpriteIdentifier rl = caller.resolveSprite(m.getTexture().getPath());
				Sprite tex = spriteGetter.apply(rl);
				if(tex == missing && !rl.getTextureId().getPath().equals("missingno")){
					//missingTexs.add(rl);
				}
				builder.put(mat, tex);
			}
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		}
		builder.put("missingno", missing);
		Sprite particle;
		SpriteIdentifier rl = caller.resolveSprite("particle");
		particle = spriteGetter.apply(rl);
		if(particle == missing && !rl.getTextureId().getPath().equals("missingno")){
			//missingTexs.add(rl);
		}
		FMR fmr = new FMR(sprite.getRotation());
		OBJBakedModel baked = parent.new OBJBakedModel(parent, fmr, builder.build());
		return new BlockModelObj.BakedObjModel(baked, particle, caller.getTransformations(), fmr.apply(Optional.empty()));
	}

	@Override
	public List<ModelElement> getElements() {
		return Remap.EMPTY;
	}
	public OBJModel getObjModel(){
		return parent;
	}
}