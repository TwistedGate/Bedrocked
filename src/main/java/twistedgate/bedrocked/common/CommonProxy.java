package twistedgate.bedrocked.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy{
	public void preInitStart(FMLPreInitializationEvent event){}
	public void preInitEnd(FMLPreInitializationEvent event){}
	
	public void initEnd(FMLInitializationEvent event){}
	public void initStart(FMLInitializationEvent event){}
	
	public void postInitEnd(FMLPostInitializationEvent event){}
	public void postInitStart(FMLPostInitializationEvent event){}
}
