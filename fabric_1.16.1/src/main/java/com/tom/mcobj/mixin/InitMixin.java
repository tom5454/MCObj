package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;

import com.tom.mcobj.MCObjInit;

@Mixin(MinecraftClient.class)
public class InitMixin {

	@Inject(at = @At(value = "RETURN"), method = "<init>")
	private void mcobj_init(CallbackInfo info) {
		MCObjInit.modInstance.setup();
	}
}
