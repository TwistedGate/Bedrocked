package twistedgate.bedrocked.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class CEnergyStorage extends EnergyStorage{
	public CEnergyStorage(int capacity){
		super(capacity);
	}
	
	public CEnergyStorage(int capacity, int maxTransfer){
		super(capacity, maxTransfer);
	}
	
	public CEnergyStorage(int capacity, int maxReceive, int maxExtract){
		super(capacity, maxReceive, maxExtract);
	}
	
	public CEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy){
		super(capacity, maxReceive, maxExtract, energy);
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		compound.setInteger("storedenergy", this.energy);
		return compound;
	}
	
	public void readFromNBT(NBTTagCompound compound){
		this.energy=compound.getInteger("storedenergy");
	}
}
