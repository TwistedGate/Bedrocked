package twistedgate.bedrocked.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;

public class ContainerBedrockBreaker extends Container{
	
	private final IInventory breakerInv;
	
	private int minHeight=1;
	private int maxHeight=6;
	private int radius=2;
	
	private int power=0;
	private int capacity=0;
	
	private int hits=0;
	private boolean noBedrock=false;
	private boolean enabled=true;
	
	public ContainerBedrockBreaker(InventoryPlayer playerInv, IInventory breakerInv){
		this.breakerInv=breakerInv;
	}
	
	@Override
	public void addListener(IContainerListener listener){
		super.addListener(listener);
		listener.sendAllWindowProperties(this, this.breakerInv);
	}
	
	@Override
	public void detectAndSendChanges(){
		this.listeners.forEach(listener->{
			if(this.radius!=this.breakerInv.getField(0)){
				listener.sendWindowProperty(this, 0, this.breakerInv.getField(0));
			}
			if(this.minHeight!=this.breakerInv.getField(1)){
				listener.sendWindowProperty(this, 1, this.breakerInv.getField(1));
			}
			if(this.maxHeight!=this.breakerInv.getField(2)){
				listener.sendWindowProperty(this, 2, this.breakerInv.getField(2));
			}
			if(this.power!=this.breakerInv.getField(3)){
				listener.sendWindowProperty(this, 3, this.breakerInv.getField(3));
			}
			if(this.capacity!=this.breakerInv.getField(4)){
				listener.sendWindowProperty(this, 4, this.breakerInv.getField(4));
			}
			if(this.hits!=this.breakerInv.getField(5)){
				listener.sendWindowProperty(this, 5, this.breakerInv.getField(5));
			}
			if(this.noBedrock!=(this.breakerInv.getField(6)==0?false:true)){
				listener.sendWindowProperty(this, 6, this.breakerInv.getField(6));
			}
			if(this.enabled!=(this.breakerInv.getField(7)==0?false:true)){
				listener.sendWindowProperty(this, 7, this.breakerInv.getField(7));
			}
		});
		
		this.radius=this.breakerInv.getField(0);
		this.minHeight=this.breakerInv.getField(1);
		this.maxHeight=this.breakerInv.getField(2);
		this.power=this.breakerInv.getField(3);
		this.capacity=this.breakerInv.getField(4);
		this.hits=this.breakerInv.getField(5);
		this.noBedrock=this.breakerInv.getField(6)==0?false:true;
		this.enabled=this.breakerInv.getField(7)==0?false:true;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return this.breakerInv.isUsableByPlayer(playerIn);
	}
}
