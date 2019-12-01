package com.tom.mcobj.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.EntityType;

import com.tom.mcobj.Access.EDA;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin implements EDA {

	@Shadow
	private Map<EntityType<?>, EntityRenderer<?>> renderers;

	@Shadow
	private Map<String, PlayerEntityRenderer> modelRenderers;

	@Override
	public Map<EntityType<?>, EntityRenderer<?>> mcobj_getRegistry() {
		return renderers;
	}

	@Override
	public Map<String, PlayerEntityRenderer> mcobj_getRenderers() {
		return modelRenderers;
	}


}
