package com.tom.mcobj;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import com.tom.mcobj.EntityModelLoader.Model;
import com.tom.mcobj.forge.VertexC;

public class ObjModel extends Model {
	private Map<String, List<ObjModel.Face>> faces;
	public ObjModel(BufferedReader bufferedReader) throws IOException {
		String ln;
		List<Vec3d> vec = new ArrayList<>();
		List<Vec3d> vns = new ArrayList<>();
		List<Vec2f> tex = new ArrayList<>();
		faces = new HashMap<>();
		List<ObjModel.Face> curr = null;
		while((ln = bufferedReader.readLine()) != null){
			String[] sp = ln.split(" ");
			switch (sp[0]) {
			case "v":
				try {
					vec.add(new Vec3d(Double.parseDouble(sp[1]), Double.parseDouble(sp[2]), Double.parseDouble(sp[3])));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					throw new IOException("", e);
				}
				break;
			case "vt":
				try {
					tex.add(new Vec2f(Float.parseFloat(sp[1]), Float.parseFloat(sp[2])));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					throw new IOException("", e);
				}
				break;

			case "vn":
				try {
					vns.add(new Vec3d(Double.parseDouble(sp[1]), Double.parseDouble(sp[2]), Double.parseDouble(sp[3])));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					throw new IOException("", e);
				}
				break;
			case "o":
			case "g":
			{
				String g = sp[1];
				int i = g.indexOf(':');
				if(i > 0)g = g.substring(0, i);
				curr = faces.computeIfAbsent(g, e -> new ArrayList<>());
			}
			break;
			case "f":
				String[] v1 = sp[1].split("/");
				String[] v2 = sp[2].split("/");
				String[] v3 = sp[3].split("/");
				ObjModel.Face f = new Face();
				try {
					f.vecs[0] = getParsedValue(vec, v1[0]);
					f.vecs[1] = getParsedValue(vec, v2[0]);
					f.vecs[2] = getParsedValue(vec, v3[0]);

					f.uvs[0] = getParsedValue(tex, v1[1]);
					f.uvs[1] = getParsedValue(tex, v2[1]);
					f.uvs[2] = getParsedValue(tex, v3[1]);

					f.vns[0] = getParsedValue(vns, v1[2]);
					f.vns[1] = getParsedValue(vns, v2[2]);
					f.vns[2] = getParsedValue(vns, v3[2]);
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					throw new IOException("", e);
				}
				if(curr == null){
					curr = new ArrayList<>();
					faces.put("default", curr);
				}
				curr.add(f);
				break;

			default:
				break;
			}
		}
	}

	private static <T> T getParsedValue(List<T> list, String toParse) throws NumberFormatException, IndexOutOfBoundsException {
		if(toParse.isEmpty())return null;
		return list.get(Integer.parseInt(toParse) - 1);
	}

	private static class Face {
		Vec3d[] vecs = new Vec3d[3];
		Vec3d[] vns = new Vec3d[3];
		Vec2f[] uvs = new Vec2f[3];
	}

	@Override
	public boolean renderNormal(String name, VertexC bb){
		List<ObjModel.Face> l = faces.get(name);
		if(l == null)return false;
		//bb.begin(GL11.GL_TRIANGLES, VertexFormats.POSITION_UV);
		UnaryOperator<Vec2f> remap = uv -> new Vec2f(uv.x, 1-uv.y);
		l.forEach(f -> {
			addVert(bb, f, 0, remap);
			addVert(bb, f, 1, remap);
			addVert(bb, f, 2, remap);
			bb.end();//Make quad
		});
		//Tessellator.getInstance().draw();
		return true;
	}
	@Override
	public void renderReUV(VertexC bb, UnaryOperator<Vec2f> remap){
		//bb.begin(GL11.GL_TRIANGLES, VertexFormats.POSITION_UV);
		faces.values().stream().flatMap(List::stream).forEach(f -> {
			addVert(bb, f, 0, remap);
			addVert(bb, f, 1, remap);
			addVert(bb, f, 2, remap);
			bb.end();//Make quad
		});
	}
	private static void addVert(VertexC bb, ObjModel.Face f, int ind, UnaryOperator<Vec2f> remap) {
		Vec2f uv = remap != null ? remap.apply(f.uvs[ind]) : f.uvs[ind];
		Vec3d v = new Vec3d(f.vecs[ind].x, 1-f.vecs[ind].y, 1-f.vecs[ind].z);
		Vec3d n = new Vec3d(f.vns[ind].x, f.vns[ind].y, 1-f.vns[ind].z);
		//Vec3d n = f.vns[ind];
		bb.pos(v.x, v.y, v.z).tex(uv.x, uv.y).
		normal((float) n.x, (float) n.y, (float) n.z).
		end();
	}
}