package com.tom.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MappingGenerator {
	private static final String PCKG = "net/minecraft/client/render/entity/model/";
	private static final String PCKG2 = "net/minecraft/client/render/block/entity/";
	public static void main(String[] args) {//TODO
		try (BufferedReader mappingRd = new BufferedReader(new InputStreamReader(MappingGenerator.class.getResourceAsStream("/mappings/mappings.tiny")));
				BufferedReader fieldRd = new BufferedReader(new FileReader(new File("../MCObj", "fields.csv")));
				BufferedReader forgeRd = new BufferedReader(new FileReader(new File("../MCObj/src/main/resources/com/tom/mcobj", "modelnames.txt")));
				BufferedReader classRd = new BufferedReader(new FileReader(new File(".", "fclass.map")));
				PrintWriter wr = new PrintWriter(new File("src/main/resources/com/tom/mcobj", "modelnames.txt"));
				PrintWriter wrc = new PrintWriter(new File("src/main/resources/com/tom/mcobj", "classnames.txt"))){
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			Map<String, String> srgToV = new HashMap<>();
			Map<String, Map<String, String>> VtoF =  new HashMap<>();
			Map<String, String> FtoVC = new HashMap<>();
			Map<String, String> FClassMapper = new HashMap<>();
			Map<String, String> FFieldMapper = new HashMap<>();
			Map<String, String> FtoO = new HashMap<>();
			System.out.println("Loading mappings");
			mappingRd.readLine();
			fieldRd.readLine();
			String ln;
			while((ln = mappingRd.readLine()) != null){
				String[] sp = ln.split("\t");
				if(sp[0].equals("FIELD")) {
					String clazz = sp[1];
					String vname = sp[3];
					String name = sp[4];
					VtoF.computeIfAbsent(vname, e -> new HashMap<>()).put(clazz, name);
				}else if(sp[0].equals("CLASS")){
					FtoVC.put(sp[3].toLowerCase(), sp[1]);
					FtoO.put(sp[3].toLowerCase(), sp[2]);
				}
			}
			while((ln = fieldRd.readLine()) != null){
				String[] sp = ln.split(",", 4);
				String srg = sp[0];
				int i = srg.lastIndexOf('_');
				srgToV.put(srg, srg.substring(i+1));
			}
			while((ln = classRd.readLine()) != null){
				String[] sp = ln.split(";", 3);
				if     (sp[0].equals("C"))FClassMapper.put(sp[1], sp[2]);
				else if(sp[0].equals("F"))FFieldMapper.put(sp[1], sp[2]);
			}
			System.out.println("Processing");
			Set<String> clazzPrinted = new HashSet<>();
			while((ln = forgeRd.readLine()) != null){
				String[] sp = ln.split(";", 2);
				String srg = sp[0];
				int i = srg.indexOf('/');
				if(i != -1)srg = srg.substring(0, i);
				i = srg.indexOf('[');
				if(i != -1)srg = srg.substring(0, i);
				i = ln.indexOf("//");
				String clazz = ln.substring(i+2);
				i = clazz.indexOf(' ');
				if(i != -1)clazz = clazz.substring(0, i);
				String v = srgToV.get(srg);
				Map<String, String> f = VtoF.get(v);
				i = clazz.indexOf("model");
				String fclazz = clazz;
				if(i != -1)fclazz = clazz.substring(0, i);
				String ffclazz = fclazz + "entitymodel";
				String bfclazz = PCKG + ffclazz;
				String vclazz = FtoVC.get(bfclazz);
				if(vclazz == null) {
					ffclazz = fclazz + "blockentitymodel";
					bfclazz = PCKG + ffclazz;
					vclazz = FtoVC.get(bfclazz);
				}
				if(vclazz == null) {
					ffclazz = fclazz + "model";
					bfclazz = PCKG + ffclazz;
					vclazz = FtoVC.get(bfclazz);
				}
				if(vclazz == null) {
					bfclazz = PCKG2 + ffclazz;
					vclazz = FtoVC.get(bfclazz);
				}
				if(vclazz == null) {
					bfclazz = FClassMapper.get(fclazz);
					vclazz = FtoVC.get(bfclazz);
				}
				System.out.println(ln + " vanilla: " + v + " fclass: " + bfclazz + " vclass: " + vclazz);
				if(vclazz == null) {
					System.out.println(fclazz);
					if(true)continue;
					String fclazzName = null;
					while(vclazz == null) {
						fclazzName = in.readLine();
						vclazz = FtoVC.get(fclazzName);
					}
					bfclazz = fclazzName;
					FClassMapper.put(fclazz, fclazzName);
				}
				String fV = f.get(vclazz);
				if(fV == null) {
					fV = FFieldMapper.get(srg);
				}
				if(!clazzPrinted.contains(ffclazz)) {
					wrc.print(ffclazz);
					wrc.print(';');
					wrc.print(clazz);
					wrc.print(';');
					String oc = FtoO.get(bfclazz);
					i = oc.lastIndexOf('/');
					wrc.print(oc.substring(i+1));
					wrc.println();
					wrc.flush();
					clazzPrinted.add(ffclazz);
				}
				System.out.println(srg + " " + fV);
				if(fV == null) {
					if(true)continue;
					while(fV == null || fV.isEmpty()) {
						fV = in.readLine();
					}
					FFieldMapper.put(srg, fV);
				}
				String fname = sp[0].replace(srg, fV);
				System.out.println(sp[0] + " " + fname);
				wr.print(fname);
				wr.print(';');
				wr.print(sp[1]);
				wr.print(' ');
				wr.print(bfclazz);
				wr.println();
				wr.flush();
			}
			classRd.close();
			PrintWriter classWr = new PrintWriter(new File(".", "fclass.map"));
			FClassMapper.forEach((k, v) -> {
				classWr.print("C;");
				classWr.print(k);
				classWr.print(';');
				classWr.print(v);
			});
			FFieldMapper.forEach((k, v) -> {
				classWr.print("F;");
				classWr.print(k);
				classWr.print(';');
				classWr.print(v);
			});
			classWr.close();
		} catch (IOException e) {
		}
	}

}
