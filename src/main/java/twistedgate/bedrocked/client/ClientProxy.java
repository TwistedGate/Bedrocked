package twistedgate.bedrocked.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import twistedgate.bedrocked.common.CommonProxy;

public class ClientProxy extends CommonProxy{
	
	@Override
	public void preInitEnd(FMLPreInitializationEvent event){
	}
	
	@Override
	public void initStart(FMLInitializationEvent event){
		ClientEventHandler handler=new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
	}
}
