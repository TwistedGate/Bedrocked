package twistedgate.bedrocked.common.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import twistedgate.bedrocked.BRStuff;
import twistedgate.bedrocked.common.tileentity.TEDebugGenerator;

public class BlockDebugGenerator extends BRBlockBase implements ITileEntityProvider{
	public BlockDebugGenerator(){
		super(Material.IRON, "debuggenerator");
		
		BRStuff.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new TEDebugGenerator();
	}
}
