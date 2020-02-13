package twistedgate.bedrocked.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import twistedgate.bedrocked.common.tileentity.TEBedrockBreaker;

public class BreakerToggleMessage extends BaseMessage{
	
	public BreakerToggleMessage(){super(null);}

	public BreakerToggleMessage(TEBedrockBreaker tileEntity){
		super(tileEntity.getPos());
	}
	
	public static class Handler implements IMessageHandler<BreakerToggleMessage, IMessage>{
		@Override
		public IMessage onMessage(BreakerToggleMessage message, MessageContext ctx){
			if(ctx.side==Side.SERVER){
				WorldServer world=ctx.getServerHandler().player.getServerWorld();
				BlockPos pos=message.pos;
				
				if(world.isBlockLoaded(pos)){
					TileEntity te=world.getTileEntity(pos);
					if(te instanceof TEBedrockBreaker){
						((TEBedrockBreaker)te).toggle();
					}
				}
			}
			return null;
		}
	}
}
