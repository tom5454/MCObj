package com.tom.mcobj.forge;

import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

import com.google.gson.Gson;

import com.tom.mcobj.Access;
import com.tom.mcobj.forge.Fabric.IModelState;

/*
 * Interpolation-friendly affine transformation.
 * If created with matrix, should successfully decompose it to a composition
 * of easily interpolatable transformations (translation, first rotation, scale
 * (with generally speaking different factors for each axis) and second rotation.
 * If the input matrix is a composition of translation, rotation and scale (in
 * any order), then the interpolation of the derived primitive transformations
 * should result in the same transformation as the interpolation of the originals.
 * Decomposition happens lazily (and is hopefully fast enough), so performance
 * should be comparable to using Matrix4f directly.
 * Immutable.
 *
 * Dumb impl for OBJModel
 */
@SuppressWarnings("unchecked")
public class TRSRTransformation implements IModelState {
	private Matrix4f mat;
	private Matrix3f normalTransform;
	private static final Map<ModelRotation, TRSRTransformation> rotations = new EnumMap<>(ModelRotation.class);
	private static final TRSRTransformation identity;
	private AffineTransformation rot3;

	static {
		Gson gson = new Gson();
		try (InputStreamReader rd = new InputStreamReader(TRSRTransformation.class.getResourceAsStream("/com/tom/mcobj/trsrtransformation.json"))){
			Map<String, Object> json = (Map<String, Object>) gson.fromJson(rd, Object.class);
			identity = new TRSRTransformation((Map<String, Object>) json.get("identity"));
			List<Map<String, Object>> mrs = (List<Map<String, Object>>) json.get("ModelRotations");
			for(ModelRotation mr : ModelRotation.values()) {
				rotations.put(mr, new TRSRTransformation(mrs.get(mr.ordinal())));
			}
		} catch (Exception e) {
			throw new RuntimeException("Mod jar is corrupted: invalid trsrtransformation.json file", e);
		}
	}

	public Vector4f transformPosition(Vector4f pos) {
		Vector4f v = new Vector4f(pos.getX() - 0.5f, pos.getY() - 0.5f, pos.getZ() - 0.5f, Access.Fw(pos) - 0.5f);
		v.transform(rot3.getMatrix());
		return new Vector4f(v.getX() + 0.5f, v.getY() + 0.5f, v.getZ() + 0.5f, Access.Fw(v) + 0.5f);
	}
	public static TRSRTransformation from(ModelRotation rotation) {
		return rotations.get(rotation);
	}

	private TRSRTransformation(Map<String, Object> elems) {
		List<Object> matA = (List<Object>) elems.get("mat");
		this.mat = new Matrix4f();
		Access.load(mat, toArray(matA, 16));
		matA = (List<Object>) elems.get("nmat");
		this.normalTransform = new Matrix3f();
		Access.load(normalTransform, toArray(matA, 9));
	}

	public TRSRTransformation(AffineTransformation rotation) {
		this.rot3 = rotation;
	}
	private static float[] toArray(List<Object> lst, int s) {
		float[] a = new float[s];
		for (int i = 0; i < a.length; i++) {
			a[i] = ((Number)lst.get(i)).floatValue();
		}
		return a;
	}

	public static TRSRTransformation identity()
	{
		return identity;
	}

	public void transformNormal(Vector3f normal) {
		//normalTransform.transform(normal);
		//normal.normalize();
	}

	@Override
	public Optional<TRSRTransformation> apply(Optional<? extends Object> part) {
		if(part.isPresent())
		{
			return Optional.empty();
		}
		return Optional.of(this);
	}

	public Direction rotateTransform(Direction facing) {
		return Direction.transform(mat, facing);
	}
}