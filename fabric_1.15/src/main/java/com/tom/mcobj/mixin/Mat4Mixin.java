package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.util.math.Matrix4f;

import com.tom.mcobj.Access.MXA;

@Mixin(Matrix4f.class)
public class Mat4Mixin implements MXA {
	@Shadow protected float a00;
	@Shadow protected float a01;
	@Shadow protected float a02;
	@Shadow protected float a03;
	@Shadow protected float a10;
	@Shadow protected float a11;
	@Shadow protected float a12;
	@Shadow protected float a13;
	@Shadow protected float a20;
	@Shadow protected float a21;
	@Shadow protected float a22;
	@Shadow protected float a23;
	@Shadow protected float a30;
	@Shadow protected float a31;
	@Shadow protected float a32;
	@Shadow protected float a33;

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
		this.a03 = v[ 3];

		this.a10 = v[ 4];
		this.a11 = v[ 5];
		this.a12 = v[ 6];
		this.a13 = v[ 7];

		this.a20 = v[ 8];
		this.a21 = v[ 9];
		this.a22 = v[10];
		this.a23 = v[11];

		this.a30 = v[12];
		this.a31 = v[13];
		this.a32 = v[14];
		this.a33 = v[15];
	}
}
