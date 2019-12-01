package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import com.tom.mcobj.Access.TMA;

@Mixin(TextureManager.class)
public class TextureManagerMixin implements TMA {
	public Identifier mcobj_currTexture;

	@Inject(method = "bindTexture", at = @At("RETURN"))
	public void onBindTexture(Identifier loc, CallbackInfo cbi) {
		mcobj_currTexture = loc;
	}

	@Override
	public Identifier mcobj_getBoundTexture() {
		return mcobj_currTexture;
	}
}
