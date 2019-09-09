package com.tom.mcobj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.tom.mcobj.proxy.ClientProxy;
import com.tom.mcobj.proxy.CommonProxy;
import com.tom.mcobj.proxy.ServerProxy;

@Mod(MCObjInit.modid)
public class MCObjInit {
	public static final String modid = "mcobj";
	public static final String modName = "Minecraft .obj Loader";
	public static MCObjInit modInstance;

	public static CommonProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

	public static final Logger log = LogManager.getLogger(modName);

	public MCObjInit() {
		modInstance = this;
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(proxy);
		FMLJavaModLoadingContext.get().getModEventBus().register(proxy);

		proxy.construct();
	}
	private void setup(final FMLCommonSetupEvent event) {
		log.info("Start Setup");
		long tM = System.currentTimeMillis();
		proxy.setup();
		long time = System.currentTimeMillis() - tM;
		log.info("Setup took in " + time + " milliseconds");
	}
}
