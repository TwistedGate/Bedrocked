package twistedgate.bedrocked.client.gui;

import java.io.IOException;
import java.util.Locale;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twistedgate.bedrocked.BRInfo;
import twistedgate.bedrocked.common.container.ContainerBedrockBreaker;
import twistedgate.bedrocked.common.tileentity.TEBedrockBreaker;

@SideOnly(Side.CLIENT)
public class GuiBedrockBreaker extends GuiContainer{
	private static final ResourceLocation GUI_TEXTURE=new ResourceLocation(BRInfo.ID, "textures/gui/bedrockbreaker.png");
	
	private final TEBedrockBreaker tileEntity;
	
	public GuiBedrockBreaker(InventoryPlayer playerInv, TEBedrockBreaker tileEntity){
		super(new ContainerBedrockBreaker(playerInv, tileEntity));
		this.tileEntity=tileEntity;
		this.xSize=176;
		this.ySize=166;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		
		int x=(this.width-this.xSize)/2;
		int y=(this.height-this.ySize)/2;
		
		addButton(Button.RAD_DEC.create(x+114, y+7, 10, 10, "-"));
		addButton(Button.RAD_INC.create(x+125, y+7, 10, 10, "+"));
		
		addButton(Button.MIN_DEC.create(x+114, y+18, 10, 10, "-"));
		addButton(Button.MIN_INC.create(x+125, y+18, 10, 10, "+"));
		
		addButton(Button.MAX_DEC.create(x+114, y+29, 10, 10, "-"));
		addButton(Button.MAX_INC.create(x+125, y+29, 10, 10, "+"));
		
		GuiLabel label=new GuiLabel(this.fontRenderer, 0, x+137, y+13, 0, 0, 0xFFFFFF);
		label.addLine("Radius");
		this.labelList.add(label);
		
		label=new GuiLabel(this.fontRenderer, 0, x+137, y+24, 0, 0, 0xFFFFFF);
		label.addLine("Min");
		this.labelList.add(label);
		
		label=new GuiLabel(this.fontRenderer, 0, x+137, y+35, 0, 0, 0xFFFFFF);
		label.addLine("Max");
		this.labelList.add(label);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException{
		boolean changed=false;
		switch(Button.fromId(button.id)){
			case RAD_INC:{
				int value=this.tileEntity.getField(0)+1;
				
				if(value>32)
					value=32;
				
				this.tileEntity.setField(0, value);
				changed=true;
				break;
			}
			case RAD_DEC:{
				int value=this.tileEntity.getField(0)-1;
				
				if(value<1)
					value=1;
				
				this.tileEntity.setField(0, value);
				changed=true;
				break;
			}
			case MIN_INC:{
				int value=this.tileEntity.getField(1)+1;
				
				if(value>this.tileEntity.getField(2))
					value=this.tileEntity.getField(2);
				
				this.tileEntity.setField(1, value);
				changed=true;
				break;
			}
			case MIN_DEC:{
				int value=this.tileEntity.getField(1)-1;
				
				if(value<1)
					value=1;
				
				this.tileEntity.setField(1, value);
				changed=true;
				break;
			}
			case MAX_INC:{
				int value=this.tileEntity.getField(2)+1;
				
				if(value>255)
					value=255;
				
				this.tileEntity.setField(2, value);
				changed=true;
				break;
			}
			case MAX_DEC:{
				int value=this.tileEntity.getField(2)-1;
				
				if(value<this.tileEntity.getField(1))
					value=this.tileEntity.getField(1);
				
				this.tileEntity.setField(2, value);
				changed=true;
				break;
			}
		}
		
		if(changed){
			// TODO: Find a fucking way to send changes made by the user to the server.
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
        this.drawDefaultBackground();
        this.renderHoveredToolTip(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1, 1);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		int x=(this.width-this.xSize)/2;
		int y=(this.height-this.ySize)/2;
		drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		
		String[] lines={
				"Radius: "+this.tileEntity.getField(0),
				"Min: "+this.tileEntity.getField(1),
				"Max: "+this.tileEntity.getField(2),
				"Power: "+compresstoString(this.tileEntity.getField(3), false)+"/"+compresstoString(this.tileEntity.getField(4), true),
				"Progress: "+String.format(Locale.ENGLISH, "%.2f", this.tileEntity.getField(5)/(float)TEBedrockBreaker.REQUIRED_HITS*100)+"%",
				"Done: "+(this.tileEntity.getField(6)>0?"Yes.":"No."),
		};
		
		for(int i=0;i<lines.length;i++)
			drawString(this.fontRenderer, lines[i], x+9, y+9+(8*i), 0x00AF00);
	}
	
	private String compresstoString(int power, boolean usePost){
		if(power>=1000000000)	return String.format(Locale.ENGLISH, usePost?"%.1fGRF":"%.1f", power/(float)1000000000);
		if(power>=1000000)		return String.format(Locale.ENGLISH, usePost?"%.1fMRF":"%.1f", power/(float)1000000);
		if(power>=1000)			return String.format(Locale.ENGLISH, usePost?"%.1fKRF":"%.1f", power/(float)1000);
		return power+(usePost?"RF":"");
	}
	
	
	protected enum Button{
		RAD_INC, RAD_DEC, // Radius Control (Increment and Decrement)
		MIN_INC, MIN_DEC, // Min Height Control (Increment and Decrement)
		MAX_INC, MAX_DEC; // Max Height Control (Increment and Decrement)
		
		protected GuiButtonExt create(int xPos, int yPos, int width, int height, String displayString){
			return new GuiButtonExt(this.ordinal(), xPos, yPos, width, height, displayString);
		}
		
		protected static Button fromId(int id){
			return values()[id];
		}
	}
}
