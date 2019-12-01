package com.tom.mcobj.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

import com.tom.mcobj.Access.BDA;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin implements BDA {

	@Shadow
	private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers;

	@Override
	public Map<BlockEntityType<?>, BlockEntityRenderer<?>> mcobj_getRegistry() {
		return renderers;
	}
}
