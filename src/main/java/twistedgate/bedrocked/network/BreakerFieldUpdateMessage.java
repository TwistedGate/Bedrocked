package twistedgate.bedrocked.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import twistedgate.bedrocked.common.tileentity.TEBedrockBreaker;

public class BreakerFieldUpdateMessage implements IMessage{
	public BreakerFieldUpdateMessage(){}
	
	private BlockPos pos;
	private byte fieldId;
	private byte fieldValue;
	public BreakerFieldUpdateMessage(BlockPos pos, int id, int value){
		this.pos=pos;
		this.fieldId=(byte)(id&0xFF);
		this.fieldValue=(byte)(value&0xFF);
	}
	
	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(this.pos.getX());
		buf.writeInt(this.pos.getY());
		buf.writeInt(this.pos.getZ());
		
		// Deflate into a 24-Bit int
		buf.writeBytes(new byte[]{this.fieldId, this.fieldValue});
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		int x=buf.readInt();
		int y=buf.readInt();
		int z=buf.readInt();
		this.pos=new BlockPos(x,y,z);
		
		// Inflate from the 24-Bit int
		this.fieldId	=buf.readByte();
		this.fieldValue	=buf.readByte();
	}
	
	public static class Handler implements IMessageHandler<BreakerFieldUpdateMessage, IMessage>{
		
		public Handler(){}
		
		@Override
		public IMessage onMessage(final BreakerFieldUpdateMessage message, final MessageContext ctx){
			if(ctx.side==Side.SERVER){
				WorldServer world=ctx.getServerHandler().player.getServerWorld();
				BlockPos pos=message.pos;
				
				if(world.isBlockLoaded(pos)){
					TileEntity te=world.getTileEntity(pos);
					if(te instanceof TEBedrockBreaker){
						TEBedrockBreaker breaker=(TEBedrockBreaker)te;
						
						// Both id and value get casted to int then with bit-and pulled to 8-Bit, to avoid negative numbers.
						int id=message.fieldId&0xFF;
						int va=message.fieldValue&0xFF;
						
						breaker.setField(id, va);
						
						breaker.markDirty();
						
						IBlockState state=world.getBlockState(pos);
						world.notifyBlockUpdate(pos, state, state, 3);
					}
				}
			}
			return null;
		}
	}
}