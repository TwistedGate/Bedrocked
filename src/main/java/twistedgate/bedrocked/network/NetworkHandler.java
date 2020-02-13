package twistedgate.bedrocked.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import twistedgate.bedrocked.BRInfo;

public class NetworkHandler{
	public static final SimpleNetworkWrapper NET_WRAPPER=NetworkRegistry.INSTANCE.newSimpleChannel(BRInfo.ID);
	
	public static void init(){
		NET_WRAPPER.registerMessage(BreakerFieldUpdateMessage.Handler.class, BreakerFieldUpdateMessage.class, 0, Side.SERVER);
		NET_WRAPPER.registerMessage(BreakerResetMessage.Handler.class, BreakerResetMessage.class, 1, Side.SERVER);
		NET_WRAPPER.registerMessage(BreakerToggleMessage.Handler.class, BreakerToggleMessage.class, 2, Side.SERVER);
	}
	
	public static void sendToServer(IMessage message){
		NET_WRAPPER.sendToServer(message);
	}
}
