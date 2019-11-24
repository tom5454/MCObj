package com.tom.mcobj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import com.mojang.blaze3d.platform.GlStateManager;

import com.tom.mcobj.EntityModelLoader.Model;

public class JsonModel extends Model {
	private Map<String, List<JsonModel.Element>> elements;
	@SuppressWarnings("unchecked")
	public JsonModel(Map<String, Object> modelJson) {
		List<Map<String, Object>> elements = (List<Map<String, Object>>) modelJson.get("elements");
		this.elements = new HashMap<>();
		elements.stream().map(JsonModel.Element::new).forEach(e -> {
			String n = e.name;
			int i = n.indexOf(':');
			if(i > 0)n = n.substring(0, i);
			this.elements.computeIfAbsent(n, k -> new ArrayList<>()).add(e);
		});
	}

	@Override
	public boolean renderNormal(String name, BufferBuilder bb) {
		List<JsonModel.Element> l = elements.get(name);
		if(l == null)return false;
		double s = 1/16f;
		//double s = 20;
		GlStateManager.scaled(-s, -s, s);
		l.forEach(e -> {
			GlStateManager.pushMatrix();
			GlStateManager.translated(e.rotOrigin.x, e.rotOrigin.y, e.rotOrigin.z);
			GlStateManager.rotated(e.angle, e.axis == Axis.X ? 1 : 0, e.axis == Axis.Y ? 1 : 0, e.axis == Axis.Z ? 1 : 0);
			GlStateManager.translated(-16, -8, -16);
			bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Vec3d size = e.to.subtract(e.from);
			e.faces.entrySet().forEach(fe -> {
				JsonModel.FaceDirection fd = fe.getKey();
				Element.Face f = fe.getValue();
				float us = f.v.x - f.u.x;
				float vs = f.v.y - f.u.y;
				for(int i = 0;i<4;i++){
					Vec3d v = fd.v[i];
					Vec2f t = fd.t[i];
					System.out.println(new Vec3d(v.x * size.x + e.from.x,
							v.y * size.y + e.from.y,
							v.z * size.z + e.from.z) + ", " + i + ", " + v + ", (" + e.from + " -- " + e.to + ")");
					bb.
					pos(    v.x * size.x + e.from.x,
							v.y * size.y + e.from.y,
							v.z * size.z + e.from.z).
					tex(    (t.x * us + f.u.x) / 16,
							(t.y * vs + f.u.y) / 16).
					/*tex(    t.x,
							t.y).*/
					//color(255, 255, 255, 255).
					endVertex();
				}
			});
			Tessellator.getInstance().draw();
			GlStateManager.popMatrix();
		});
		/*GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		int x = 1, y = 1, w = 10, h = 10, zDepth = 0;
		vertexbuffer.pos(x + w, y, zDepth).endVertex();
		vertexbuffer.pos(x, y, zDepth).endVertex();
		vertexbuffer.pos(x, y + h, zDepth).endVertex();
		vertexbuffer.pos(x + w, y + h, zDepth).endVertex();
		// renderer.finishDrawing();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();*/
		return true;
	}

	@Override
	public void renderReUV(BufferBuilder bb, UnaryOperator<Vec2f> remap) {
	}

	public static class Element {
		private static final Vec3d RO = new Vec3d(8, 8, 8);
		private String name;
		private Vec3d from, to, rotOrigin;
		private Map<JsonModel.FaceDirection, Element.Face> faces;
		private double angle;
		private Axis axis;
		@SuppressWarnings("unchecked")
		public Element(Map<String, Object> json) {
			name = (String) json.getOrDefault("name", "NULL");
			List<Object> l = (List<Object>) json.get("from");
			from = new Vec3d((double) l.get(0), (double) l.get(1), (double) l.get(2));
			l = (List<Object>) json.get("to");
			to = new Vec3d((double) l.get(0), (double) l.get(1), (double) l.get(2));
			Map<String, Object> faces = (Map<String, Object>) json.get("faces");
			this.faces = new HashMap<>();
			for(JsonModel.FaceDirection d : FaceDirection.values()){
				Object o = faces.get(d.name().toLowerCase());
				if(o != null){
					this.faces.put(d, new Face((Map<String, Object>) o));
				}
			}
			if(json.containsKey("rotation")) {
				Map<String, Object> r = (Map<String, Object>) json.get("rotation");
				angle = (double) r.get("angle");
				axis = Axis.byName((String) r.get("axis"));
				l = (List<Object>) r.get("origin");
				rotOrigin = new Vec3d((double) l.get(0), (double) l.get(1), (double) l.get(2));
			}else{
				rotOrigin = RO;
			}
		}

		private static class Face {
			private Vec2f u, v;
			@SuppressWarnings("unchecked")
			public Face(Map<String, Object> json) {
				List<Object> uv = (List<Object>) json.get("uv");
				u = new Vec2f(((Double) uv.get(0)).floatValue(), ((Double) uv.get(1)).floatValue());
				v = new Vec2f(((Double) uv.get(2)).floatValue(), ((Double) uv.get(3)).floatValue());
			}
		}
	}
	private static final Vec3d OOO = Vec3d.ZERO;
	private static final Vec3d OOI = new Vec3d(0, 0, 1);
	private static final Vec3d OIO = new Vec3d(0, 1, 0);
	private static final Vec3d OII = new Vec3d(0, 1, 1);
	private static final Vec3d IOO = new Vec3d(1, 0, 0);
	private static final Vec3d IOI = new Vec3d(1, 0, 1);
	private static final Vec3d IIO = new Vec3d(1, 1, 0);
	private static final Vec3d III = new Vec3d(1, 1, 1);
	private static final Vec2f OO = Vec2f.ZERO;
	private static final Vec2f OI = Vec2f.UNIT_Y;
	private static final Vec2f IO = Vec2f.UNIT_X;
	private static final Vec2f II = Vec2f.ONE;
	private static enum FaceDirection {
		//    Vertex Pos            Tex Pos
		DOWN (IOO, IOI, OOI, OOO,   IO, II, OI, OO),
		UP   (III, IIO, OIO, OII,   II, IO, OO, OI),
		NORTH(OOO, OOI, OII, OIO,   OI, II, IO, OO),
		SOUTH(IOI, IOO, IIO, III,   OI, II, IO, OO),
		WEST (IOO, OOO, OIO, IIO,   OI, II, IO, OO),
		EAST (OOI, IOI, III, OII,   OI, II, IO, OO)
		;
		private Vec3d[] v;
		private Vec2f[] t;
		private FaceDirection(Vec3d av, Vec3d bv, Vec3d cv, Vec3d dv, Vec2f at, Vec2f bt, Vec2f ct, Vec2f dt) {
			v = new Vec3d[]{av, bv, cv, dv};
			t = new Vec2f[]{at, bt, ct, dt};
		}
	}
}