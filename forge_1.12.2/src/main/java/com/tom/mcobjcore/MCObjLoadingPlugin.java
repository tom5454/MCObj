package com.tom.mcobjcore;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class MCObjLoadingPlugin implements IFMLLoadingPlugin {
	public static boolean deobf;
	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"com.tom.mcobjcore.MCObjTransformerService"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		Object deobf = data.get("runtimeDeobfuscationEnabled");
		if(deobf == Boolean.FALSE) {
			MCObjLoadingPlugin.deobf = true;
		}
		MCObjTransformerService.init();
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
