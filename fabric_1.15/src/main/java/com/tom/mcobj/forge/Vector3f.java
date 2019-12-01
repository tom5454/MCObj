package com.tom.mcobj.forge;

public class Vector3f {
	public float x, y, z;

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void normalize() {
		float norm;

		norm = (float)
				(1.0/Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z));
		this.x *= norm;
		this.y *= norm;
		this.z *= norm;
	}

	public void cross(Vector3f v1, Vector3f v2) {
		float x,y;

		x = v1.y*v2.z - v1.z*v2.y;
		y = v2.x*v1.z - v2.z*v1.x;
		this.z = v1.x*v2.y - v1.y*v2.x;
		this.x = x;
		this.y = y;
	}

	public void sub(Vector3f t1) {
		this.x -= t1.x;
		this.y -= t1.y;
		this.z -= t1.z;
	}

	public Vector3f(Vector3f pos) {
		this(pos.x, pos.y, pos.z);
	}

	public final void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f() {
	}
}