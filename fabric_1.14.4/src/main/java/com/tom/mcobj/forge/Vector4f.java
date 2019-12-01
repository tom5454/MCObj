package com.tom.mcobj.forge;

public class Vector4f {
	public float x, y, z, w;

	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
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