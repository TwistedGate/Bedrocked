package twistedgate.bedrocked.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Predicate;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twistedgate.bedrocked.BRInfo;
import twistedgate.bedrocked.common.container.ContainerBedrockBreaker;
import twistedgate.bedrocked.common.tileentity.TEBedrockBreaker;
import twistedgate.bedrocked.energy.EnergyUtils;
import twistedgate.bedrocked.network.BreakerFieldUpdateMessage;
import twistedgate.bedrocked.network.BreakerResetMessage;
import twistedgate.bedrocked.network.BreakerToggleMessage;
import twistedgate.bedrocked.network.NetworkHandler;

@SideOnly(Side.CLIENT)
public class GuiBedrockBreaker extends GuiContainer{
	private static final ResourceLocation GUI_TEXTURE=new ResourceLocation(BRInfo.ID, "textures/gui/bedrockbreaker.png");
	
	private final TEBedrockBreaker tileEntity;
	private final ArrayList<GuiTextField> guiStuff=new ArrayList<>();
	
	public GuiBedrockBreaker(InventoryPlayer playerInv, TEBedrockBreaker tileEntity){
		super(new ContainerBedrockBreaker(playerInv, tileEntity));
		this.tileEntity=tileEntity;
		this.xSize=136;
		this.ySize=166;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		this.guiStuff.clear();
		
		int x=(this.width-this.xSize)/2;
		int y=(this.height-this.ySize)/2;
		
		addButton(Button.RAD_DEC.create(x+5, y+75, 10, 10, "-"));
		addButton(Button.RAD_INC.create(x+45, y+75, 10, 10, "+"));
		
		addButton(Button.MIN_DEC.create(x+5, y+87, 10, 10, "-"));
		addButton(Button.MIN_INC.create(x+45, y+87, 10, 10, "+"));
		
		addButton(Button.MAX_DEC.create(x+5, y+99, 10, 10, "-"));
		addButton(Button.MAX_INC.create(x+45, y+99, 10, 10, "+"));
		
		addButton(Button.TOGGLE.create(x+121, y+5, 10, 10, "T"));
		addButton(Button.SOFT_RESET.create(x+121, y+17, 10, 10, "R"));
		
		
		// Validator for text fields only allowing digits (ie 0-9)
		Predicate<String> numval=new Predicate<String>(){
			@Override
			public boolean apply(String input){
				for(int i=0;i<input.length();i++){
					if(!Character.isDigit(input.charAt(i)))
						return false;
				}
				return true;
			}
		};
		
		GuiTextField field0=addGuiThing(new GuiTextField(0, this.fontRenderer, x+17, y+76, 26, 8));
		field0.setText(""+this.tileEntity.getField(0));
		field0.setValidator(numval);
		field0.setMaxStringLength(3);
		
		GuiTextField field1=addGuiThing(new GuiTextField(1, this.fontRenderer, x+17, y+88, 26, 8));
		field1.setText(""+this.tileEntity.getField(1));
		field1.setValidator(numval);
		field1.setMaxStringLength(3);
		
		GuiTextField field2=addGuiThing(new GuiTextField(2, this.fontRenderer, x+17, y+100, 26, 8));
		field2.setText(""+this.tileEntity.getField(2));
		field2.setValidator(numval);
		field2.setMaxStringLength(3);
	}
	
	public GuiTextField addGuiThing(GuiTextField gui){
		this.guiStuff.add(gui);
		return gui;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		if(!this.guiStuff.isEmpty())
			for(GuiTextField gui:this.guiStuff)
				gui.drawTextBox();
		
		renderHovertexts(mouseX, mouseY);
		
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	private void renderHovertexts(int mouseX, int mouseY){
		int x=(this.width-this.xSize)/2;
		int y=(this.height-this.ySize)/2;
		if((mouseX>=(x+6) && mouseX<=(x+9)) && (mouseY>=(y+6) && mouseY<=(y+69))){
			int minPower=this.tileEntity.getField(3);
			int maxPower=this.tileEntity.getField(4);
			
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
				drawHoveringText("§d"+minPower+"/"+maxPower+"RF", mouseX, mouseY);
			}else{
				drawHoveringText("§d"+EnergyUtils.toString(minPower, true)+"/"+EnergyUtils.toString(maxPower, true), mouseX, mouseY);
			}
		}
		
		// TODO: Hovertext for: Radius, Min Y and Max Y Controlls.
	}
	
	private void renderBedrock(int x, int y, float partialTicks){
		ItemStack bedrock=new ItemStack(Blocks.BEDROCK);
		
		float f=32F*(this.tileEntity.getField(5)/(float)TEBedrockBreaker.REQUIRED_HITS);
		
		TextureManager textureManager=this.mc.getTextureManager();
		
		GlStateManager.pushMatrix();
		{
			GlStateManager.translate((float)x, (float)y, 100.0F+this.itemRender.zLevel);
			GlStateManager.scale(1.0F, -1.0F, 1.0F);
	        GlStateManager.rotate(11.25F, 1.0F, 0.0F, 0.0F);
	        GlStateManager.rotate(45F, 0.0F, 1.0F, 0.0F);
	        GlStateManager.scale(32.0F-f, 32.0F-f, 32.0F-f);
	        
	        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
	        
	        this.itemRender.renderItem(bedrock, this.itemRender.getItemModelWithOverrides(bedrock, null, null));
		}	
		GlStateManager.popMatrix();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1, 1);
		int x=(this.width-this.xSize)/2;
		int y=(this.height-this.ySize)/2;
		
		if(!this.tileEntity.noBedrock && this.tileEntity.getField(5)>0)
			renderBedrock(x+66, y+38, partialTicks);
		
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		
		drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize); // Power storage display.
		
		int minPower=this.tileEntity.getField(3);
		int maxPower=this.tileEntity.getField(4);
		int bar=(int)Math.floor(minPower/(float)maxPower*64);
		
		drawTexturedModalRect(x+6, y+70-bar, 136, 64-bar, 4, bar);
		
		
		boolean enabled=this.tileEntity.getField(7)!=0;
		String[] lines;
		if(enabled){
			lines=new String[]{
					"Done: "+(this.tileEntity.getField(6)>0?"Yes.":"No."),
					"-----------------",
					"Radius: "+this.tileEntity.getField(0),
					"Min: "+this.tileEntity.getField(1),
					"Max: "+this.tileEntity.getField(2),
					"-----------------",
					"Progress: "+String.format(Locale.ENGLISH, "%.2f", this.tileEntity.getField(5)/(float)TEBedrockBreaker.REQUIRED_HITS*100)+"%",
			};
		}else{
			lines=new String[]{
					"Breaker Disabled!",
					"Press T to Enable."
			};
		}
		
		if(lines!=null)
			for(int i=0;i<lines.length;i++)
				drawString(this.fontRenderer, lines[i], x-110, y+7+(8*i), enabled?0x00AF00:0xAF0000);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(!this.guiStuff.isEmpty())
			for(GuiTextField gui:this.guiStuff)
				gui.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
		super.keyTyped(typedChar, keyCode);
		if(!this.guiStuff.isEmpty())
			for(int i=0;i<this.guiStuff.size();i++){
				GuiTextField gui=this.guiStuff.get(i);
				if(gui.isFocused()){
					if(keyCode==Keyboard.KEY_NUMPADENTER || keyCode==Keyboard.KEY_RETURN){
						gui.setFocused(false);
						
						int value=Integer.valueOf(gui.getText());
						int id=gui.getId();
						switch(id){
							case 0:{
								if(value<1) value=1;
								if(value>16) value=16;
								break;
							}
							case 1:{
								if(value<1) value=1;
								if(value>this.tileEntity.getField(2)) value=this.tileEntity.getField(2);
								break;
							}
							case 2:{
								if(value>255) value=255;
								if(value<this.tileEntity.getField(1)) value=this.tileEntity.getField(1);
								break;
							}
						}
						
						this.tileEntity.setField(id, value);
						gui.setText(""+value);
						NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), id, value));
						return;
					}else{
						gui.textboxKeyTyped(typedChar, keyCode);
					}
				}
			}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException{
		// Change incremental/decremental value to 10 when a shift key is held down.
		int offset=(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))?10:1;
		switch(Button.fromId(button.id)){
			case SOFT_RESET:{
				NetworkHandler.sendToServer(new BreakerResetMessage(this.tileEntity));
				//this.inventorySlots.detectAndSendChanges();
				return;
			}
			case TOGGLE:{
				NetworkHandler.sendToServer(new BreakerToggleMessage(this.tileEntity));
				//this.inventorySlots.detectAndSendChanges();
				break;
			}
			case RAD_INC:{
				int value=this.tileEntity.getField(0)+offset;
				
				if(value>16)
					value=16;
				
				this.tileEntity.setField(0, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 0, value));
				break;
			}
			case RAD_DEC:{
				int value=this.tileEntity.getField(0)-offset;
				
				if(value<1)
					value=1;
				
				this.tileEntity.setField(0, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 0, value));
				break;
			}
			case MIN_INC:{
				int value=this.tileEntity.getField(1)+offset;
				
				if(value>this.tileEntity.getField(2))
					value=this.tileEntity.getField(2);
				
				this.tileEntity.setField(1, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 1, value));
				break;
			}
			case MIN_DEC:{
				int value=this.tileEntity.getField(1)-offset;
				
				if(value<1)
					value=1;
				
				this.tileEntity.setField(1, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 1, value));
				break;
			}
			case MAX_INC:{
				int value=this.tileEntity.getField(2)+offset;
				
				if(value>255)
					value=255;
				
				this.tileEntity.setField(2, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 2, value));
				break;
			}
			case MAX_DEC:{
				int value=this.tileEntity.getField(2)-offset;
				
				if(value<this.tileEntity.getField(1))
					value=this.tileEntity.getField(1);
				
				this.tileEntity.setField(2, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 2, value));
				break;
			}
		}
		
		
		this.guiStuff.get(0).setText(""+this.tileEntity.getField(0));
		this.guiStuff.get(1).setText(""+this.tileEntity.getField(1));
		this.guiStuff.get(2).setText(""+this.tileEntity.getField(2));
	}
	
	
	protected enum Button{
		/** Radius Increment */ RAD_INC,
		/** Radius Decrement */ RAD_DEC,
		/** Min Height Increment */ MIN_INC,
		/** Min Height Decrement */ MIN_DEC,
		/** Max Height Increment */ MAX_INC,
		/** Max Height Decrement */ MAX_DEC,
		/** ON/OFF Control */ TOGGLE,
		/** See {@link TEBedrockBreaker#softReset()} */ SOFT_RESET
		;
		
		protected GuiButtonExt create(int xPos, int yPos, int width, int height, String displayString){
			return new GuiButtonExt(this.ordinal(), xPos, yPos, width, height, displayString);
		}
		
		protected static Button fromId(int id){
			return values()[id];
		}
	}
}
