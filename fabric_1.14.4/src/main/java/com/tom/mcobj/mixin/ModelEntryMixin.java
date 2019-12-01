package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.render.model.BakedModel;

import com.tom.mcobj.Access.MEA;

@Mixin(targets = "net.minecraft.client.render.model.WeightedBakedModel$ModelEntry")
public class ModelEntryMixin implements MEA {
	@Shadow
	protected BakedModel model;

	@Override
	public FabricBakedModel mcobj_getModel() {
		return (FabricBakedModel) model;
	}
}
