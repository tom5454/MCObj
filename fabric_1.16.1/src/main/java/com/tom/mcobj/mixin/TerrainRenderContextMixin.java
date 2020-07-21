package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.fabric.impl.client.indigo.renderer.render.ChunkRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ObjQuadRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainFallbackConsumer;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;

import com.tom.mcobj.Access.RCA;

@Mixin(TerrainRenderContext.class)
public abstract class TerrainRenderContextMixin implements RCA {
	@Shadow
	private ChunkRenderInfo chunkInfo;

	@Shadow
	private TerrainFallbackConsumer fallbackConsumer;

	private ObjQuadRenderer mcobj_objQuadRenderer;

	@Override
	public ObjQuadRenderer mcobj_objRenderer() {
		if(mcobj_objQuadRenderer == null)mcobj_objQuadRenderer = new ObjQuadRenderer(chunkInfo, fallbackConsumer);
		return mcobj_objQuadRenderer;
	}
}
