package com.tom.mcobj.forge;

import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.client.render.model.ModelRotation;

import com.google.gson.Gson;

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

	static {
		Gson gson = new Gson();
		try (InputStreamReader rd = new InputStreamReader(TRSRTransformation.class.getResourceAsStream("/com/tom/mcobj/trsrtransformation.json"))){
			Map<String, Object> json = (Map<String, Object>) gson.fromJson(rd, Object.class);
			identity = new TRSRTransformation((Map<String, Object>) json.get("identity"), "identity");
			List<Map<String, Object>> mrs = (List<Map<String, Object>>) json.get("ModelRotations");
			for(ModelRotation mr : ModelRotation.values()) {
				rotations.put(mr, new TRSRTransformation(mrs.get(mr.ordinal()), mr.name()));
			}
		} catch (Exception e) {
			throw new RuntimeException("Mod jar is corrupted: invalid trsrtransformation.json file", e);
		}
	}

	public void transformPosition(Vector4f pos) {
		mat.transform(pos);
	}
	public static TRSRTransformation from(ModelRotation rotation) {
		return rotations.get(rotation);
	}

	private TRSRTransformation(Map<String, Object> elems, String info) {
		List<Object> matA = (List<Object>) elems.get("mat");
		this.mat = new Matrix4f(toArray(matA, 16));
		matA = (List<Object>) elems.get("nmat");
		this.normalTransform = new Matrix3f(toArray(matA, 9));
		//System.out.println("[TRSR " + info + "] " + normalTransform);
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
		normalTransform.transform(normal);
		normal.normalize();
	}

	@Override
	public Optional<TRSRTransformation> apply(Optional<? extends Object> part) {
		if(part.isPresent())
		{
			return Optional.empty();
		}
		return Optional.of(this);
	}
}