package com.tom.mcobj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

import com.tom.mcobj.Access.CBA;
import com.tom.mcobj.Remap;

@Mixin(Cuboid.class)
public class CuboidMixin implements CBA {

	public Model mcobj_parentModel;
	public Cuboid mcobj_parentRenderer;

	@Shadow
	private int list;

	@Shadow
	private boolean compiled;

	@Inject(method = "<init>(Lnet/minecraft/client/model/Model;Ljava/lang/String;)V", at = @At("RETURN"))
	public void onInit(Model model, String boxNameIn, CallbackInfo cbi) {
		mcobj_parentModel = model;
	}

	@Inject(method = "compile", at = @At("HEAD"), cancellable = true)
	public void compile(float scale, CallbackInfo cbi) {
		if(Remap.compileDisplayList((Cuboid)(Object) this, scale))cbi.cancel();
	}

	@Inject(method = "addChild", at = @At("RETURN"))
	public void onAddChild(Cuboid child, CallbackInfo cbi) {
		((CBA)child).mcobj_setParentRenderer(this);
	}

	@Override
	public void mcobj_setCompiled(boolean comp) {
		compiled = comp;
	}

	@Override
	public void mcobj_setDisplayList(int dl) {
		list = dl;
	}

	@Override
	public int mcobj_getDisplayList() {
		return list;
	}

	@Override
	public Model mcobj_getParentModel() {
		return mcobj_parentModel;
	}

	@Override
	public Cuboid mcobj_getParentRenderer() {
		return mcobj_parentRenderer;
	}

	@Override
	public void mcobj_setParentRenderer(Object parent) {
		mcobj_parentRenderer = (Cuboid) parent;
	}
}
