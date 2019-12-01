package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.util.math.Vector3f;

import com.tom.mcobj.Access.BXA;

@Mixin(Cuboid.class)
public class BoxMixin implements BXA {
	public int mcobj_texU, mcobj_texV;
	public Vector3f mcobj_delta;

	@Inject(method = "<init>(IIFFFFFFFFFZFF)V", at = @At("RETURN"))
	public void onInit(int iu, int iv, float minX, float minY, float minZ,
			float xs, float ys, float zs, float dx, float dy, float dz,
			boolean boolean_1, float u, float v, CallbackInfo info) {
		mcobj_texU = iu;
		mcobj_texV = iv;
		mcobj_delta = new Vector3f(dx, dy, dz);
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
	public Vector3f mcobj_getDelta() {
		return mcobj_delta;
	}
}
