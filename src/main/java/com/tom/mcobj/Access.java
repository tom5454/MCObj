package com.tom.mcobj;

import java.util.function.Function;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.ModelLoader;

public class Access {
	public static IBakedModel NORMbake(BlockModel caller, ModelBakery bakery, BlockModel p_217644_2_, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format){
		//return caller.NORMbake(bakery, p_217644_2_, spriteGetter, sprite, format);
		throw new RuntimeException("Code injection failed");//Code injected
	}
	public static IUnbakedModel SUPERgetUnbakedModel(ModelLoader s, ResourceLocation modelLocation){
		//return s.SUPERgetUnbakedModel(modelLocation);
		throw new RuntimeException("Code injection failed");//Code injected
	}
}
