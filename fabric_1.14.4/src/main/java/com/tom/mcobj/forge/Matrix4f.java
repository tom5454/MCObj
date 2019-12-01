package com.tom.mcobj.forge;

public class Matrix4f {
	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;

	public void transform(Vector4f vec) {
		float x,y,z;

		x = m00*vec.x + m01*vec.y
				+ m02*vec.z + m03*vec.w;
		y = m10*vec.x + m11*vec.y
				+ m12*vec.z + m13*vec.w;
		z = m20*vec.x + m21*vec.y
				+ m22*vec.z + m23*vec.w;
		vec.w = m30*vec.x + m31*vec.y
				+ m32*vec.z + m33*vec.w;
		vec.x = x;
		vec.y = y;
		vec.z = z;
	}

	public Matrix4f(float[] v) {
		this.m00 = v[ 0];
		this.m01 = v[ 1];
		this.m02 = v[ 2];
		this.m03 = v[ 3];

		this.m10 = v[ 4];
		this.m11 = v[ 5];
		this.m12 = v[ 6];
		this.m13 = v[ 7];

		this.m20 = v[ 8];
		this.m21 = v[ 9];
		this.m22 = v[10];
		this.m23 = v[11];

		this.m30 = v[12];
		this.m31 = v[13];
		this.m32 = v[14];
		this.m33 = v[15];
	}
}