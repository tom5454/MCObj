package com.tom.mcobj;

import java.util.function.Function;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

public class Access {
	public static IBakedModel NORMbake(BlockModel caller, ModelBakery bakery, BlockModel p_217644_2_, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format){
		//return caller.NORMbake(bakery, p_217644_2_, spriteGetter, sprite, format);
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static IUnbakedModel SUPERgetUnbakedModel(ModelLoader s, ResourceLocation modelLocation){
		//return s.SUPERgetUnbakedModel(modelLocation);
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static void NORMcompileDisplayList(RendererModel model, float scale){
		//model.NORMcompileDisplayList(scale);
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static void FcompiledS(RendererModel model, boolean v){
		//model.compiled = v;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static void FdisplayListS(RendererModel model, int v){
		//model.displayList = v;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static boolean Fcompiled(RendererModel model){
		//return model.compiled;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static int FdisplayList(RendererModel model){
		//return model.displayList;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static Model FparentModel(RendererModel model){
		//return model.mcobj_parentModel;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static RendererModel FparentRenderer(RendererModel model){
		//return model.mcobj_parentRenderer;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static int FtexU(ModelBox box){
		//return box.mcobj_texU;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static int FtexV(ModelBox box){
		//return box.mcobj_texV;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static float Fdelta(ModelBox box){
		//return box.mcobj_delta;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static ResourceLocation FcurrTex(TextureManager mngr){
		//return mngr.mcobj_currTexture;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static void FcullS(UnpackedBakedQuad.Builder ubqb, Direction cull){
		//ubqb.mcobj_cull = cull;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static Direction Fcull(UnpackedBakedQuad.Builder ubqb){
		//return ubqb.mcobj_cull;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static void FcullS(UnpackedBakedQuad ubq, Direction cull){
		//ubq.mcobj_cull = cull;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static Direction Fcull(UnpackedBakedQuad ubq){
		//return ubq.mcobj_cull;
		throw new RuntimeException("Code injection failed");//Code injected
	}
}
