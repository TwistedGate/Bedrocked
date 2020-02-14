package twistedgate.bedrocked.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import net.minecraft.util.math.BlockPos;
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
	
	private GuiTextField field0;
	private GuiTextField field1;
	private GuiTextField field2;
	private GuiTextField field3;
	
	private BlockPos lastWorking=null;
	
	public GuiBedrockBreaker(InventoryPlayer playerInv, TEBedrockBreaker tileEntity){
		super(new ContainerBedrockBreaker(playerInv, tileEntity));
		this.tileEntity=tileEntity;
		this.xSize=136;
		this.ySize=166;
	}
	
	private int getRadius(){
		return this.tileEntity.getField(0);
	}
	
	private int getMin(){
		return this.tileEntity.getField(1);
	}
	
	private int getMax(){
		return this.tileEntity.getField(2);
	}
	
	private int getPowerStored(){
		return this.tileEntity.getField(3);
	}
	
	private int getPowerCapacity(){
		return this.tileEntity.getField(4);
	}
	
	private int getHits(){
		return this.tileEntity.getField(5);
	}
	
	private boolean noBedrock(){
		return this.tileEntity.getField(6)!=0;
	}
	
	private boolean isEnabled(){
		return this.tileEntity.getField(7)!=0;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		this.guiStuff.clear();
		
		int x=(this.width-this.xSize)/2;
		int y=(this.height-this.ySize)/2;
		
		addButton(Button.RAD_DEC.create(x+5, y+76, 10, 10, "-"));
		addButton(Button.RAD_INC.create(x+45, y+76, 10, 10, "+"));
		
		addButton(Button.MIN_DEC.create(x+5, y+90, 10, 10, "-"));
		addButton(Button.MIN_INC.create(x+45, y+90, 10, 10, "+"));
		
		addButton(Button.MAX_DEC.create(x+5, y+104, 10, 10, "-"));
		addButton(Button.MAX_INC.create(x+45, y+104, 10, 10, "+"));
		
		addButton(Button.TOGGLE.create(x+121, y+5, 10, 10, "T"));
		addButton(Button.SOFT_RESET.create(x+121, y+17, 10, 10, "R"));
		
		
		// Validator for text fields, only allowing digits (ie 0-9)
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
		
		this.field0=addGuiThing(new GuiTextField(0, this.fontRenderer, x+17, y+76, 26, 10));
		this.field0.setText(""+getRadius());
		this.field0.setValidator(numval);
		this.field0.setMaxStringLength(3);
		
		this.field1=addGuiThing(new GuiTextField(1, this.fontRenderer, x+17, y+90, 26, 10));
		this.field1.setText(""+getMin());
		this.field1.setValidator(numval);
		this.field1.setMaxStringLength(3);
		
		this.field2=addGuiThing(new GuiTextField(2, this.fontRenderer, x+17, y+104, 26, 10));
		this.field2.setText(""+getMax());
		this.field2.setValidator(numval);
		this.field2.setMaxStringLength(3);
		
		this.field3=addGuiThing(new GuiTextField(3, this.fontRenderer, x+6, y+120, 124, 10));
		this.field3.setEnabled(false);
		this.field3.setDisabledTextColour(0xE0E0E0);
	}
	
	public GuiTextField addGuiThing(GuiTextField gui){
		this.guiStuff.add(gui);
		return gui;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		boolean needsUpdating=false;
		if(this.tileEntity.workingPos!=null)
			needsUpdating=!this.tileEntity.workingPos.equals(this.lastWorking);
		if(this.lastWorking!=null)
			needsUpdating=!this.lastWorking.equals(this.tileEntity.workingPos);
		
		if(needsUpdating){
			this.lastWorking=this.tileEntity.workingPos;
			
			if(this.tileEntity.workingPos!=null){
				BlockPos pos=this.tileEntity.workingPos;
				
				this.field3.setText("x"+pos.getX()+", y"+pos.getY()+", z"+pos.getZ());
			}else{
				this.field3.setText("None.");
			}
		}
		
		if(!this.guiStuff.isEmpty())
			for(GuiTextField gui:this.guiStuff)
				gui.drawTextBox();
		
		renderHovertexts(mouseX, mouseY);
		
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	private void renderHovertexts(int mouseX, int mouseY){
		int x=(this.width-this.xSize)/2;
		int y=(this.height-this.ySize)/2;
		
		if(inside(mouseX, mouseY, x+6, y+6, 4, 64)){
			int minPower=getPowerStored();
			int maxPower=getPowerCapacity();
			
			if(GuiKeyUtils.isShiftDown()){
				drawHoveringText("§d"+minPower+"/"+maxPower+"RF", mouseX, mouseY);
			}else{
				drawHoveringText("§d"+EnergyUtils.toString(minPower, true)+"/"+EnergyUtils.toString(maxPower, true), mouseX, mouseY);
			}
		}
		
		if(!GuiKeyUtils.isCtrlDown()){
			if(Button.RAD_INC.isMouseOver())
				drawHoveringText(Arrays.asList("§6§nRadius Control", "§7[SHIFT]: +10","§7[CTRL]: Hide This"), mouseX, mouseY);
			
			if(Button.RAD_DEC.isMouseOver())
				drawHoveringText(Arrays.asList("§6§nRadius Control", "§7[SHIFT]: -10","§7[CTRL]: Hide This"), mouseX, mouseY);
			
			if(Button.MIN_INC.isMouseOver())
				drawHoveringText(Arrays.asList("§6§nMin Height", "§7[SHIFT]: +10","§7[CTRL]: Hide This"), mouseX, mouseY);
			
			if(Button.MIN_DEC.isMouseOver())
				drawHoveringText(Arrays.asList("§6§nMin Height", "§7[SHIFT]: -10","§7[CTRL]: Hide This"), mouseX, mouseY);
			
			if(Button.MAX_INC.isMouseOver())
				drawHoveringText(Arrays.asList("§6§nMax Height", "§7[SHIFT]: +10","§7[CTRL]: Hide This"), mouseX, mouseY);
			
			if(Button.MAX_DEC.isMouseOver())
				drawHoveringText(Arrays.asList("§6§nMax Height", "§7[SHIFT]: -10","§7[CTRL]: Hide This"), mouseX, mouseY);
			
			if(Button.TOGGLE.isMouseOver())
				drawHoveringText(Arrays.asList("Toggle Breaker", "§7State: "+(isEnabled()?"§2ON":"§4OFF")), mouseX, mouseY);
			
			if(Button.SOFT_RESET.isMouseOver())
				drawHoveringText(Arrays.asList("§4Soft-Reset","§7Rescans area at","§7the cost of progress"), mouseX, mouseY);
		}
		
		if(this.field0!=null && inside(mouseX, mouseY, this.field0.x-2, this.field0.y, this.field0.width+4, this.field0.height))
			drawHoveringText("§6§nRadius Control", mouseX, mouseY);
		
		if(this.field1!=null && inside(mouseX, mouseY, this.field1.x-2, this.field1.y, this.field1.width+4, this.field1.height))
			drawHoveringText("§6§nMin Height", mouseX, mouseY);
		
		if(this.field2!=null && inside(mouseX, mouseY, this.field2.x-2, this.field2.y, this.field2.width+4, this.field2.height))
			drawHoveringText("§6§nMax Height", mouseX, mouseY);
		
		if(this.field3!=null && inside(mouseX, mouseY, this.field3.x, this.field3.y, this.field3.width, this.field3.height))
			drawHoveringText("§6§nWorking Coordinates", mouseX, mouseY);
	}
	
	private boolean inside(int mouseX, int mouseY, int x, int y, int width, int height){
		return mouseX>=x && mouseX<=(x+width) && mouseY>=y && mouseY<=(y+height);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		int x=(this.width-this.xSize)/2;
		int y=(this.height-this.ySize)/2;
		
		GlStateManager.color(1, 1, 1, 1);
		
		if(!noBedrock() && getHits()>0)
			renderBedrock(x+66, y+38, partialTicks);
		
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		
		drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize); // Main GUI Texture
		
		GlStateManager.enableBlend();
		if(isEnabled()){
			drawTexturedModalRect(x+14, y+6, 140, 0, 104, 64);
		}else{
			drawTexturedModalRect(x+14, y+6, 140, 64, 104, 64);
		}
		GlStateManager.disableBlend();
		
		int minPower=getPowerStored();
		int maxPower=getPowerCapacity();
		int bar=(int)Math.floor(minPower/(float)maxPower*64);
		
		drawTexturedModalRect(x+6, y+70-bar, 136, 64-bar, 4, bar);  // Power storage display.
		
		
		// Debugging Text
		String[] lines;
		if(isEnabled()){
			lines=new String[]{
					"§00§11§22§33§44§55§66§77§88§99§aa§bb§cc§dd§ee",
					"Done: "+(noBedrock()?"Yes.":"No."),
					"-----------------",
					"Radius: "+getRadius(),
					"Min: "+getMin(),
					"Max: "+getMax(),
					"-----------------",
					"Progress: "+String.format(Locale.ENGLISH, "%.2f", getHits()/(float)TEBedrockBreaker.REQUIRED_HITS*100)+"%",
			};
		}else{
			lines=new String[]{
					"§00§11§22§33§44§55§66§77§88§99§aa§bb§cc§dd§ee",
					"Breaker Disabled!",
					"Click T to Enable."
			};
		}
		
		if(lines!=null)
			for(int i=0;i<lines.length;i++)
				drawString(this.fontRenderer, lines[i], x-102, y+7+(8*i), isEnabled()?0x00AF00:0xAF0000);
	}
	
	private void renderBedrock(int x, int y, float partialTicks){
		ItemStack bedrock=new ItemStack(Blocks.BEDROCK);
		
		float f=32*(getHits()/(float)TEBedrockBreaker.REQUIRED_HITS);
		
		TextureManager textureManager=this.mc.getTextureManager();
		
		GlStateManager.pushMatrix();
		{
			GlStateManager.translate((float)x, (float)y, 100.0F+this.itemRender.zLevel);
			GlStateManager.scale(1.0F, -1.0F, 1.0F);
	        GlStateManager.rotate(11.25F, 1.0F, 0.0F, 0.0F);
	        GlStateManager.rotate(45F, 0.0F, 1.0F, 0.0F);
	        GlStateManager.scale(32.0F, 32.0F-f, 32.0F);
	        
	        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
	        
	        this.itemRender.renderItem(bedrock, this.itemRender.getItemModelWithOverrides(bedrock, null, null));
		}	
		GlStateManager.popMatrix();
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
								if(value>getMax()) value=getMax();
								break;
							}
							case 2:{
								if(value>255) value=255;
								if(value<getMin()) value=getMin();
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
				this.inventorySlots.detectAndSendChanges();
				return;
			}
			case TOGGLE:{
				NetworkHandler.sendToServer(new BreakerToggleMessage(this.tileEntity));
				this.inventorySlots.detectAndSendChanges();
				return;
			}
			case RAD_INC:{
				int value=getRadius()+offset;
				
				if(value>16)
					value=16;
				
				this.tileEntity.setField(0, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 0, value));
				break;
			}
			case RAD_DEC:{
				int value=getRadius()-offset;
				
				if(value<1)
					value=1;
				
				this.tileEntity.setField(0, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 0, value));
				break;
			}
			case MIN_INC:{
				int value=getMin()+offset;
				
				if(value>getMax())
					value=getMax();
				
				this.tileEntity.setField(1, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 1, value));
				break;
			}
			case MIN_DEC:{
				int value=getMin()-offset;
				
				if(value<1)
					value=1;
				
				this.tileEntity.setField(1, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 1, value));
				break;
			}
			case MAX_INC:{
				int value=getMax()+offset;
				
				if(value>255)
					value=255;
				
				this.tileEntity.setField(2, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 2, value));
				break;
			}
			case MAX_DEC:{
				int value=getMax()-offset;
				
				if(value<getMin())
					value=getMin();
				
				this.tileEntity.setField(2, value);
				NetworkHandler.sendToServer(new BreakerFieldUpdateMessage(this.tileEntity.getPos(), 2, value));
				break;
			}
		}
		
		
		this.guiStuff.get(0).setText(""+getRadius());
		this.guiStuff.get(1).setText(""+getMin());
		this.guiStuff.get(2).setText(""+getMax());
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
		
		protected GuiButtonExt instance=null;
		protected GuiButtonExt create(int xPos, int yPos, int width, int height, String displayString){
			return this.instance=new GuiButtonExt(this.ordinal(), xPos, yPos, width, height, displayString);
		}
		
		protected boolean isMouseOver(){
			return this.instance.isMouseOver();
		}
		
		protected static Button fromId(int id){
			return values()[id];
		}
	}
}
