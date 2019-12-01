package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.Box;
import net.minecraft.client.model.Cuboid;

import com.tom.mcobj.Access.BXA;

@Mixin(Box.class)
public class BoxMixin implements BXA {
	public int mcobj_texU, mcobj_texV;
	public float mcobj_delta;

	@Inject(method = "<init>(Lnet/minecraft/client/model/Cuboid;IIFFFIIIFZ)V", at = @At("RETURN"))
	public void onInit(Cuboid cb, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror, CallbackInfo cbi) {
		mcobj_texU = texU;
		mcobj_texV = texV;
		mcobj_delta = delta;
	}

	@Override
	public int mcobj_getTexU() {
		return mcobj_texU;
	}

	@Override
	public int mcobj_getTexV() {
		return mcobj_texV;
	}

	@Override
	public float mcobj_getDelta() {
		return mcobj_delta;
	}
}
