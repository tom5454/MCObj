package com.tom.mcobj.proxy;

import net.minecraft.world.World;

public class ServerProxy extends CommonProxy {

	@Override
	public World getWorld() {
		throw new UnsupportedOperationException();
	}

}
