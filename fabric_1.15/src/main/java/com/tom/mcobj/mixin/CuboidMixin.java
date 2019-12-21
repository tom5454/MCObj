package com.tom.mcobj.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.tom.mcobj.Access.CBA;
import com.tom.mcobj.Remap;
import com.tom.mcobj.Remap2;

import it.unimi.dsi.fastutil.objects.ObjectList;

@Mixin(ModelPart.class)
public class CuboidMixin implements CBA {

	public String mcobj_name;
	public String mcobj_parentName;
	public Model mcobj_parentModel;
	public ModelPart mcobj_parentRenderer;

	@Shadow
	private ObjectList<ModelPart> children;

	@Shadow
	private ObjectList<Cuboid> cuboids;

	@Shadow
	private float textureWidth;

	@Shadow
	private float textureHeight;

	@Shadow
	public float pivotX;

	@Shadow
	public float pivotY;

	@Shadow
	public float pivotZ;

	@Inject(method = "<init>(Lnet/minecraft/client/model/Model;)V", at = @At("RETURN"))
	public void mcobj_onInit(Model model, CallbackInfo cbi) {
		mcobj_parentName = Remap.getClassName(model);
		mcobj_parentModel = model;
	}

	@Inject(method = "<init>(Lnet/minecraft/client/model/Model;II)V", at = @At("RETURN"))
	public void mcobj_onInit(Model model, int int_1, int int_2, CallbackInfo cbi) {
		mcobj_parentName = Remap.getClassName(model);
		mcobj_parentModel = model;
	}

	@Inject(method = "renderCuboids", at = @At("HEAD"), cancellable = true)
	public void mcobj_renderCuboids(MatrixStack.Entry matrix4f_1, VertexConsumer vertexConsumer_1, int int_1, int int_2, float float_1, float float_2, float float_3, float float_4, CallbackInfo cbi) {
		if(Remap2.renderCuboid(matrix4f_1, vertexConsumer_1, int_1, int_2, float_1, float_2, float_3, float_4,
				cuboids, (ModelPart)(Object) this, textureWidth, textureHeight, mcobj_name, pivotX, pivotY, pivotZ))
			cbi.cancel();
	}

	@Inject(method = "addChild", at = @At("RETURN"))
	public void mcobj_onAddChild(ModelPart child, CallbackInfo cbi) {
		((CBA)child).mcobj_setParentRenderer(this);
	}

	@Override
	public ModelPart mcobj_getParentRenderer() {
		return mcobj_parentRenderer;
	}

	@Override
	public void mcobj_setParentRenderer(Object parent) {
		mcobj_parentRenderer = (ModelPart) parent;
	}

	@Override
	public String mcobj_getName() {
		return mcobj_name;
	}

	@Override
	public void mcobj_setName(String name) {
		this.mcobj_name = name;
	}

	@Override
	public List<ModelPart> mcobj_getChildren() {
		return children;
	}

	@Override
	public Model mcobj_getParentModel() {
		return mcobj_parentModel;
	}

	@Override
	public void mcobj_setParentName(String name) {
		mcobj_parentName = name;
	}

	@Override
	public String mcobj_getParentName() {
		return mcobj_parentName;
	}
}
