package com.tom.mcobj;

import static com.tom.mcobj.MCObjInit.log;

import java.util.List;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec2f;

import com.tom.mcobj.forge.VertexC;
import com.tom.mcobj.proxy.ClientProxy;

public class Remap2 {

	public static boolean renderCuboid(MatrixStack.Entry matrix4f_1, VertexConsumer vertexConsumer_1, int int_1, int int_2,
			float float_1, float float_2, float float_3, float float_4, List<Cuboid> cuboids, ModelPart this$0,
			float textureWidth, float textureHeight, String mcobj_name, float pivotX, float pivotY, float pivotZ) {
		Remap.ModelName name = Remap.toName(this$0);
		if(!Remap.logSet.contains(name.toString())){
			log.info("Compiling: " + name + " " + textureWidth + ", " + textureHeight);
			Remap.logSet.add(name.toString());
		}
		com.tom.mcobj.EntityModelLoader.Model m = ClientProxy.loader.getModel(name.model);
		if(m == null){
			m = ClientProxy.loader.getModel("model_box");
			if(m == null)
				return false;
			else {
				VertexC vc = new VertexC(matrix4f_1.getModel(), vertexConsumer_1, float_1, int_1, int_2, float_2, float_3, float_4);
				for(int i = 0; i < cuboids.size(); ++i) {
					vc.push();
					Cuboid box = cuboids.get(i);
					float dx = box.maxX - box.minX;
					float dy = box.maxY - box.minY;
					float dz = box.maxZ - box.minZ;
					Vector3f delta = Access.Fdelta(box);
					vc.translate((box.minX - delta.getX()) / 16f, (box.minY - delta.getY()) / 16f, (box.minZ - delta.getZ()) / 16f);
					vc.scale((dx + delta.getX()*2)/16f, (dy + delta.getY()*2)/16f, (dz + delta.getZ()*2)/16f);
					int texU = Access.FtexU(box);
					int texV = Access.FtexV(box);
					float w = dx * 2 + dz * 2;
					float h = (dy + dz) * 2;
					m.renderReUV(vc, uv -> {
						float x = uv.x;
						float y = 1 - uv.y;
						float qx = (x % .25f) * 4;
						float qy = (y % .25f) * 4;
						float u = texU + (x) * w;
						float v = texV + (y) * h;
						String block = null;
						if(x >= .25f && x < .5f && y < .25f){//xW zH
							block = "top";
							u = texU + dz +  qx * dx;
							v = texV +       qy * dz;
						}else if(x >= .5f && x < .75f && y < .25f){//xW zH
							block = "bottom";
							u = texU + dz + dx +  qx * dx;
							v = texV +            qy * dz;
						}else if(x < .25f && y < .5f && y >= .25f){//zW yH
							block = "left";
							u = texU +       qx * dz;
							v = texV + dz +  qy * dy;
						}else if(x < .5f && x >= .25f && y < .5f && y >= .25f){//xW yH
							block = "front";
							u = texU + dz +  qx * dx;
							v = texV + dz +  qy * dy;
						}else if(x < .75f && x >= .5f && y < .5f && y >= .25f){//zW yH
							block = "right";
							u = texU + dz + dx +  qx * dz;
							v = texV + dz +       qy * dy;
						}else if(x >= .75f && y < .5f && y >= .25f){//xW yH
							block = "back";
							u = texU + dz * 2 + dx +  qx * dx;
							v = texV + dz +           qy * dy;
						}
						/*if(block != null){
							log.info(block + " " + texU + " " + texV + " " + x + " " + y + " " + dx + " " + dy + " " + dz);
							log.info(u + " " + v);
						}*/
						return new Vec2f(
								u / textureWidth,//x,//
								v / textureHeight//-y//
								);
					});
				}
				return true;
			}
		}else{
			VertexC vc = new VertexC(matrix4f_1.getModel(), vertexConsumer_1, float_1, int_1, int_2, float_2, float_3, float_4);
			vc.push();
			vc.translate(-pivotX / 16, -pivotY / 16, -pivotZ / 16);
			vc.scale(-1, -1, 1);
			vc.translate(-1, -1, 0);
			if(!m.renderNormal(Remap.fieldRemapper.getOrDefault(mcobj_name, mcobj_name), vc))
				m.renderNormal(mcobj_name, vc);
			return true;
		}
	}

}
