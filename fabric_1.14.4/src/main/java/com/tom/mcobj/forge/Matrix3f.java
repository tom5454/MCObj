package com.tom.mcobj.forge;

public class Matrix3f {
	public float m00, m01, m02;
	public float m10, m11, m12;
	public float m20, m21, m22;

	public Matrix3f(float[] v) {
		this.m00 = v[ 0];
		this.m01 = v[ 1];
		this.m02 = v[ 2];

		this.m10 = v[ 3];
		this.m11 = v[ 4];
		this.m12 = v[ 5];

		this.m20 = v[ 6];
		this.m21 = v[ 7];
		this.m22 = v[ 8];

	}

	public void transform(Vector3f t) {
		float x,y,z;
		x = m00* t.x + m01*t.y + m02*t.z;
		y = m10* t.x + m11*t.y + m12*t.z;
		z = m20* t.x + m21*t.y + m22*t.z;
		t.set(x,y,z);
	}

	@Override
	public String toString() {
		return
				this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" +
				this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" +
				this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
	}
}