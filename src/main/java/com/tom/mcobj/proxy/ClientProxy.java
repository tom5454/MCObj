package com.tom.mcobj.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import net.minecraftforge.client.model.obj.OBJLoader;

import com.tom.mcobj.Remap;

public class ClientProxy extends CommonProxy {

	@Override
	public World getWorld() {
		return Minecraft.getInstance().world;
	}
	@Override
	public void construct() {
		OBJLoader.INSTANCE.addDomain("mcobj");
		Remap.init();
	}
	/*@SubscribeEvent
	public void addTextures(TextureStitchEvent.Pre event){
		Remap.addTextures(event);
	}*/
	@Override
	public void setup() {
	}
}
