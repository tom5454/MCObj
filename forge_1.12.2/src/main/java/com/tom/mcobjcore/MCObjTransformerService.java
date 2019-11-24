package com.tom.mcobjcore;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;

import org.jline.utils.InputStreamReader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class MCObjTransformerService implements IClassTransformer {
	private static final String DYNAMIC = "com/tom/mcobj/Remap";//MCOBJAccessDyn
	private static Map<String, UnaryOperator<ClassNode>> transformers;
	public static Optional<BiFunction<Integer, String, String>> mapping = Optional.empty();
	private static String compileDisplayList, compiled, displayList, baseModel, bindTexture, addChild;
	private static String imodel, vertexformat, resloc, modelblock, modelbase, modelrenderer, modelbox, texmngr;

	public static void init() {
		beginScanning();
		transformers();
	}
	public static final int METHOD = 0, FIELD = 1, CLASS = 2;
	private static void beginScanning() {
		if(MCObjLoadingPlugin.deobf){
			System.out.println("Deobf version");
			Map<String, String> fields = new HashMap<>();
			Map<String, String> methods = new HashMap<>();
			Map<String, String> classes = new HashMap<>();
			loadCSV("/com/tom/mcobjcore/fields.csv", fields);
			loadCSV("/com/tom/mcobjcore/methods.csv", methods);
			loadCSV("/com/tom/mcobjcore/classes.csv", classes);
			mapping = Optional.of((t, f) -> {
				switch(t) {
				case METHOD:
					return methods.get(f);
				case FIELD:
					return fields.get(f);
				case CLASS:
					return classes.get(f);
				}
				return f;
			});
		}else{
			mapping = Optional.of((t, f) -> {
				switch(t) {
				case METHOD:
					return asmName(f);
				case FIELD:
					return f;
				case CLASS:
					return f;
				}
				return f;
			});
		}
		compileDisplayList = mapping.map(m -> m.apply(METHOD, "func_78788_d")).orElse("func_78788_d");
		compiled = mapping.map(m -> m.apply(FIELD, "field_78812_q")).orElse("field_78812_q");
		displayList = mapping.map(m -> m.apply(FIELD, "field_78811_r")).orElse("field_78811_r");
		bindTexture = mapping.map(m -> m.apply(METHOD, "func_110577_a")).orElse("func_110577_a");
		baseModel = mapping.map(m -> m.apply(FIELD, "field_78810_s")).orElse("field_78810_s");
		addChild = mapping.map(m -> m.apply(METHOD, "func_78792_a")).orElse("func_78792_a");
		imodel = mapping.map(m -> m.apply(CLASS, "cfy")).orElse("cfy");
		vertexformat = mapping.map(m -> m.apply(CLASS, "cea")).orElse("cea");
		resloc = mapping.map(m -> m.apply(CLASS, "nf")).orElse("nf");
		modelblock = mapping.map(m -> m.apply(CLASS, "bvu")).orElse("bvu");
		modelbase = mapping.map(m -> m.apply(CLASS, "bqf")).orElse("bqf");
		modelrenderer = mapping.map(m -> m.apply(CLASS, "brs")).orElse("brs");
		modelbox = mapping.map(m -> m.apply(CLASS, "brq")).orElse("brq");
		texmngr = mapping.map(m -> m.apply(CLASS, "cdr")).orElse("cdr");
	}

	private static void loadCSV(String path, Map<String, String> str) {
		try (BufferedReader r = new BufferedReader(new InputStreamReader(MCObjTransformerService.class.getResourceAsStream(path)))){
			r.readLine();
			String ln;
			while((ln = r.readLine()) != null) {
				String[] sp = ln.split(",", 4);
				str.put(sp[0], sp[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Nonnull
	private static void transformers() {
		transformers = new HashMap<>();
		/*transformers.put("net.minecraftforge.client.model.ModelLoaderRegistry", new UnaryOperator<ClassNode>(){

			@Override
			public ClassNode apply(ClassNode input) {
				String remapClass = "com/tom/mcobj/Remap";
				String desc = "(L" + resloc + ";)"
						+ "Lnet/minecraftforge/client/model/IModel;";

				input.methods.forEach(m -> {
					if(m.name.equals("getModel") && m.desc.equals(desc)){
						m.name = "NORMgetModel";
					}
				});

				MethodNode node = getMethodNode();
				node.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.name = "getModel";
				node.desc = desc;
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitMethodInsn(Opcodes.INVOKESTATIC, remapClass, "getUnbakedModel", desc, false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				return input;
			}
		});*/
		transformers.put("net.minecraftforge.client.model.ModelLoader$VanillaModelWrapper", new UnaryOperator<ClassNode>(){

			@Override
			public ClassNode apply(ClassNode input) {
				String bakeDesc = "(Lnet/minecraftforge/common/model/IModelState;"
						+ "L" + vertexformat + ";"
						+ "Ljava/util/function/Function;)"
						+ "L" + imodel + ";";
				input.methods.forEach(m -> {
					if(m.name.equals("bakeImpl") && m.desc.equals(bakeDesc)){
						m.name = "NORMbake";
					}
				});
				//input.fields.forEach(f -> System.out.println(f.name + " " + f.desc));
				input.access = (input.access & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PUBLIC;
				MethodNode node = getMethodNode();
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.access = Opcodes.ACC_PUBLIC;
				node.name = "bakeImpl";
				node.desc = bakeDesc;
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ALOAD, 2);
				node.visitVarInsn(Opcodes.ALOAD, 3);
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/client/model/ModelLoader$VanillaModelWrapper",
						"model", "L" + modelblock + ";");
				node.visitMethodInsn(Opcodes.INVOKESTATIC, DYNAMIC, "bake",
						"(Lnet/minecraftforge/common/model/IModelState;"
								+ "Lnet/minecraftforge/client/model/IModel;"
								+ "L" + vertexformat + ";"
								+ "Ljava/util/function/Function;"
								+ "L" + modelblock + ";)"
								+ "L" + imodel + ";", false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);
				node = null;

				for(MethodNode e : input.methods){
					if(e.name.equals("getTextures")){
						node = e;
						break;
					}
				}

				ListIterator<AbstractInsnNode> itr = node.instructions.iterator();
				while (itr.hasNext()) {
					AbstractInsnNode abstractInsnNode = itr.next();
					if(abstractInsnNode.getOpcode() == Opcodes.ARETURN){
						itr.previous();
						itr.previous();
						VarInsnNode ld = (VarInsnNode) itr.previous();
						itr.next();
						itr.add(new VarInsnNode(Opcodes.ALOAD, 0));
						itr.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/ModelLoader$VanillaModelWrapper",
								"model", "L" + modelblock + ";"));
						itr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, DYNAMIC, "getTextures",
								"(Lcom/google/common/collect/ImmutableSet$Builder;"
										+ "L" + modelblock + ";)V", false));
						itr.add(new VarInsnNode(ld.getOpcode(), ld.var));
						break;
					}
				}

				return input;
			}
		});
		transformers.put("com.tom.mcobj.Access", new UnaryOperator<ClassNode>(){

			@Override
			public ClassNode apply(ClassNode input) {
				String bakeDesc = "(Lnet/minecraftforge/common/model/IModelState;"
						+ "L" + vertexformat + ";"
						+ "Ljava/util/function/Function;)"
						+ "L" + imodel + ";";
				String normBakeDesc = "(Lnet/minecraftforge/client/model/IModel;"
						+ "Lnet/minecraftforge/common/model/IModelState;"
						+ "L" + vertexformat + ";"
						+ "Ljava/util/function/Function;)"
						+ "L" + imodel + ";";
				String normGetModelDesc = "(L" + resloc + ";)"
						+ "Lnet/minecraftforge/client/model/IModel;";
				String compileDispList = "(F)V";
				String NORMcompileDispList = "(L" + modelrenderer + ";F)V";
				String FcompiledS = "(L" + modelrenderer + ";Z)V";
				String FdisplayListS = "(L" + modelrenderer + ";I)V";
				String Fcompiled = "(L" + modelrenderer + ";)Z";
				String FdisplayList = "(L" + modelrenderer + ";)I";
				String FparentModel = "(L" + modelrenderer + ";)"
						+ "L" + modelbase + ";";
				String Ftex = "(L" + modelbox + ";)I";
				String Fdelta = "(L" + modelbox + ";)F";
				String FcurrTex = "(L" + texmngr + ";)"
						+ "L" + resloc + ";";
				String FparentRenderer = "(L" + modelrenderer + ";)"
						+ "L" + modelrenderer + ";";
				String FanimationDesc = "(Lnet/minecraftforge/client/model/IModel;)"
						+ "Lnet/minecraftforge/client/model/animation/ModelBlockAnimation;";
				String NewVMWDesc = "(Lnet/minecraftforge/client/model/ModelLoader;"
						+ "L" + resloc + ";"
						+ "L" + modelblock + ";"
						+ "Z"
						+ "Lnet/minecraftforge/client/model/animation/ModelBlockAnimation;)"
						+ "Lnet/minecraftforge/client/model/IModel;";

				input.methods.removeIf(m -> m.name.startsWith("SUPER") ||
						m.name.startsWith("F") || m.name.startsWith("NORM") || m.name.startsWith("New"));

				MethodNode node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "NORMbake";
				node.desc = normBakeDesc;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraftforge/client/model/ModelLoader$VanillaModelWrapper");
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitVarInsn(Opcodes.ALOAD, 2);
				node.visitVarInsn(Opcodes.ALOAD, 3);
				node.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/model/ModelLoader$VanillaModelWrapper", "NORMbake", bakeDesc, false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "NORMgetModel";
				node.desc = normGetModelDesc;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraftforge/client/model/ModelLoaderRegistry", "NORMgetModel", normGetModelDesc, false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "NORMcompileDisplayList";
				node.desc = NORMcompileDispList;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.FLOAD, 1);
				node.visitMethodInsn(Opcodes.INVOKEVIRTUAL, modelrenderer, "NORMcompileDisplayList", compileDispList, false);
				node.visitInsn(Opcodes.RETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FcompiledS";
				node.desc = FcompiledS;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ILOAD, 1);
				node.visitFieldInsn(Opcodes.PUTFIELD, modelrenderer, compiled, "Z");
				node.visitInsn(Opcodes.RETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FdisplayListS";
				node.desc = FdisplayListS;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ILOAD, 1);
				node.visitFieldInsn(Opcodes.PUTFIELD, modelrenderer, displayList, "I");
				node.visitInsn(Opcodes.RETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "Fcompiled";
				node.desc = Fcompiled;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, modelrenderer, compiled, "Z");
				node.visitInsn(Opcodes.IRETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FdisplayList";
				node.desc = FdisplayList;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, modelrenderer, displayList, "I");
				node.visitInsn(Opcodes.IRETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FparentModel";
				node.desc = FparentModel;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, modelrenderer,
						baseModel, "L" + modelbase + ";");
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FparentRenderer";
				node.desc = FparentRenderer;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, modelrenderer,
						"mcobj_parentRenderer", "L" + modelrenderer + ";");
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FtexU";
				node.desc = Ftex;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, modelbox, "mcobj_texU", "I");
				node.visitInsn(Opcodes.IRETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FtexV";
				node.desc = Ftex;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, modelbox, "mcobj_texV", "I");
				node.visitInsn(Opcodes.IRETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "Fdelta";
				node.desc = Fdelta;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, modelbox, "mcobj_delta", "F");
				node.visitInsn(Opcodes.FRETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "FcurrTex";
				node.desc = FcurrTex;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitFieldInsn(Opcodes.GETFIELD, texmngr,
						"mcobj_currTexture", "L" + resloc + ";");
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "Fanimation";
				node.desc = FanimationDesc;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraftforge/client/model/ModelLoader$VanillaModelWrapper");
				node.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/client/model/ModelLoader$VanillaModelWrapper",
						"animation", "Lnet/minecraftforge/client/model/animation/ModelBlockAnimation;");
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				node = getMethodNode();
				node.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
				node.name = "NewVMW";
				node.desc = NewVMWDesc;
				node.tryCatchBlocks = new ArrayList<>();
				node.exceptions = new ArrayList<>();
				node.visitTypeInsn(Opcodes.NEW, "net/minecraftforge/client/model/ModelLoader$VanillaModelWrapper");
				node.visitInsn(Opcodes.DUP);
				node.visitVarInsn(Opcodes.ALOAD, 0);
				node.visitVarInsn(Opcodes.ALOAD, 1);
				node.visitVarInsn(Opcodes.ALOAD, 2);
				node.visitVarInsn(Opcodes.ILOAD, 3);
				node.visitVarInsn(Opcodes.ALOAD, 4);
				node.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraftforge/client/model/ModelLoader$VanillaModelWrapper",
						"<init>", "(Lnet/minecraftforge/client/model/ModelLoader;"
								+ "L" + resloc + ";"
								+ "L" + modelblock + ";"
								+ "Z"
								+ "Lnet/minecraftforge/client/model/animation/ModelBlockAnimation;)V", false);
				node.visitInsn(Opcodes.ARETURN);
				input.methods.add(node);

				return input;
			}
		});
		transformers.put("net.minecraftforge.client.model.obj.OBJModel$OBJBakedModel", new UnaryOperator<ClassNode>(){

			@Override
			public ClassNode apply(ClassNode input) {
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
		transformers.put("net.minecraft.client.model.ModelRenderer", new UnaryOperator<ClassNode>(){

			@Override
			public ClassNode apply(ClassNode input) {
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

					node = getMethodNode();
					node.tryCatchBlocks = new ArrayList<>();
					node.exceptions = new ArrayList<>();
					node.access = Opcodes.ACC_PRIVATE;
					node.name = name;
					node.desc = compileDisplayListDesc;
					node.visitVarInsn(Opcodes.ALOAD, 0);
					node.visitVarInsn(Opcodes.FLOAD, 1);
					node.visitMethodInsn(Opcodes.INVOKESTATIC, DYNAMIC, "compileDisplayList",
							"(L" + modelrenderer + ";F)V", false);
					node.visitInsn(Opcodes.RETURN);

					input.methods.add(node);
				}

				for(FieldNode f : input.fields){
					//System.out.println(f.name + " " + f.desc);
					//if(f.name.equals(asmName(compiled)) || f.name.equals(asmName(displayList)) || f.name.equals(asmName(baseModel))){
					f.access = ((f.access & ~Opcodes.ACC_PRIVATE) & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
					//}
				}

				FieldNode fieldPR = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_parentRenderer",
						"L" + modelrenderer + ";", null, null);
				input.fields.add(fieldPR);

				String desc = "(L" + modelbase + ";Ljava/lang/String;)V";
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
					lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, DYNAMIC, "logNames", "(L" + modelrenderer + ";)V", false));
					node.instructions.insertBefore(ret, lst);
				}

				desc = "(L" + modelrenderer + ";)V";
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
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, modelrenderer, fieldPR.name, fieldPR.desc));
					node.instructions.insertBefore(ret, lst);
				}

				return input;
			}

		});
		transformers.put("net.minecraft.client.model.ModelBox", new UnaryOperator<ClassNode>(){

			@Override
			public ClassNode apply(ClassNode input) {
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

				String constr = "(L" + modelrenderer + ";IIFFFIIIFZ)V";
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
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, modelbox, fieldU.name, fieldU.desc));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new VarInsnNode(Opcodes.ILOAD, 3));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, modelbox, fieldV.name, fieldV.desc));
					lst.add(new VarInsnNode(Opcodes.ALOAD, 0));
					lst.add(new VarInsnNode(Opcodes.FLOAD, 10));
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, modelbox, fieldD.name, fieldD.desc));
					node.instructions.insertBefore(ret, lst);
				}

				return input;
			}
		});
		transformers.put("net.minecraft.client.renderer.texture.TextureManager", new UnaryOperator<ClassNode>(){

			@Override
			public ClassNode apply(ClassNode input) {
				MethodNode node = null;
				FieldNode field = new FieldNode(Opcodes.ACC_PUBLIC, "mcobj_currTexture",
						"L" + resloc + ";", null, null);
				input.fields.add(field);

				String constr = "(L" + resloc + ";)V";
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
					lst.add(new FieldInsnNode(Opcodes.PUTFIELD, texmngr, field.name, field.desc));
					node.instructions.insertBefore(ret, lst);
				}

				return input;
			}
		});
		/*transformers.put("net.minecraftforge.client.model.obj.OBJModel$MaterialLibrary", new UnaryOperator<ClassNode>(){

			@Override
			public ClassNode apply(ClassNode input) {
				MethodNode node = null;

				String constr = " (Lnet/minecraft/resources/IResourceManager;"
						+ "Ljava/lang/String;"
						+ "L" + resloc + ";)V";
				for (MethodNode m : input.methods) {
					if(m.name.equals("parseMaterials") && m.desc.equals(constr)){
						node = m;
					}
				}

				if(node != null){
					AbstractInsnNode ret = node.instructions.get(node.instructions.size() - 2);
					int mtlReader = 0;
					for (LocalVariableNode lvn : node.localVariables) {
						if(lvn.name.equals("mtlReader") &&
								lvn.desc.equals("Ljava/io/BufferedReader;")){
							mtlReader = lvn.index;
						}
					}
				}

				return input;
			}
		});*/
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(transformers == null)return basicClass;
		//System.out.println("MCObj transformer: " + name + ", " + transformedName);
		UnaryOperator<ClassNode> tr = transformers.get(transformedName);
		if(tr != null) {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);
			System.out.println("Applying mcobj transformer: " + transformedName);
			classNode = tr.apply(classNode);
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			return writer.toByteArray();
		}
		return basicClass;
	}

	public static MethodNode getMethodNode() {
		return new MethodNode(Opcodes.ASM5);
	}

	private static String asmName(String f) {
		return f.substring(f.length() - 1, f.length());
	}
}
