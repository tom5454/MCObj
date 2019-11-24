package com.tom.mcobj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.tom.mcobj.proxy.CommonProxy;

@Mod(modid = MCObjInit.modid, name = MCObjInit.modName, clientSideOnly = true)
public class MCObjInit {
	public static final String modid = "mcobj";
	public static final String modName = "Minecraft .obj Loader";
	public static MCObjInit modInstance;


	@SidedProxy(clientSide = "com.tom.mcobj.proxy.ClientProxy")
	public static CommonProxy proxy;

	public static final Logger log = LogManager.getLogger(modName);

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		proxy.construct();
		MinecraftForge.EVENT_BUS.register(proxy);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		log.info("Start Post Init");
		long tM = System.currentTimeMillis();
		proxy.setup();
		long time = System.currentTimeMillis() - tM;
		log.info("Post Init took in " + time + " milliseconds");
	}
}
