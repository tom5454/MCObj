package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.util.math.Vector4f;

import com.tom.mcobj.Access.V4A;

@Mixin(Vector4f.class)
public class Vector4fMixin implements V4A {
	@Shadow
	private float w;

	@Override
	public float mcobj_getW() {
		return w;
	}
}
