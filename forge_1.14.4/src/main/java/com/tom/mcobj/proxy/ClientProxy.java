package com.tom.mcobj.proxy;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.world.World;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.tom.mcobj.EntityModelLoader;
import com.tom.mcobj.Remap;

public class ClientProxy extends CommonProxy {
	public static EntityModelLoader loader;
	@Override
	public World getWorld() {
		return Minecraft.getInstance().world;
	}
	@Override
	public void construct() {
		OBJLoader.INSTANCE.addDomain("mcobj");
		Remap.init();
		IResourceManager mngr = Minecraft.getInstance().getResourceManager();
		if(mngr instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)mngr).addReloadListener(new ReloadListener<EntityModelLoader>(){

				@Override
				protected EntityModelLoader prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
					profilerIn.startTick();
					EntityModelLoader modelLoader = new EntityModelLoader(resourceManagerIn);
					profilerIn.endTick();
					return modelLoader;
				}

				@Override
				protected void apply(EntityModelLoader oml, IResourceManager resourceManagerIn, IProfiler profilerIn) {
					loader = oml;
					Remap.clearCache();
				}
			});
		}else{
			loader = new EntityModelLoader(mngr);
		}
	}

	@SubscribeEvent
	public void key(InputEvent.KeyInputEvent e){
		if(Remap.RELOAD_KEY_ENABLE && e.getAction() == GLFW.GLFW_RELEASE && e.getKey() == GLFW.GLFW_KEY_KP_7){
			Remap.clearCache();
		}
	}
	@Override
	public void setup() {
	}
}
