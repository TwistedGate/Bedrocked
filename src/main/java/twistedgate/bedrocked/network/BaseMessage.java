package twistedgate.bedrocked.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class BaseMessage implements IMessage{
	
	protected BlockPos pos;
	public BaseMessage(BlockPos pos){
		this.pos=pos;
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
}
