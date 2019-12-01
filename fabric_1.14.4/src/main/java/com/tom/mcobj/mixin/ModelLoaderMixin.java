package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

import com.tom.mcobj.Remap;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
	@Inject(method = "getOrLoadModel", at = @At("HEAD"), cancellable = true)
	public void onGetOrLoadModel(Identifier modelLocation, CallbackInfoReturnable<UnbakedModel> cir) {
		UnbakedModel rm = Remap.getUnbakedModel(modelLocation, this);
		if(rm != null)cir.setReturnValue(rm);
	}
}
