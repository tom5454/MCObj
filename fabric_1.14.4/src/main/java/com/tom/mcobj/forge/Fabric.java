package com.tom.mcobj.forge;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class Fabric {
	private static Map<String, String> nameMapper;
	public static Optional<Function<String, String>> findNameMapping() {
		if(nameMapper == null) {
			nameMapper = new HashMap<>();
			try (BufferedReader mappingRd = new BufferedReader(new InputStreamReader(Fabric.class.getResourceAsStream("/mappings/mappings.tiny")))) {
				String ln;
				while((ln = mappingRd.readLine()) != null){
					String[] sp = ln.split("\t");
					if(sp[0].equals("FIELD")) {
						String vname = sp[4];
						String name = sp.length > 5 ? sp[5] : vname;
						nameMapper.put(vname, name);
					}
				}
			} catch (IOException e) {
			}
		}
		return Optional.of(nameMapper::get);
	}
	//Forge ModelLoaderRegistry
	public static Identifier getActualLocation(Identifier location) {
		if(location instanceof ModelIdentifier) return location;
		if(location.getPath().startsWith("builtin/")) return location;
		return new Identifier(location.getNamespace(), "models/" + location.getPath());
	}
	//Forge OBJLoader
	public static enum OBJLoader {
		INSTANCE
		;
		public ResourceManager manager;
		private final Map<Identifier, OBJModel> cache = new HashMap<>();
		private final Map<Identifier, Exception> errors = new HashMap<>();

		public UnbakedModel loadModel(Identifier modelLocation) throws Exception
		{
			Identifier file = new Identifier(modelLocation.getNamespace(), modelLocation.getPath());
			if (!cache.containsKey(file))
			{
				Resource resource = null;
				try
				{
					try
					{
						resource = manager.getResource(file);
					}
					catch (FileNotFoundException e)
					{
						if (modelLocation.getPath().startsWith("models/block/"))
							resource = manager.getResource(new Identifier(file.getNamespace(), "models/item/" + file.getPath().substring("models/block/".length())));
						else if (modelLocation.getPath().startsWith("models/item/"))
							resource = manager.getResource(new Identifier(file.getNamespace(), "models/block/" + file.getPath().substring("models/item/".length())));
						else throw e;
					}
					OBJModel.Parser parser = new OBJModel.Parser(resource, manager);
					OBJModel model = null;
					try
					{
						model = parser.parse();
					}
					catch (Exception e)
					{
						errors.put(modelLocation, e);
					}
					finally
					{
						cache.put(modelLocation, model);
					}
				}
				finally
				{
					IOUtils.closeQuietly(resource);
				}
			}
			OBJModel model = cache.get(file);
			if (model == null) throw new RuntimeException("Error loading model previously: " + file, errors.get(modelLocation));
			return model;
		}
	}
	public interface IModelState {
		Optional<TRSRTransformation> apply(Optional<? extends Object> part);
	}
}
