package com.tom.mcobj.mixin;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import com.tom.mcobj.Access.BMA;
import com.tom.mcobj.Remap;

@Mixin(JsonUnbakedModel.class)
public class JsonUnbakedModelMixin implements BMA {
	@Shadow
	protected JsonUnbakedModel parent;

	@Inject(method = "bake", at = @At("HEAD"), cancellable = true)
	public void mcobj_onBake(ModelLoader bakery, Function<Identifier, Sprite> spriteGetter,
			ModelBakeSettings sprite, Identifier id, CallbackInfoReturnable<BakedModel> caller) {
		BakedModel bm = Remap.bake(bakery, spriteGetter, sprite, (JsonUnbakedModel)(Object) this);
		if(bm != null)caller.setReturnValue(bm);
	}

	@Inject(method = "getTextureDependencies", at = @At("RETURN"))
	public void mcobj_onGetTextureDependencies(CallbackInfoReturnable<Collection<Identifier>> cir) {
		Remap.getTextures((Set<Identifier>) cir.getReturnValue(), (JsonUnbakedModel)(Object) this);
	}

	@Override
	public JsonUnbakedModel mcobj_getParent() {
		return parent;
	}
}
