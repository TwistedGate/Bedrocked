package twistedgate.bedrocked;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twistedgate.bedrocked.common.CommonProxy;
import twistedgate.bedrocked.common.tileentity.TEBedrockBreaker;
import twistedgate.bedrocked.common.tileentity.TEDebugGenerator;
import twistedgate.bedrocked.network.NetworkHandler;

@Mod(
	modid=BRInfo.ID,
	name=BRInfo.NAME,
	dependencies=BRInfo.DEPENDS,
	certificateFingerprint=BRInfo.CERT_PRINT,
	updateJSON=BRInfo.UPDATE_URL
)
public class Bedrocked{
	public static Logger log;
	
	@Mod.Instance(BRInfo.ID)
	public static Bedrocked instance;
	
	@SidedProxy(modId=BRInfo.ID, serverSide=BRInfo.PROXY_SERVER, clientSide=BRInfo.PROXY_CLIENT)
	public static CommonProxy proxy;
	
	public static final CreativeTabs creativeTab=new CreativeTabs(BRInfo.ID){
		ItemStack iconstack=null;
		@Override
		public ItemStack createIcon(){
			if(this.iconstack==null)
				iconstack=new ItemStack(BRStuff.bedrockBreaker);
			return this.iconstack;
		}
	};
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		log=event.getModLog();
		
		BRStuff.initStuff();
		proxy.preInitStart(event);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new BRGuiHandler());
		NetworkHandler.init();
		
		regTE(TEBedrockBreaker.class, "breaker");
		regTE(TEDebugGenerator.class, "debuggenerator");
		
		proxy.preInitEnd(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.initStart(event);
		
		proxy.initEnd(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInitStart(event);
		
		proxy.postInitEnd(event);
	}
	
	private void regTE(Class<? extends TileEntity> teClass, String name){
		ResourceLocation loc=new ResourceLocation(BRInfo.ID, "te_"+name);
		GameRegistry.registerTileEntity(teClass, loc);
	}
}
