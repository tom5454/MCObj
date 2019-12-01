package com.tom.mcobj.forge;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.MathHelper;

public class VertexC {
	private Matrix4f mat4, matrix4f_1;
	private Matrix3f mat3, matrix3f_1;
	private VertexConsumer bb;
	private float float_1, float_2, float_3, float_4;
	private int int_1, int_2;
	private Sprite sprite_1;

	private float u, v;
	private float x, y, z;
	private float r, g, b, a;
	private float nx, ny, nz;
	public VertexC(Matrix4f matrix4f_1, VertexConsumer bb, float float_1, int int_1, int int_2,
			Sprite sprite_1, float float_2, float float_3, float float_4) {
		this.mat4 = matrix4f_1;
		this.mat3 = new Matrix3f(matrix4f_1);
		this.float_1 = float_1;
		this.float_2 = float_2;
		this.float_3 = float_3;
		this.float_4 = float_4;
		this.bb = bb;
		this.int_1 = int_1;
		this.int_2 = int_2;
		this.sprite_1 = sprite_1;
		clear();
	}

	public VertexC pos(double x, double y, double z) {
		Vector4f vector4f_1 = new Vector4f((float) (x * float_1), (float) (y * float_1), (float) (z * float_1), 1.0F);
		vector4f_1.multiply(matrix4f_1);
		this.x = vector4f_1.getX();
		this.y = vector4f_1.getY();
		this.z = vector4f_1.getZ();
		return this;
	}

	public VertexC tex(float u, float v) {
		if (sprite_1 != null) {
			this.u = sprite_1.getU((u * 16.0F));
			this.v = sprite_1.getV((v * 16.0F));
		}else{
			this.u = u;
			this.v = v;
		}
		return this;
	}

	public void end() {
		bb.vertex(x, y, z).color(r, g, b, a).texture(u, v).defaultOverlay(int_2).
		light(int_1).normal(nx, ny, nz).next();
	}

	public void clear(){
		u = v = x = y = z = 0;
		r = g = b = a = 1;
		nx = ny = nz = 0;
	}

	public VertexC color(float r, float g, float b, float a) {
		this.r = float_2 * r;
		this.g = float_3 * g;
		this.b = float_4 * b;
		this.a = a;
		return this;
	}

	public VertexC normal(float x, float y, float z) {
		Vector3f v = new Vector3f(x, y, z);
		v.multiply(matrix3f_1);
		this.nx = v.getX();
		this.ny = v.getY();
		this.nz = v.getZ();
		return this;
	}
	public void push() {
		matrix4f_1 = new Matrix4f(mat4);
		matrix3f_1 = new Matrix3f(mat3);
	}

	public void translate(double double_1, double double_2, double double_3) {
		Matrix4f matrix4f_1 = new Matrix4f();
		matrix4f_1.loadIdentity();
		matrix4f_1.addToLastColumn(new Vector3f((float)double_1, (float)double_2, (float)double_3));

		this.matrix4f_1.multiply(matrix4f_1);
	}

	public void scale(float float_1, float float_2, float float_3) {
		Matrix4f matrix4f_1 = new Matrix4f();
		matrix4f_1.loadIdentity();
		matrix4f_1.set(0, 0, float_1);
		matrix4f_1.set(1, 1, float_2);
		matrix4f_1.set(2, 2, float_3);
		this.matrix4f_1.multiply(matrix4f_1);

		if (float_1 == float_2 && float_2 == float_3) {
			return;
		}


		float float_4 = MathHelper.fastInverseCbrt(float_1 * float_2 * float_3);

		Matrix3f matrix3f_1 = new Matrix3f();
		matrix3f_1.set(0, 0, float_4 / float_1);
		matrix3f_1.set(1, 1, float_4 / float_2);
		matrix3f_1.set(2, 2, float_4 / float_3);
		this.matrix3f_1.multiply(matrix3f_1);
	}
}
