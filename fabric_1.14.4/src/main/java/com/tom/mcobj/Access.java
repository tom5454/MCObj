package com.tom.mcobj;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.indigo.renderer.render.ObjQuadRenderer;
import net.minecraft.client.model.Box;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public class Access {
	public static void FcompiledS(Cuboid model, boolean v){
		((CBA)model).mcobj_setCompiled(v);
	}
	public static void FdisplayListS(Cuboid model, int v){
		((CBA)model).mcobj_setDisplayList(v);
	}
	public static int FdisplayList(Cuboid model){
		return ((CBA)model).mcobj_getDisplayList();
	}
	public static Model FparentModel(Cuboid model){
		return ((CBA)model).mcobj_getParentModel();
	}
	public static Cuboid FparentRenderer(Cuboid model){
		return ((CBA)model).mcobj_getParentRenderer();
	}
	public static int FtexU(Box box){
		return ((BXA)box).mcobj_getTexU();
	}
	public static int FtexV(Box box){
		return ((BXA)box).mcobj_getTexV();
	}
	public static float Fdelta(Box box){
		return ((BXA)box).mcobj_getDelta();
	}
	public static Identifier FcurrTex(TextureManager mngr){
		return ((TMA)mngr).mcobj_getBoundTexture();
	}
	public static Consumer<BakedModel> FobjRenderer(RenderContext ctx) {
		return ctx instanceof RCA ? ((RCA)ctx).mcobj_objRenderer() : ctx.fallbackConsumer();
	}
	public static JsonUnbakedModel Fparent(JsonUnbakedModel model) {
		return ((BMA)model).mcobj_getParent();
	}

	public static interface BMA {
		public JsonUnbakedModel mcobj_getParent();
	}
	public static interface CBA {
		void   mcobj_setCompiled(boolean comp);
		void   mcobj_setDisplayList(int dl);
		int    mcobj_getDisplayList();
		Model  mcobj_getParentModel();
		Cuboid mcobj_getParentRenderer();
		void   mcobj_setParentRenderer(Object parent);
	}
	public static interface BXA {
		int   mcobj_getTexU();
		int   mcobj_getTexV();
		float mcobj_getDelta();
	}
	public static interface TMA {
		Identifier mcobj_getBoundTexture();
	}
	public static interface RCA {
		ObjQuadRenderer mcobj_objRenderer();
	}
	public static interface MEA {
		FabricBakedModel mcobj_getModel();
	}
}
