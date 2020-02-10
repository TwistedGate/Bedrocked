package twistedgate.bedrocked.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import twistedgate.bedrocked.common.tileentity.TEBedrockBreaker;

public class BreakerResetMessage implements IMessage{
	
	public BreakerResetMessage(){}
	
	private BlockPos pos;
	public BreakerResetMessage(TEBedrockBreaker tileEntity){
		this.pos=tileEntity.getPos();
	}
	
	public BlockPos getPos(){
		return this.pos.toImmutable();
	}
	
	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(this.pos.getX());
		buf.writeInt(this.pos.getY());
		buf.writeInt(this.pos.getZ());
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		int x=buf.readInt();
		int y=buf.readInt();
		int z=buf.readInt();
		this.pos=new BlockPos(x,y,z);
		
	}
	
	public static class Handler implements IMessageHandler<BreakerResetMessage, IMessage>{
		@Override
		public IMessage onMessage(BreakerResetMessage message, MessageContext ctx){
			if(ctx.side==Side.SERVER){
				WorldServer world=ctx.getServerHandler().player.getServerWorld();
				BlockPos pos=message.getPos();
				
				if(world.isBlockLoaded(pos)){
					TileEntity te=world.getTileEntity(pos);
					if(te instanceof TEBedrockBreaker){
						TEBedrockBreaker breaker=(TEBedrockBreaker)te;
						breaker.softReset();
					}
				}
			}
			return null;
		}
	}
}
