package twistedgate.bedrocked.common.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import twistedgate.bedrocked.common.blocks.BRBlockBase;
import twistedgate.bedrocked.common.tileentity.TEMachineBase;

public class ItemMaybe extends BRItemBase{
	public ItemMaybe(){
		super("maybe");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		return super.onItemRightClick(world, player, hand);
	}
	
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand){
		if(world.isRemote) return EnumActionResult.PASS;
		
		if(world.getBlockState(pos).getBlock() instanceof BRBlockBase){
			TEMachineBase machine=(TEMachineBase)world.getTileEntity(pos);
			
			int stored=machine.getStorage().getEnergyStored();
			int cap=machine.getStorage().getMaxEnergyStored();
			
			player.sendStatusMessage(new TextComponentString("Power: "+stored+"/"+cap), true);
			
			return EnumActionResult.SUCCESS;
		}
		
		return EnumActionResult.FAIL;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
}
