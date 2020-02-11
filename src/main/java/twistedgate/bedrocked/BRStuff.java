package twistedgate.bedrocked;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twistedgate.bedrocked.common.blocks.BlockBedrockBreaker;
import twistedgate.bedrocked.common.blocks.BlockDebugGenerator;

@Mod.EventBusSubscriber(modid=BRInfo.ID)
public class BRStuff{
	public static final ArrayList<Block> BLOCKS=new ArrayList<>();
	public static final ArrayList<Item> ITEMS=new ArrayList<>();
	
	public static Block bedrockBreaker;
	public static Block debugGen;
	
	public static final void initStuff(){
		// Blocks
		bedrockBreaker=new BlockBedrockBreaker();
		debugGen=new BlockDebugGenerator();
	}
	
	@SubscribeEvent
	public static void regItems(RegistryEvent.Register<Item> event){
		for(Item item:ITEMS){
			event.getRegistry().register(item);
		}
	}
	
	@SubscribeEvent
	public static void regBlocks(RegistryEvent.Register<Block> event){
		for(Block block:BLOCKS){
			event.getRegistry().register(block);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void regModels(ModelRegistryEvent event){
		for(Block block:BLOCKS){
			Item bItem=Item.getItemFromBlock(block);
			
			ModelResourceLocation loc=new ModelResourceLocation(block.getRegistryName(), "inventory");
			
			ModelLoader.setCustomModelResourceLocation(bItem, 0, loc);
		}
		
		for(Item item:ITEMS){
			if(item instanceof ItemBlock) continue;
			
			ModelResourceLocation loc=new ModelResourceLocation(item.getRegistryName(), "inventory");
			
			ModelLoader.setCustomModelResourceLocation(item, 0, loc);
		}
	}
}
