package com.tom.mcobj.proxy;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

import com.tom.mcobj.EntityModelLoader;
import com.tom.mcobj.Remap;

public class ClientProxy extends CommonProxy {
	public static EntityModelLoader loader;
	@Override
	public World getWorld() {
		return Minecraft.getMinecraft().world;
	}
	@Override
	public void construct() {
		ModelLoaderRegistry.registerLoader(new ICustomModelLoader() {

			@Override
			public void onResourceManagerReload(IResourceManager resourceManager) {
				OBJLoader.INSTANCE.onResourceManagerReload(resourceManager);
				loader = new EntityModelLoader(resourceManager);
				Remap.clearCache();
			}

			@Override
			public IModel loadModel(ResourceLocation modelLocation) throws Exception {
				return Remap.getUnbakedModel(modelLocation);
			}

			@Override
			public boolean accepts(ResourceLocation modelLocation) {
				return modelLocation.getResourceDomain().equals("mcobj") && modelLocation.getResourcePath().endsWith(".obj");
			}
		});
		Remap.init();
	}

	@SubscribeEvent
	public void key(KeyInputEvent e){
		int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
		if (!Keyboard.isKeyDown(Keyboard.getEventKey()))
			return;
		if(Remap.RELOAD_KEY_ENABLE && i == Keyboard.KEY_NUMPAD7){
			Remap.clearCache();
		}else if(Remap.RELOAD_KEY_ENABLE && i == Keyboard.KEY_NUMPAD6){
			Remap.trsrdump();
		}
	}
	@Override
	public void setup() {
	}
}
