package com.tom.mcobj.forge;

import com.tom.mcobj.Access;

public class Vector4f {
	public float x, y, z, w;

	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f(net.minecraft.client.util.math.Vector4f mc) {
		this.x = mc.getX();
		this.y = mc.getY();
		this.z = mc.getZ();
		this.w = Access.Fw(mc);
	}

	public net.minecraft.client.util.math.Vector4f toMC() {
		return new net.minecraft.client.util.math.Vector4f(x, y, z, w);
	}

	public void setW(float w) {
		this.w = w;
	}

	public Vector4f(Vector4f pos) {
		this(pos.x, pos.y, pos.z, pos.w);
	}

	public Vector4f() {
	}
}