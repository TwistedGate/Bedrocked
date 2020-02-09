package twistedgate.bedrocked.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import twistedgate.bedrocked.energy.CEnergyStorage;

public class TEMachineBase extends TileEntity{
	protected CEnergyStorage energyStorage;
	public TEMachineBase(CEnergyStorage energyStorage){
		this.energyStorage=energyStorage;
	}
	
	public CEnergyStorage getStorage(){
		return this.energyStorage;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == CapabilityEnergy.ENERGY)
			return (T) this.energyStorage;
		return super.getCapability(capability, facing);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		if(capability == CapabilityEnergy.ENERGY)
			return true;
		return super.hasCapability(capability, facing);
	}
	
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		this.energyStorage.writeToNBT(compound);
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound){
		this.energyStorage.readFromNBT(compound);
		super.readFromNBT(compound);
	}
}
