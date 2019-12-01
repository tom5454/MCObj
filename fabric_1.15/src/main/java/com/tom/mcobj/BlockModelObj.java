package com.tom.mcobj;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import com.google.common.collect.ImmutableMap;

import com.tom.mcobj.forge.OBJModel;
import com.tom.mcobj.forge.OBJModel.FMR;
import com.tom.mcobj.forge.OBJModel.Material;
import com.tom.mcobj.forge.OBJModel.MaterialLibrary;
import com.tom.mcobj.forge.OBJModel.OBJBakedModel;

public class BlockModelObj extends JsonUnbakedModel {
	public static class BakedObjModel implements BakedModel, FabricBakedModel {
		private BakedModel parent;
		private Sprite particle;
		private ModelTransformation tr;
		public BakedObjModel(OBJBakedModel model, Sprite particle, ModelTransformation tr) {
			this.parent = model;
			this.particle = particle;
			this.tr = tr;
		}
		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
			return parent.getQuads(state, side, rand);
		}
		@Override
		public boolean useAmbientOcclusion() {
			return parent.useAmbientOcclusion();
		}
		@Override
		public boolean hasDepthInGui() {
			return parent.hasDepthInGui();
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
		public ModelItemPropertyOverrideList getItemPropertyOverrides() {
			return parent.getItemPropertyOverrides();
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
	}
	private OBJModel parent;

	public BlockModelObj(OBJModel parent) {
		super(new Identifier("block/block"), Collections.emptyList(), Collections.emptyMap(), true, false, ModelTransformation.NONE, Collections.emptyList());
		this.parent = parent;
	}
	@Override
	public Collection<Identifier> getModelDependencies() {
		return parent.getModelDependencies();
	}
	@Override
	public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		return parent.getTextureDependencies(modelGetter, missingTextureErrors);
	}
	@Override
	public BakedModel bake(ModelLoader p_217641_1_, Function<Identifier, Sprite> p_217641_2_, ModelBakeSettings p_217641_3_, Identifier id) {
		return parent.bake(p_217641_1_, p_217641_2_, p_217641_3_, id);
	}
	public BakedModel bake(ModelLoader bakery, Function<Identifier, Sprite> spriteGetter, ModelBakeSettings sprite, JsonUnbakedModel caller) {
		ImmutableMap.Builder<String, Sprite> builder = ImmutableMap.builder();
		Sprite missing = spriteGetter.apply(new Identifier("missingno"));
		try {
			MaterialLibrary ml = parent.getMatLib();
			for (String mat : ml.getMaterialNames()) {
				Material m = ml.getMaterial(mat);
				Identifier rl = new Identifier(caller.resolveTexture(m.getTexture().getPath()));
				Sprite tex = spriteGetter.apply(rl);
				if(tex == missing && !rl.getPath().equals("missingno")){
					//missingTexs.add(rl);
				}
				builder.put(mat, tex);
			}
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		}
		builder.put("missingno", missing);
		Sprite particle;
		Identifier rl = new Identifier(caller.resolveTexture("particle"));
		particle = spriteGetter.apply(rl);
		if(particle == missing && !rl.getPath().equals("missingno")){
			//missingTexs.add(rl);
		}
		OBJBakedModel baked = parent.new OBJBakedModel(parent, new FMR(sprite.getRotation()), builder.build());
		return new BlockModelObj.BakedObjModel(baked, particle, caller.getTransformations());
	}

	@Override
	public List<ModelElement> getElements() {
		return Remap.EMPTY;
	}
	public OBJModel getObjModel(){
		return parent;
	}
}