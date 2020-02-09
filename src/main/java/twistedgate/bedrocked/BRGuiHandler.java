package twistedgate.bedrocked;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import twistedgate.bedrocked.client.gui.GuiBedrockBreaker;
import twistedgate.bedrocked.common.container.ContainerBedrockBreaker;
import twistedgate.bedrocked.common.tileentity.TEBedrockBreaker;

public class BRGuiHandler implements IGuiHandler{
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		switch(ID){
			case 0: return new ContainerBedrockBreaker(player.inventory, (TEBedrockBreaker)world.getTileEntity(new BlockPos(x,y,z)));
		}
		
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		switch(ID){
			case 0:{
				if(!world.isRemote)
					return getServerGuiElement(ID, player, world, x, y, z);
				
				return new GuiBedrockBreaker(player.inventory, (TEBedrockBreaker)world.getTileEntity(new BlockPos(x,y,z)));
			}
		}
		return null;
	}
}
