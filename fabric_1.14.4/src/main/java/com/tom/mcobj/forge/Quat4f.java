package com.tom.mcobj.forge;

public class Quat4f extends Vector4f {

	public Quat4f(float x, float y, float z, float w) {
		float mag;
		mag = (float)(1.0/Math.sqrt( x*x + y*y + z*z + w*w ));
		this.x =  x*mag;
		this.y =  y*mag;
		this.z =  z*mag;
		this.w =  w*mag;
	}

	public Quat4f() {
	}

	@Override
	public Quat4f clone() {
		return new Quat4f(x, y, z, w);
	}

	public void mul(Quat4f q1) {
		float     x, y, w;

		w = this.w*q1.w - this.x*q1.x - this.y*q1.y - this.z*q1.z;
		x = this.w*q1.x + q1.w*this.x + this.y*q1.z - this.z*q1.y;
		y = this.w*q1.y + q1.w*this.y - this.x*q1.z + this.z*q1.x;
		this.z = this.w*q1.z + q1.w*this.z + this.x*q1.y - this.y*q1.x;
		this.w = w;
		this.x = x;
		this.y = y;
	}

	public void normalize() {
		float norm;

		norm = (this.x*this.x + this.y*this.y + this.z*this.z + this.w*this.w);

		if (norm > 0.0f) {
			norm = 1.0f / (float)Math.sqrt(norm);
			this.x *= norm;
			this.y *= norm;
			this.z *= norm;
			this.w *= norm;
		} else {
			this.x = (float) 0.0;
			this.y = (float) 0.0;
			this.z = (float) 0.0;
			this.w = (float) 0.0;
		}
	}

	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public void scale(float s) {
		this.x *= s;
		this.y *= s;
		this.z *= s;
		this.w *= s;
	}
}