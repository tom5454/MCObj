package com.tom.mcobjcore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.coremod.api.ASMAPI;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.INameMappingService.Domain;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformer.Target;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.api.TransformerVoteResult;

public class MCObjTransformerService implements ITransformationService {
	private String DYNAMIC = "MCOBJAccessDyn";
	private List<Transformer> transformers;
	private String getUnbakedModel, getTextures, compileDisplayList, compiled, displayList, bindTexture, addChild;
	@Nonnull
	@Override
	public String name() {
		return "mcobj";
	}

	@Override
	public void initialize(final IEnvironment environment) {
	}

	@Override
	public void beginScanning(final IEnvironment environment) {
		getUnbakedModel = environment.findNameMapping("srg").map(m -> m.apply(Domain.METHOD, "func_209597_a")).orElse("func_209597_a");
		getTextures = environment.findNameMapping("srg").map(m -> m.apply(Domain.METHOD, "func_209559_a")).orElse("func_209559_a");
		compileDisplayList = environment.findNameMapping("srg").map(m -> m.apply(Domain.METHOD, "func_78788_d")).orElse("func_78788_d");
		compiled = environment.findNameMapping("srg").map(m -> m.apply(Domain.FIELD, "field_78812_q")).orElse("field_78812_q");
		displayList = environment.findNameMapping("srg").map(m -> m.apply(Domain.FIELD, "field_78811_r")).orElse("field_78811_r");
		bindTexture = environment.findNameMapping("srg").map(m -> m.apply(Domain.METHOD, "func_110577_a")).orElse("func_110577_a");
		addChild = environment.findNameMapping("srg").map(m -> m.apply(Domain.METHOD, "func_78792_a")).orElse("func_78792_a");
	}

	@Override
	public void onLoad(final IEnvironment env, final Set<String> otherServices) throws IncompatibleEnvironmentException {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Nonnull
	@Override
	public List<ITransformer> transformers() {
		transformers = new ArrayList<>();
		transformers.add(new Transformer("net.minecraftforge.client.model.ModelLoader"){

			@Override
			public ClassNode transform(ClassNode input) {
				String remapClass = "com/tom/mcobj/Remap";
				MethodNode node = ASMAPI.getMethodNode();
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();//func_209597_a
				node.name = getUnbakedModel;
				node.desc = "(Lnet/minecraft/util/ResourceLocation;)"
						+ "Lnet/minecraft/client/renderer/model/IUnbakedModel;";
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitMethodInsn(Opcodes.INVOKESTATIC, remapClass, "getUnbakedModel",
						"(Lnet/minecraft/util/ResourceLocation;"
								+ "Lnet/minecraftforge/client/model/ModelLoader;)"
								+ "Lnet/minecraft/client/renderer/model/IUnbakedModel;", false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.access = Opcodes.ACC_PUBLIC;
				node.name = "SUPERgetUnbakedModel";
				node.desc = "(Lnet/minecraft/util/ResourceLocation;)"
						+ "Lnet/minecraft/client/renderer/model/IUnbakedModel;";
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/client/renderer/model/ModelBakery", getUnbakedModel,
						"(Lnet/minecraft/util/ResourceLocation;)"
								+ "Lnet/minecraft/client/renderer/model/IUnbakedModel;", false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				return input;
			}
		});
		transformers.add(new Transformer("net.minecraft.client.renderer.model.BlockModel"){

			@Override
			public ClassNode transform(ClassNode input) {
				String bakeDesc = "(Lnet/minecraft/client/renderer/model/ModelBakery;"
						+ "Lnet/minecraft/client/renderer/model/BlockModel;"
						+ "Ljava/util/function/Function;"
						+ "Lnet/minecraft/client/renderer/texture/ISprite;"
						+ "Lnet/minecraft/client/renderer/vertex/VertexFormat;)"
						+ "Lnet/minecraft/client/renderer/model/IBakedModel;";
				input.methods.forEach(m -> {
					if(m.name.equals("bake") && m.desc.equals(bakeDesc)){
						m.name = "NORMbake";
					}
				});
				MethodNode node = ASMAPI.getMethodNode();
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.access = Opcodes.ACC_PUBLIC;
				node.name = "bake";
				node.desc = bakeDesc;
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitVarInsn(Opcodes.ALOAD, 2);
				node.visitVarInsn(Opcodes.ALOAD, 3);
				node.visitVarInsn(Opcodes.ALOAD, 4);
				node.visitVarInsn(Opcodes.ALOAD, 5);
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitMethodInsn(Opcodes.INVOKESTATIC, DYNAMIC, "bake",
						"(Lnet/minecraft/client/renderer/model/ModelBakery;"
								+ "Lnet/minecraft/client/renderer/model/BlockModel;"
								+ "Ljava/util/function/Function;"
								+ "Lnet/minecraft/client/renderer/texture/ISprite;"
								+ "Lnet/minecraft/client/renderer/vertex/VertexFormat;"
								+ "Lnet/minecraft/client/renderer/model/BlockModel;)"
								+ "Lnet/minecraft/client/renderer/model/IBakedModel;", false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);
				node = null;

				for(MethodNode e : input.methods){
					if(e.name.equals(getTextures)){
						node = e;
						break;
					}
				}

				ListIterator<AbstractInsnNode> itr = node.instructions.iterator();
				while (itr.hasNext()) {
					AbstractInsnNode abstractInsnNode = itr.next();
					if(abstractInsnNode.getOpcode() == Opcodes.ARETURN){
						itr.previous();
						VarInsnNode ld = (VarInsnNode) itr.previous();
						itr.next();
						itr.add(new VarInsnNode(Opcodes.ALOAD, 0));
						itr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, DYNAMIC, "getTextures",
								"(Ljava/util/Set;Lnet/minecraft/client/renderer/model/BlockModel;)V", false));
						itr.add(new VarInsnNode(ld.getOpcode(), ld.var));
						break;
					}
				}

				return input;
			}
		});
		transformers.add(new Transformer("com.tom.mcobj.Access"){

			@Override
			public ClassNode transform(ClassNode input) {
				String bakeDesc = "(Lnet/minecraft/client/renderer/model/ModelBakery;"
						+ "Lnet/minecraft/client/renderer/model/BlockModel;"
						+ "Ljava/util/function/Function;"
						+ "Lnet/minecraft/client/renderer/texture/ISprite;"
						+ "Lnet/minecraft/client/renderer/vertex/VertexFormat;)"
						+ "Lnet/minecraft/client/renderer/model/IBakedModel;";
				String normBakeDesc = "(Lnet/minecraft/client/renderer/model/BlockModel;"
						+ "Lnet/minecraft/client/renderer/model/ModelBakery;"
						+ "Lnet/minecraft/client/renderer/model/BlockModel;"
						+ "Ljava/util/function/Function;"
						+ "Lnet/minecraft/client/renderer/texture/ISprite;"
						+ "Lnet/minecraft/client/renderer/vertex/VertexFormat;)"
						+ "Lnet/minecraft/client/renderer/model/IBakedModel;";
				String superUnbakedDesc = "(Lnet/minecraft/util/ResourceLocation;)"
						+ "Lnet/minecraft/client/renderer/model/IUnbakedModel;";
				String superUnbakedSDesc = "(Lnet/minecraftforge/client/model/ModelLoader;"
						+ "Lnet/minecraft/util/ResourceLocation;)"
						+ "Lnet/minecraft/client/renderer/model/IUnbakedModel;";
				String compileDispList = "(F)V";
				String NORMcompileDispList = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;F)V";
				String FcompiledS = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;Z)V";
				String FdisplayListS = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;I)V";
				String Fcompiled = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;)Z";
				String FdisplayList = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;)I";
				String FparentModel = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;)"
						+ "Lnet/minecraft/client/renderer/model/Model;";
				String Ftex = "(Lnet/minecraft/client/renderer/model/ModelBox;)I";
				String Fdelta = "(Lnet/minecraft/client/renderer/model/ModelBox;)F";
				String FcurrTex = "(Lnet/minecraft/client/renderer/texture/TextureManager;)"
						+ "Lnet/minecraft/util/ResourceLocation;";
				String FparentRenderer = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;)"
						+ "Lnet/minecraft/client/renderer/entity/model/RendererModel;";

				input.methods.removeIf(m -> m.name.startsWith("SUPER") || m.name.startsWith("F") || m.name.startsWith("NORM"));

				MethodNode node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "NORMbake";
				node.desc = normBakeDesc;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitVarInsn(Opcodes.ALOAD, 2);
				node.visitVarInsn(Opcodes.ALOAD, 3);
				node.visitVarInsn(Opcodes.ALOAD, 4);
				node.visitVarInsn(Opcodes.ALOAD, 5);
				node.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/model/BlockModel", "NORMbake", bakeDesc, false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "SUPERgetUnbakedModel";
				node.desc = superUnbakedSDesc;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/model/ModelLoader", "SUPERgetUnbakedModel", superUnbakedDesc, false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "NORMcompileDisplayList";
				node.desc = NORMcompileDispList;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.FLOAD, 1);
				node.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/entity/model/RendererModel", "NORMcompileDisplayList", compileDispList, false);
				node.visitInsn(Opcodes.RETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FcompiledS";
				node.desc = FcompiledS;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ILOAD, 1);
				node.visitFieldInsn(Opcodes.PUTFIELD, "net/minecraft/client/renderer/entity/model/RendererModel", compiled, "Z");
				node.visitInsn(Opcodes.RETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FdisplayListS";
				node.desc = FdisplayListS;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ILOAD, 1);
				node.visitFieldInsn(Opcodes.PUTFIELD, "net/minecraft/client/renderer/entity/model/RendererModel", displayList, "I");
				node.visitInsn(Opcodes.RETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "Fcompiled";
				node.desc = Fcompiled;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/model/RendererModel", compiled, "Z");
				node.visitInsn(Opcodes.IRETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FdisplayList";
				node.desc = FdisplayList;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/model/RendererModel", displayList, "I");
				node.visitInsn(Opcodes.IRETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FparentModel";
				node.desc = FparentModel;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/model/RendererModel",
						"mcobj_parentModel", "Lnet/minecraft/client/renderer/model/Model;");
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FparentRenderer";
				node.desc = FparentRenderer;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/model/RendererModel",
						"mcobj_parentRenderer", "Lnet/minecraft/client/renderer/entity/model/RendererModel;");
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FtexU";
				node.desc = Ftex;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/model/ModelBox", "mcobj_texU", "I");
				node.visitInsn(Opcodes.IRETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FtexV";
				node.desc = Ftex;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/model/ModelBox", "mcobj_texV", "I");
				node.visitInsn(Opcodes.IRETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "Fdelta";
				node.desc = Fdelta;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/model/ModelBox", "mcobj_delta", "F");
				node.visitInsn(Opcodes.FRETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FcurrTex";
				node.desc = FcurrTex;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/renderer/texture/TextureManager",
						"mcobj_currTexture", "Lnet/minecraft/util/ResourceLocation;");
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FcullS";
				node.desc = "(Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad$Builder;"
						+ "Lnet/minecraft/util/Direction;)V";
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitFieldInsn(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/UnpackedBakedQuad$Builder",
						"mcobj_cull", "Lnet/minecraft/util/Direction;");
				node.visitInsn(Opcodes.RETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FcullS";
				node.desc = "(Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad;"
						+ "Lnet/minecraft/util/Direction;)V";
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitFieldInsn(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/UnpackedBakedQuad",
						"mcobj_cull", "Lnet/minecraft/util/Direction;");
				node.visitInsn(Opcodes.RETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "Fcull";
				node.desc = "(Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad$Builder;)"
						+ "Lnet/minecraft/util/Direction;";
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/client/model/pipeline/UnpackedBakedQuad$Builder",
						"mcobj_cull", "Lnet/minecraft/util/Direction;");
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = ASMAPI.getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "Fcull";
				node.desc = "(Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad;)"
						+ "Lnet/minecraft/util/Direction;";
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/client/model/pipeline/UnpackedBakedQuad",
						"mcobj_cull", "Lnet/minecraft/util/Direction;");
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				return input;
			}
		});
		transformers.add(new Transformer("net.minecraftforge.client.model.obj.OBJModel$OBJBakedModel"){

			@Override
			public ClassNode transform(ClassNode input) {
				String buildQuadsDesc = "(Lnet/minecraftforge/common/model/IModelState;)"
						+ "Lcom/google/common/collect/ImmutableList;";
				MethodNode node = null;
				for (MethodNode m : input.methods) {
					if(m.name.equals("buildQuads") && m.desc.equals(buildQuadsDesc)){
						node = m;
					}
				}
				String remapClass = "com/tom/mcobj/Remap";
				for (ListIterator<AbstractInsnNode> it = node.instructions.iterator(); it.hasNext(); ) {
					AbstractInsnNode insnNode = it.next();
					if(insnNode instanceof MethodInsnNode){
						MethodInsnNode mn = (MethodInsnNode) insnNode;
						if(mn.name.equals("putVertexData")){
							int builder = 0;
							int face = 0;
							for (LocalVariableNode lvn : node.localVariables) {
								if(lvn.name.equals("builder") &&
										lvn.desc.equals("Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad$Builder;")){
									builder = lvn.index;
								}else if(lvn.name.equals("f") &&
										lvn.desc.equals("Lnet/minecraftforge/client/model/obj/OBJModel$Face;")){
									face = lvn.index;
								}
							}
							it.add(new VarInsnNode(Opcodes.ALOAD, builder));
							it.add(new VarInsnNode(Opcodes.ALOAD, face));
							it.add(new VarInsnNode(Opcodes.ALOAD, 0));
							it.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/obj/OBJModel$OBJBakedModel",
									"model", "Lnet/minecraftforge/client/model/obj/OBJModel;"));
							it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, remapClass, "bakeObjQuads",
									"(Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad$Builder;"
											+ "Lnet/minecraftforge/client/model/obj/OBJModel$Face;"
											+ "Lnet/minecraftforge/client/model/obj/OBJModel;)V", false));
							break;
						}
					}
				}

				return input;
			}
		});
		transformers.add(new Transformer("net.minecraft.client.renderer.entity.model.RendererModel"){

			@Override
			public ClassNode transform(ClassNode input) {
				String compileDisplayListDesc = "(F)V";
				MethodNode node = null;
				for (MethodNode m : input.methods) {
					if(m.name.equals(compileDisplayList) && m.desc.equals(compileDisplayListDesc)){
						node = m;
					}
				}

				if(node != null){
					String name = node.name;
					node.name = "NORMcompileDisplayList";
					node.access = Opcodes.ACC_PUBLIC;

					node = ASMAPI.getMethodNode();
					node.tryCatchBlocks = new ArrayList<>();
					node.exceptions = new ArrayList<>();
					node.access = Opcodes.ACC_PRIVATE;
					node.name = name;
					node.desc = compileDisplayListDesc;
					node.visitVarInsn(Opcodes.ALOAD, 0);
					node.visitVarInsn(Opcodes.FLOAD, 1);
					node.visitMethodInsn(Opcodes.INVOKESTATIC, DYNAMIC, "compileDisplayList",
							"(Lnet/minecraft/client/renderer/entity/model/RendererModel;F)V", false);
					node.visitInsn(Opcodes.RETURN);

					input.methods.add(node);
				}

				for(FieldNode f : input.fields){
					if(f.name.equals(compiled) || f.name.equals(displayList)){
						f.access = Opcodes.ACC_PUBLIC;
					}
				}

				FieldNode fieldPM = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_parentModel",
						"Lnet/minecraft/client/renderer/model/Model;", null, null);
				input.fields.add(fieldPM);

				FieldNode fieldPR = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_parentRenderer",
						"Lnet/minecraft/client/renderer/entity/model/RendererModel;", null, null);
				input.fields.add(fieldPR);

				String desc = "(Lnet/minecraft/client/renderer/model/Model;Ljava/lang/String;)V";
				node = null;
				for (MethodNode m : input.methods) {
					if(m.name.equals("<init>") && m.desc.equals(desc)){
						node = m;
					}
				}

				if(node != null){
					AbstractInsnNode ret = node.instructions.get(node.instructions.size() - 2);
					InsnList lst = new InsnList();
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/entity/model/RendererModel", fieldPM.name, fieldPM.desc));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, DYNAMIC, "logNames", "(Lnet/minecraft/client/renderer/entity/model/RendererModel;)V", false));
					node.instructions.insertBefore(ret, lst);
				}

				desc = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;)V";
				node = null;
				for (MethodNode m : input.methods) {
					if(m.name.equals(addChild) && m.desc.equals(desc)){
						node = m;
					}
				}

				if(node != null){
					AbstractInsnNode ret = node.instructions.get(node.instructions.size() - 2);
					InsnList lst = new InsnList();
					lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/entity/model/RendererModel", fieldPR.name, fieldPR.desc));
					node.instructions.insertBefore(ret, lst);
				}

				return input;
			}

		});
		transformers.add(new Transformer("net.minecraft.client.renderer.model.ModelBox"){

			@Override
			public ClassNode transform(ClassNode input) {
				MethodNode node = null;
				FieldNode fieldU = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_texU",
						"I", null, null);
				input.fields.add(fieldU);
				FieldNode fieldV = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_texV",
						"I", null, null);
				input.fields.add(fieldV);
				FieldNode fieldD = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_delta",
						"F", null, null);
				input.fields.add(fieldD);

				String constr = "(Lnet/minecraft/client/renderer/entity/model/RendererModel;IIFFFIIIFZ)V";
				for (MethodNode m : input.methods) {
					if(m.name.equals("<init>") && m.desc.equals(constr)){
						node = m;
					}
				}

				if(node != null){
					AbstractInsnNode ret = node.instructions.get(node.instructions.size() - 2);
					InsnList lst = new InsnList();
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new VarInsnNode(Opcodes.ILOAD, 2));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/model/ModelBox", fieldU.name, fieldU.desc));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new VarInsnNode(Opcodes.ILOAD, 3));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/model/ModelBox", fieldV.name, fieldV.desc));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new VarInsnNode(Opcodes.FLOAD, 10));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/model/ModelBox", fieldD.name, fieldD.desc));
					node.instructions.insertBefore(ret, lst);
				}

				return input;
			}

		});
		transformers.add(new Transformer("net.minecraft.client.renderer.texture.TextureManager"){

			@Override
			public ClassNode transform(ClassNode input) {
				MethodNode node = null;
				FieldNode field = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_currTexture",
						"Lnet/minecraft/util/ResourceLocation;", null, null);
				input.fields.add(field);

				String constr = "(Lnet/minecraft/util/ResourceLocation;)V";
				for (MethodNode m : input.methods) {
					if(m.name.equals(bindTexture) && m.desc.equals(constr)){
						node = m;
					}
				}

				if(node != null){
					AbstractInsnNode ret = node.instructions.get(node.instructions.size() - 2);
					InsnList lst = new InsnList();
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/texture/TextureManager", field.name, field.desc));
					node.instructions.insertBefore(ret, lst);
				}

				return input;
			}

		});
		transformers.add(new Transformer("net.minecraftforge.client.model.obj.OBJModel$MaterialLibrary"){

			@Override
			public ClassNode transform(ClassNode input) {
				MethodNode node = null;

				String constr = " (Lnet/minecraft/resources/IResourceManager;"
						+ "Ljava/lang/String;"
						+ "Lnet/minecraft/util/ResourceLocation;)V";
				for (MethodNode m : input.methods) {
					if(m.name.equals("parseMaterials") && m.desc.equals(constr)){
						node = m;
					}
				}

				if(node != null){
					AbstractInsnNode ret = node.instructions.get(node.instructions.size() - 2);
					InsnList lst = new InsnList();
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/obj/OBJModel$MaterialLibrary",
							"mtlReader", "Ljava/io/BufferedReader;"));
					lst.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/BufferedReader", "close", "()V", false));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new InsnNode(Opcodes.ACONST_NULL));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/obj/OBJModel$MaterialLibrary",
							"mtlReader", "Ljava/io/BufferedReader;"));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new InsnNode(Opcodes.ACONST_NULL));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/obj/OBJModel$MaterialLibrary",
							"mtlStream", "Ljava/io/InputStreamReader;"));
					node.instructions.insertBefore(ret, lst);
				}

				return input;
			}
		});
		transformers.add(new Transformer("net.minecraftforge.client.model.pipeline.UnpackedBakedQuad"){

			@Override
			public ClassNode transform(ClassNode input) {
				FieldNode field = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_cull",
						"Lnet/minecraft/util/Direction;", null, null);
				input.fields.add(field);

				return input;
			}
		});
		transformers.add(new Transformer("net.minecraftforge.client.model.pipeline.UnpackedBakedQuad$Builder"){

			@Override
			public ClassNode transform(ClassNode input) {
				FieldNode field = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_cull",
						"Lnet/minecraft/util/Direction;", null, null);
				input.fields.add(field);
				MethodNode node = null;
				String constr = "()Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad;";
				for (MethodNode m : input.methods) {
					if(m.name.equals("build") && m.desc.equals(constr)){
						node = m;
					}
				}

				AbstractInsnNode ret = node.instructions.get(node.instructions.size() - 2);
				InsnList lst = new InsnList();
				lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
				/*lst.add(new FieldInsnNode(Opcodes.GETFIELD, "Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad$Builder;",
						"mcobj_cull", "Lnet/minecraft/util/Direction;"));*/
				lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/tom/mcobj/Remap", "finishQuad",
						"(Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad;"
								+ "Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad$Builder;)"
								+ "Lnet/minecraftforge/client/model/pipeline/UnpackedBakedQuad;", false));
				node.instructions.insertBefore(ret, lst);
				return input;
			}
		});
		return (List) transformers.stream().map(TransformerWrapper::new).collect(Collectors.toList());
	}

	private static abstract class Transformer {
		private final String clazz;
		public Transformer(String clazz) {
			this.clazz = clazz;
		}

		public Set<Target> targets() {
			return Collections.singleton(Target.targetClass(clazz));
		}

		public abstract ClassNode transform(ClassNode input);
	}

	private static class TransformerWrapper implements ITransformer<ClassNode> {
		private final Transformer tr;
		public TransformerWrapper(Transformer tr) {
			this.tr = tr;
		}

		@Override
		public ClassNode transform(ClassNode input, ITransformerVotingContext ctx) {
			return tr.transform(input);
		}

		@Override
		public TransformerVoteResult castVote(ITransformerVotingContext context) {
			return TransformerVoteResult.YES;
		}

		@Override
		public Set<Target> targets() {
			return tr.targets();
		}
	}
}
