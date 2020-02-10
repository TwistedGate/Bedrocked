package twistedgate.bedrocked.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import twistedgate.bedrocked.common.blocks.BRBlockBase;
import twistedgate.bedrocked.energy.CEnergyStorage;

public class TEDebugGenerator extends TEMachineBase implements ITickable{
	public TEDebugGenerator(){
		super(new CEnergyStorage(Integer.MAX_VALUE-1,Integer.MAX_VALUE-1,Integer.MAX_VALUE-1,Integer.MAX_VALUE-1));
	}
	
	@Override
	public void update(){
		if(this.world.isRemote) return;
		
		for(EnumFacing facing:EnumFacing.values()){
			BlockPos pos=this.pos.offset(facing);
			IBlockState state=this.world.getBlockState(pos);
			
			if(state!=null && state.getBlock() instanceof BRBlockBase){
				TEMachineBase machine=(TEMachineBase) this.world.getTileEntity(pos);
				
				machine.energyStorage.receiveEnergy(2048, false);
				machine.markDirty();
			}
		}
	}
}
