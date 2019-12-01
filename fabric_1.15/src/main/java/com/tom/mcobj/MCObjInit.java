package com.tom.mcobj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;

import com.tom.mcobj.proxy.ClientProxy;
import com.tom.mcobj.proxy.CommonProxy;

public class MCObjInit implements ClientModInitializer {
	public static final String modid = "mcobj";
	public static final String modName = "Minecraft .obj Loader";
	public static MCObjInit modInstance;

	public static CommonProxy proxy;

	public static final Logger log = LogManager.getLogger(modName);

	public MCObjInit() {
		modInstance = this;
		// Register the setup method for modloading
		proxy = new ClientProxy();
	}

	@Override
	public void onInitializeClient() {
		log.info("Start Init");
		long tM = System.currentTimeMillis();
		proxy.construct();
		long time = System.currentTimeMillis() - tM;
		log.info("Init took in " + time + " milliseconds");
	}

	public void setup() {
		log.info("Start Setup");
		long tM = System.currentTimeMillis();
		proxy.setup();
		long time = System.currentTimeMillis() - tM;
		log.info("Setup took in " + time + " milliseconds");
	}
}
