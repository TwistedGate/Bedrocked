package twistedgate.bedrocked.common.blocks;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twistedgate.bedrocked.BRStuff;
import twistedgate.bedrocked.Bedrocked;
import twistedgate.bedrocked.common.tileentity.TEBedrockBreaker;
import twistedgate.bedrocked.common.tileentity.TEMachineBase;

public class BlockBedrockBreaker extends BRBlockBase implements ITileEntityProvider{
	public static final PropertyDirection FACING=PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool ACTIVE=PropertyBool.create("active");
	
	public BlockBedrockBreaker(){
		super(Material.IRON, "bedrockbreaker");
		
		setDefaultState(this.blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(ACTIVE, false)
				);
		
		BRStuff.ITEMS.add(new ItemBlock(this){
			@SideOnly(Side.CLIENT)
			@Override
			public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn){
				if(stack.hasTagCompound() && stack.getTagCompound().hasKey("power", NBT.TAG_COMPOUND)){
					NBTTagCompound tag=stack.getTagCompound().getCompoundTag("power");
					
					int stored=tag.getInteger("storedenergy");
					
					tooltip.add("§4Power: "+stored);
				}
			}
		}.setRegistryName(this.getRegistryName()));
	}
	
	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, FACING, ACTIVE){
			@Override
			protected StateImplementation createState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties){
				return new BedrockBreakerBlockState(block, properties);
			}
		};
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		IBlockState state=getDefaultState();
		
		state=state.withProperty(ACTIVE, meta>3);
		switch(meta%4){
			case 0:state=state.withProperty(FACING, EnumFacing.NORTH);
			case 1:state=state.withProperty(FACING, EnumFacing.EAST);
			case 2:state=state.withProperty(FACING, EnumFacing.SOUTH);
			case 3:state=state.withProperty(FACING, EnumFacing.WEST);
		}
		
		return state;
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		int meta;
		switch(state.getValue(FACING)){
			case WEST:	meta=3;break;
			case SOUTH:	meta=2;break;
			case EAST:	meta=1;break;
			default:	meta=0;break;
		}
		return meta+(state.getValue(ACTIVE)?4:0);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!playerIn.isSneaking() && (playerIn.getHeldItemMainhand()!=null && playerIn.getHeldItemMainhand().getItem()!=BRStuff.linker)){
			if(!worldIn.isRemote) playerIn.openGui(Bedrocked.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		if(stack.hasTagCompound()){
			NBTTagCompound tag=stack.getTagCompound();
			if(tag.hasKey("power", NBT.TAG_COMPOUND)){
				TEBedrockBreaker te=(TEBedrockBreaker)worldIn.getTileEntity(pos);
				te.getStorage().readFromNBT(tag.getCompoundTag("power"));
			}
		}
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
		return getDefaultState()
				.withProperty(ACTIVE, false)
				.withProperty(FACING, EnumFacing.fromAngle(placer.rotationYaw).getOpposite());
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player){}
	
	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion){
		super.onBlockExploded(world, pos, explosion);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		TEMachineBase machine=(TEMachineBase)world.getTileEntity(pos);
		
		NBTTagCompound main=new NBTTagCompound();
		
		NBTTagCompound tag=new NBTTagCompound();
		machine.getStorage().writeToNBT(tag);
		main.setTag("power", tag);
		
		drops.add(new ItemStack(Item.getItemFromBlock(this), 1, 0, main));
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new TEBedrockBreaker();
	}
	
	
	public static void updateState(World world, BlockPos pos, boolean active){
		IBlockState state=world.getBlockState(pos);
		
		if(state.getValue(ACTIVE)!=active){ // Only update if this condition is met.
			TileEntity te=world.getTileEntity(pos);

			world.setBlockState(pos, state.withProperty(ACTIVE, active));
			
			if(te!=null){
				te.validate();
				world.setTileEntity(pos, te);
			}
		}
	}
	
	public static class BedrockBreakerBlockState extends BlockStateContainer.StateImplementation{
		protected BedrockBreakerBlockState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties){
			super(block, properties);
		}
	}
}
