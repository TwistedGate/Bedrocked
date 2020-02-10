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
	private int[] array;
	public BreakerFieldUpdateMessage(TEBedrockBreaker breaker){
		this.pos=breaker.getPos();
		this.array=new int[]{
				breaker.getField(0),
				breaker.getField(1),
				breaker.getField(2),
		};
	}
	
	public BlockPos getPos(){
		return this.pos.toImmutable();
	}
	
	public int[] getArray(){
		return this.array;
	}
	
	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(this.pos.getX());
		buf.writeInt(this.pos.getY());
		buf.writeInt(this.pos.getZ());
		
		// Write size of array and it's content
		buf.writeInt(this.array.length);
		for(int i=0;i<this.array.length;i++)
			buf.writeInt(this.array[i]);
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		int x=buf.readInt();
		int y=buf.readInt();
		int z=buf.readInt();
		this.pos=new BlockPos(x,y,z);
		
		// Read size of array and it's content
		int[] array=new int[buf.readInt()];
		for(int i=0;i<array.length;i++)
			array[i]=buf.readInt();
		this.array=array;
	}
	
	public static class Handler implements IMessageHandler<BreakerFieldUpdateMessage, IMessage>{
		
		public Handler(){}
		
		@Override
		public IMessage onMessage(final BreakerFieldUpdateMessage message, final MessageContext ctx){
			if(ctx.side==Side.SERVER){
				WorldServer world=ctx.getServerHandler().player.getServerWorld();
				BlockPos pos=message.getPos();
				
				if(world.isBlockLoaded(pos)){
					TileEntity te=world.getTileEntity(pos);
					if(te instanceof TEBedrockBreaker){
						TEBedrockBreaker breaker=(TEBedrockBreaker)te;
						
						int[] array=message.getArray();
						for(int i=0;i<array.length;i++)
							breaker.setField(i, array[i]);
						
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