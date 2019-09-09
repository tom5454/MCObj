package com.tom.mcobjcore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.coremod.api.ASMAPI;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.INameMappingService.Domain;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.api.TransformerVoteResult;

public class MCObjTransformerService implements ITransformationService {
	private List<ITransformer<?>> transformers;
	private String getUnbakedModel, getTextures;
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
	}

	@Override
	public void onLoad(final IEnvironment env, final Set<String> otherServices) throws IncompatibleEnvironmentException {

	}

	@Nonnull
	@Override
	public List<ITransformer> transformers() {
		transformers = new ArrayList<>();
		transformers.add(new ITransformer<ClassNode>(){

			@Override
			public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
				/*System.out.println("ICustomModelLoader");
				for (Method method : ICustomModelLoader.class.getDeclaredMethods()) {
					System.out.println(method.getName());
					System.out.println(method.getReturnType());
					System.out.println(Arrays.toString(method.getParameterTypes()));
					System.out.println();
				}*/
				String remapClass = "com/tom/mcobj/Remap";
				MethodNode node = ASMAPI.getMethodNode();
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();//func_209597_a
				node.name = getUnbakedModel;
				node.desc = "(Lnet/minecraft/util/ResourceLocation;)"
						+ "Lnet/minecraft/client/renderer/model/IUnbakedModel;";
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitVarInsn(Opcodes.ALOAD, 0);
				//Type.getMethodDescriptor(Type.getType(IUnbakedModel.class), Type.getType(ResourceLocation.class), Type.getType(ModelLoader.class))
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

			@Override
			public TransformerVoteResult castVote(ITransformerVotingContext context) {
				return TransformerVoteResult.YES;
			}

			@Override
			public Set<Target> targets() {
				return Collections.singleton(Target.targetClass("net.minecraftforge.client.model.ModelLoader"));
			}
		});
		transformers.add(new ITransformer<ClassNode>(){

			@Override
			public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
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
				String remapClass = "MCOBJAccessDyn";
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
				node.visitMethodInsn(Opcodes.INVOKESTATIC, remapClass, "bake",
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
						itr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, remapClass, "getTextures",
								"(Ljava/util/Set;Lnet/minecraft/client/renderer/model/BlockModel;)V", false));
						itr.add(new VarInsnNode(ld.getOpcode(), ld.var));
						break;
					}
				}

				return input;
			}

			@Override
			public TransformerVoteResult castVote(ITransformerVotingContext context) {
				return TransformerVoteResult.YES;
			}

			@Override
			public Set<Target> targets() {
				return Collections.singleton(Target.targetClass("net.minecraft.client.renderer.model.BlockModel"));
			}
		});
		transformers.add(new ITransformer<ClassNode>(){

			@Override
			public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
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

				for (MethodNode m : input.methods) {
					if(m.name.equals("NORMbake")){
						input.methods.remove(m);
						break;
					}
				}
				for (MethodNode m : input.methods) {
					if(m.name.equals("SUPERgetUnbakedModel")){
						input.methods.remove(m);
						break;
					}
				}
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

				return input;
			}

			@Override
			public TransformerVoteResult castVote(ITransformerVotingContext context) {
				return TransformerVoteResult.YES;
			}

			@Override
			public Set<Target> targets() {
				return Collections.singleton(Target.targetClass("com.tom.mcobj.Access"));
			}
		});
		transformers.add(new ITransformer<ClassNode>(){

			@Override
			public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
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

			@Override
			public TransformerVoteResult castVote(ITransformerVotingContext context) {
				return TransformerVoteResult.YES;
			}

			@Override
			public Set<Target> targets() {
				return Collections.singleton(Target.targetClass("net.minecraftforge.client.model.obj.OBJModel$OBJBakedModel"));
			}
		});
		return (List) transformers;
	}
}
