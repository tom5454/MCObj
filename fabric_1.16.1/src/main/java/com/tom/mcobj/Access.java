package com.tom.mcobj;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ObjQuadRenderer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class Access {
	public static String Fname(ModelPart model){
		return ((CBA)model).mcobj_getName();
	}
	public static void FnameS(ModelPart model, String name){
		((CBA)model).mcobj_setName(name);
	}
	public static String FparentName(ModelPart model){
		return ((CBA)model).mcobj_getParentName();
	}
	public static void FparentNameS(ModelPart model, String name){
		((CBA)model).mcobj_setParentName(name);
	}
	public static Model FparentModel(ModelPart model){
		return ((CBA)model).mcobj_getParentModel();
	}
	public static ModelPart FparentRenderer(ModelPart model){
		return ((CBA)model).mcobj_getParentRenderer();
	}
	public static List<ModelPart> Fchildren(ModelPart model){
		return ((CBA)model).mcobj_getChildren();
	}
	public static int FtexU(Cuboid box){
		return ((BXA)box).mcobj_getTexU();
	}
	public static int FtexV(Cuboid box){
		return ((BXA)box).mcobj_getTexV();
	}
	public static Vector3f Fdelta(Cuboid box){
		return ((BXA)box).mcobj_getDelta();
	}
	public static Identifier FcurrTex(TextureManager mngr){
		return ((TMA)mngr).mcobj_getBoundTexture();
	}
	public static JsonUnbakedModel Fparent(JsonUnbakedModel model) {
		return ((BMA)model).mcobj_getParent();
	}
	public static float Fw(Vector4f v){
		return ((V4A)v).mcobj_getW();
	}
	public static Map<BlockEntityType<?>, BlockEntityRenderer<?>> Frenderers() {
		return ((BDA)BlockEntityRenderDispatcher.INSTANCE).mcobj_getRegistry();
	}
	public static Map<EntityType<?>, EntityRenderer<?>> Fregistry() {
		return ((EDA)MinecraftClient.getInstance().getEntityRenderManager()).mcobj_getRegistry();
	}
	public static Map<String, PlayerEntityRenderer> FrenderersE() {
		return ((EDA)MinecraftClient.getInstance().getEntityRenderManager()).mcobj_getRenderers();
	}
	public static Consumer<BakedModel> FobjRenderer(RenderContext ctx) {
		return ctx instanceof RCA ? ((RCA)ctx).mcobj_objRenderer() : ctx.fallbackConsumer();
	}
	public static void setScale(Matrix3f mat, float x, float y, float z) {
		((MXA)(Object)mat).mcobj_setScale(x, y, z);
	}
	public static void setScale(Matrix4f mat, float x, float y, float z) {
		((MXA)(Object)mat).mcobj_setScale(x, y, z);
	}
	public static void load(Matrix3f mat, float[] d) {
		((MXA)(Object)mat).mcobj_load(d);
	}
	public static void load(Matrix4f mat, float[] d) {
		((MXA)(Object)mat).mcobj_load(d);
	}

	public static interface BMA {
		public JsonUnbakedModel mcobj_getParent();
	}
	public static interface CBA {
		Model           mcobj_getParentModel();
		ModelPart       mcobj_getParentRenderer();
		void            mcobj_setParentRenderer(Object parent);
		String          mcobj_getName();
		void            mcobj_setName(String name);
		List<ModelPart> mcobj_getChildren();
		void            mcobj_setParentName(String name);
		String          mcobj_getParentName();
	}
	public static interface BXA {
		int      mcobj_getTexU();
		int      mcobj_getTexV();
		Vector3f mcobj_getDelta();
	}
	public static interface TMA {
		Identifier mcobj_getBoundTexture();
	}
	public static interface V4A {
		float mcobj_getW();
	}
	public static interface BDA {
		Map<BlockEntityType<?>, BlockEntityRenderer<?>> mcobj_getRegistry();
	}
	public static interface EDA {
		Map<EntityType<?>, EntityRenderer<?>> mcobj_getRegistry();
		Map<String, PlayerEntityRenderer>     mcobj_getRenderers();
	}
	public static interface RCA {
		ObjQuadRenderer mcobj_objRenderer();
	}
	public static interface MEA {
		FabricBakedModel mcobj_getModel();
	}
	public static interface MXA {
		void mcobj_setScale(float x, float y, float z);
		void mcobj_load(float[] data);
	}
	public static interface CDA {
		Direction mcobj_cull();
		void      mcobj_cull(Direction dir);
	}
}
