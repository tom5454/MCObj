package com.tom.mcobj;

import java.util.function.Function;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.common.model.IModelState;

public class Access {
	public static IBakedModel NORMbake(IModel caller, IModelState state, final VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> spriteGetter){
		//return caller.NORMbake(state, format, spriteGetter);
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static IModel NORMgetModel(ResourceLocation modelLocation){
		//return ModelLoaderRegistry.getModel(modelLocation);
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static void NORMcompileDisplayList(ModelRenderer model, float scale){
		//model.NORMcompileDisplayList(scale);
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static void FcompiledS(ModelRenderer model, boolean v){
		//model.compiled = v;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static void FdisplayListS(ModelRenderer model, int v){
		//model.displayList = v;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static boolean Fcompiled(ModelRenderer model){
		//return model.compiled;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static int FdisplayList(ModelRenderer model){
		//return model.displayList;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static ModelBase FparentModel(ModelRenderer model){
		//return model.mcobj_parentModel;
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static ModelRenderer FparentRenderer(ModelRenderer model){
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
	public static IModel NewVMW(ModelLoader ml, ResourceLocation location, ModelBlock model, boolean uvlock, ModelBlockAnimation animation) {
		//return ml.new VanillaModelWrapper(location, model, uvlock, animation);
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static ModelBlockAnimation Fanimation(IModel model) {
		//return model.animation;
		throw new RuntimeException("Code injection failed");//Code injected
	}
}
