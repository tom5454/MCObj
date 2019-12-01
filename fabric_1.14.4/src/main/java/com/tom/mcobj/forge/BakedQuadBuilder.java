package com.tom.mcobj.forge;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class BakedQuadBuilder {
	private int[] bb = new int[28];
	private int elem;
	private int tint;
	private Sprite sprite_1;
	private Direction orientation;

	private float x, y, z;
	private float r, g, b, a;
	private float u, v;
	private float nx, ny, nz;

	public BakedQuad build() {
		return new NBakedQuad(bb, tint, orientation, sprite_1, nx, ny, nz);
	}

	public BakedQuadBuilder pos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public void next() {
		bb[elem + 0] = Float.floatToRawIntBits(x);
		bb[elem + 1] = Float.floatToRawIntBits(y);
		bb[elem + 2] = Float.floatToRawIntBits(z);
		bb[elem + 3] = 0xffffffff;//TODO color
		bb[elem + 4] = Float.floatToRawIntBits(u);
		bb[elem + 5] = Float.floatToRawIntBits(v);
		int cnx = ((byte) Math.round(nx * 127)) & 0xFF;
		int cny = ((byte) Math.round(ny * 127)) & 0xFF;
		int cnz = ((byte) Math.round(nz * 127)) & 0xFF;
		bb[elem + 6] = (cnx << 24 & 0xFF) | (cny << 16 & 0xFF) | (cnz << 8 & 0xFF);
		elem += 7;
	}

	public void clear(){
		u = v = x = y = z = 0;
		r = g = b = a = 1;
		nx = ny = nz = 0;
		bb = new int[28];
		elem = 0;
	}

	public BakedQuadBuilder tex(float u, float v) {
		this.u = u;
		this.v = v;
		return this;
	}

	public BakedQuadBuilder color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	public BakedQuadBuilder normal(float x, float y, float z) {
		this.nx = x;
		this.ny = y;
		this.nz = z;
		return this;
	}

	public void setTint(int tint) {
		this.tint = tint;
	}

	public void setSprite(Sprite sprite_1) {
		this.sprite_1 = sprite_1;
	}

	public void setOrientation(Direction orientation) {
		this.orientation = orientation;
	}

	public static class NBakedQuad extends BakedQuad {
		public float x, y, z;
		public NBakedQuad(int[] ints_1, int int_1, Direction direction_1, Sprite sprite_1, float x, float y, float z) {
			super(ints_1, int_1, direction_1, sprite_1);
			this.x = x;
			this.y = y;
			this.z = z;
		}

	}
}
