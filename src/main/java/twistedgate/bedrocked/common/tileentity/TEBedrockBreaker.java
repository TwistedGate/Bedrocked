package twistedgate.bedrocked.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import twistedgate.bedrocked.common.blocks.BlockBedrockBreaker;
import twistedgate.bedrocked.energy.CEnergyStorage;

public class TEBedrockBreaker extends TEMachineBase implements ITickable, ISidedInventory{
	public static final int REQUIRED_MIN_ENERGY=512;
	public static final int REQUIRED_HITS=5120;
	
	public int minHeight=1;
	public int maxHeight=6;
	public int radius=2;
	public int hits=0;
	public BlockPos workedPos=null;
	
	/** If no bedrock was found the machine considers the area cleared and won't check for more. */
	public boolean noBedrock=false;
	
	public TEBedrockBreaker(){
		super(new CEnergyStorage(REQUIRED_HITS*REQUIRED_MIN_ENERGY));
	}
	
	@Override
	public void update(){
		if(this.world.isRemote) return;
		if(this.noBedrock) return;
		
		for(EnumFacing side:EnumFacing.values()){
			if(this.world.getBlockState(this.pos.offset(side)).getWeakPower(this.world, this.pos, side)!=0){
				IBlockState state=this.world.getBlockState(this.pos);
				if(state.getValue(BlockBedrockBreaker.ACTIVE)){
					BlockBedrockBreaker.updateState(this.world, this.pos, false);
				}
				return;
			}
		}
		
		boolean dirty=false;
		
		if(this.workedPos==null){
			findBedrock();
			dirty=true;
		}else{
			boolean canRun=this.energyStorage.getEnergyStored()>=REQUIRED_MIN_ENERGY;
			
			if(canRun){
				if(this.energyStorage.extractEnergy(REQUIRED_MIN_ENERGY, false)==REQUIRED_MIN_ENERGY){
					this.hits++;
					dirty=true;
				}
				
				if(this.hits>=REQUIRED_HITS){
					if(this.world.destroyBlock(this.workedPos, false)){
						this.workedPos=null;
						this.hits=0;
						dirty=true;
						
						if(Math.random()<=0.01D)
							this.world.spawnEntity(new EntityItem(this.world, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, new ItemStack(Blocks.BEDROCK,1,0)));
					}
				}
			}
			
			BlockBedrockBreaker.updateState(this.world, this.pos, canRun);
		}
		
		if(dirty){
			markDirty();
		}
	}
	
	private void findBedrock(){
		int rad=this.radius;
		int a=this.minHeight;
		int b=this.maxHeight;
		
		for(int y=a;y<=b;y++){
			for(int z=-rad;z<=rad;z++){
				for(int x=-rad;x<=rad;x++){
					BlockPos pos=this.pos.offset(EnumFacing.NORTH, x)
										 .offset(EnumFacing.EAST, z)
										 .offset(EnumFacing.DOWN, y);
					
					if(pos.getY()==0){
						this.noBedrock=true;
						return;
					}
					
					IBlockState state=this.world.getBlockState(pos);
					if(state!=null && state.getBlock()==Blocks.BEDROCK){
						this.workedPos=pos;
						this.noBedrock=false;
						return;
					}
				}
			}
		}
		
		this.noBedrock=true;
	}
	
	@Override
	public void markDirty(){
		super.markDirty();
		
		IBlockState state=this.world.getBlockState(this.pos);
		this.world.notifyBlockUpdate(this.pos, state, state, 3);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		super.writeToNBT(compound);
		compound.setShort("minHeight", (short)this.minHeight);
		compound.setShort("maxHeight", (short)this.maxHeight);
		compound.setShort("radius", (short)this.radius);
		compound.setShort("hits", (short)this.hits);
		compound.setBoolean("areadone", this.noBedrock);
		if(this.workedPos!=null){
			NBTTagCompound coords=new NBTTagCompound();
			coords.setInteger("x", this.workedPos.getX());
			coords.setInteger("y", this.workedPos.getY());
			coords.setInteger("z", this.workedPos.getZ());
			compound.setTag("breaking", coords);
		}
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound){
		super.readFromNBT(compound);
		this.minHeight=compound.getShort("minHeight");
		this.maxHeight=compound.getShort("maxHeight");
		this.radius=compound.getShort("radius");
		this.hits=compound.getShort("hits");
		this.noBedrock=compound.getBoolean("areadone");
		
		if(compound.hasKey("breaking", NBT.TAG_COMPOUND)){
			NBTTagCompound coords=(NBTTagCompound) compound.getTag("breaking");
			int x=coords.getInteger("x");
			int y=coords.getInteger("y");
			int z=coords.getInteger("z");
			this.workedPos=new BlockPos(x, y, z);
		}
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket(){
		NBTTagCompound tag=new NBTTagCompound();
		this.writeToNBT(tag);
		return new SPacketUpdateTileEntity(this.pos, 0, tag);
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag){
		this.readFromNBT(tag);
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound tag=new NBTTagCompound();
		this.writeToNBT(tag);
		return tag;
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
		switch(pkt.getTileEntityType()){
			case 0:{
				readFromNBT(pkt.getNbtCompound());
				return;
			}
			case 3:{
				NBTTagCompound tag=pkt.getNbtCompound();
				
				setField(0, tag.getInteger("0"));
				setField(1, tag.getInteger("1"));
				setField(2, tag.getInteger("2"));
				return;
			}
		}
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player){
		if(this.world.getTileEntity(this.pos)!=this){
			return false;
		}else{
			return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}
	
	@Override
	public int getField(int id){
		switch(id){
			case 0: return this.radius;
			case 1: return this.minHeight;
			case 2: return this.maxHeight;
			case 3: return this.energyStorage.getEnergyStored();
			case 4: return this.energyStorage.getMaxEnergyStored();
			case 5: return this.hits;
			case 6: return this.noBedrock?1:0;
			default: return 0;
		}
	}
	
	@Override
	public void setField(int id, int value){
		switch(id){
			case 0: this.radius=value; break;
			case 1: this.minHeight=value; break;
			case 2: this.maxHeight=value; break;
			// 3 & 4 not nessesary.
			case 5: this.hits=value; break;
			case 6: this.noBedrock=value>0?true:false; break;
		}
	}
	
	@Override
	public int getFieldCount(){
		return 7;
	}
	
	@Override
	public int getSizeInventory(){
		return 0;
	}
	
	@Override
	public boolean isEmpty(){
		return false;
	}
	
	@Override
	public ItemStack getStackInSlot(int index){
		return null;
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count){
		return null;
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index){
		return null;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack){}
	
	@Override
	public int getInventoryStackLimit(){
		return 0;
	}
	
	@Override
	public void openInventory(EntityPlayer player){}
	
	@Override
	public void closeInventory(EntityPlayer player){}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}
	
	@Override
	public void clear(){
	}
	
	@Override
	public String getName(){
		return null;
	}
	
	@Override
	public boolean hasCustomName(){
		return false;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return null;
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return false;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}
}