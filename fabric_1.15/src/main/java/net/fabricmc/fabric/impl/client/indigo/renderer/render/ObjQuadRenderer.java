package net.fabricmc.fabric.impl.client.indigo.renderer.render;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl.Value;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractQuadRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ChunkRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.CompatibilityHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainFallbackConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

import com.tom.mcobj.forge.BakedQuadBuilder.NBakedQuad;

public class ObjQuadRenderer extends AbstractQuadRenderer implements Consumer<BakedModel> {
	private static Value MATERIAL_FLAT = (Value) IndigoRenderer.INSTANCE.materialFinder().disableAo(0, true).find();
	private static Value MATERIAL_SHADED = (Value) IndigoRenderer.INSTANCE.materialFinder().find();

	private final int[] editorBuffer = new int[EncodingFormat.TOTAL_STRIDE];
	private final ChunkRenderInfo chunkInfo;
	private TerrainFallbackConsumer tfbc;

	public ObjQuadRenderer(ChunkRenderInfo chunkInfo, TerrainFallbackConsumer tfbc) {
		super(tfbc.blockInfo, chunkInfo::getInitializedBuffer, tfbc.aoCalc, tfbc.transform);
		this.chunkInfo = chunkInfo;
		this.tfbc = tfbc;
	}

	private final MQV editorQuad = new MQV();

	private class MQV extends MutableQuadViewImpl {
		public float x, y, z;
		{
			data = editorBuffer;
			material(MATERIAL_SHADED);
		}
		@Override
		public QuadEmitter emit() {
			// should not be called
			throw new UnsupportedOperationException("Fallback consumer does not support .emit()");
		}

		@Override
		public Vector3f copyNormal(int vertexIndex, Vector3f target) {
			if(hasNormal(vertexIndex)) {
				if(target == null) {
					target = new Vector3f();
				}
				//final int normal = data[vertexStart() + vertexIndex];
				target.set(x, y, z);
				return target;
			} else {
				return null;
			}
		}
		public void normal(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	@Override
	public void accept(BakedModel model) {
		final Supplier<Random> random = blockInfo.randomSupplier;
		final Value defaultMaterial = blockInfo.defaultAo && model.useAmbientOcclusion()
				? MATERIAL_SHADED : MATERIAL_FLAT;
		final BlockState blockState = blockInfo.blockState;
		for(int i = 0; i < 6; i++) {
			Direction face = ModelHelper.faceFromIndex(i);
			List<BakedQuad> quads = model.getQuads(blockState, face, random.get());
			final int count = quads.size();
			if(count != 0 && blockInfo.shouldDrawFace(face)) {
				for(int j = 0; j < count; j++) {
					BakedQuad q = quads.get(j);
					renderQuad((NBakedQuad) q, face, defaultMaterial);
				}
			}
		}

		List<BakedQuad> quads = model.getQuads(blockState, null, random.get());
		final int count = quads.size();
		if(count != 0) {
			for(int j = 0; j < count; j++) {
				BakedQuad q = quads.get(j);
				renderQuad((NBakedQuad) q, null, defaultMaterial);
			}
		}
	}

	private void renderQuad(NBakedQuad quad, Direction cullFace, Value defaultMaterial) {
		final int[] vertexData = quad.getVertexData();

		if(!CompatibilityHelper.canRender(vertexData)) {
			return;
		}

		final MQV editorQuad = this.editorQuad;
		editorQuad.normal(quad.x, quad.y, quad.z);
		System.arraycopy(vertexData, 0, editorBuffer, EncodingFormat.HEADER_STRIDE, EncodingFormat.QUAD_STRIDE);
		editorQuad.cullFace(cullFace);
		final Direction lightFace = quad.getFace();
		editorQuad.lightFace(lightFace);
		editorQuad.nominalFace(lightFace);
		editorQuad.colorIndex(quad.getColorIndex());
		editorQuad.material(defaultMaterial);

		if(!transform.transform(editorQuad)) {
			return;
		}

		if (editorQuad.material().disableAo(0)) {
			// needs to happen before offsets are applied
			editorQuad.invalidateShape();
			aoCalc.compute(editorQuad, true);
			tesselateSmooth(editorQuad, blockInfo.defaultLayer, editorQuad.colorIndex());
		} else {
			// vanilla compatibility hack
			// For flat lighting, cull face drives everything and light face is ignored.
			if (cullFace == null) {
				editorQuad.invalidateShape();
				// Can't rely on lazy computation in tesselateFlat() because needs to happen before offsets are applied
				editorQuad.geometryFlags();
			} else {
				editorQuad.geometryFlags(GeometryHelper.LIGHT_FACE_FLAG);
				editorQuad.lightFace(cullFace);
			}

			tesselateFlat(editorQuad, blockInfo.defaultLayer, editorQuad.colorIndex());
		}
	}

	@Override
	protected Matrix4f matrix() {
		return tfbc.matrix();
	}

	@Override
	protected Matrix3f normalMatrix() {
		return tfbc.normalMatrix();
	}

	@Override
	protected int overlay() {
		return tfbc.overlay();
	}
}
