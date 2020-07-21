package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.Matrix3f;

import com.tom.mcobj.Access.MXA;

@Mixin(Matrix3f.class)
public class Mat3Mixin implements MXA {
	@Shadow protected float a00;
	@Shadow protected float a01;
	@Shadow protected float a02;
	@Shadow protected float a10;
	@Shadow protected float a11;
	@Shadow protected float a12;
	@Shadow protected float a20;
	@Shadow protected float a21;
	@Shadow protected float a22;

	@Override
	public void mcobj_setScale(float x, float y, float z) {
		a00 = x;
		a11 = y;
		a22 = z;
	}

	@Override
	public void mcobj_load(float[] v) {
		this.a00 = v[ 0];
		this.a01 = v[ 1];
		this.a02 = v[ 2];

		this.a10 = v[ 3];
		this.a11 = v[ 4];
		this.a12 = v[ 5];

		this.a20 = v[ 6];
		this.a21 = v[ 7];
		this.a22 = v[ 8];
	}
}
