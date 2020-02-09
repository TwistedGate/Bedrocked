package twistedgate.bedrocked.common.items;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import twistedgate.bedrocked.BRInfo;
import twistedgate.bedrocked.BRStuff;
import twistedgate.bedrocked.Bedrocked;

public class BRItemBase extends Item{
	public BRItemBase(String regName){
		setCreativeTab(Bedrocked.creativeTab);
		setRegistryName(new ResourceLocation(BRInfo.ID, regName));
		setTranslationKey(BRInfo.ID+"."+regName);
		setFull3D();
		
		BRStuff.ITEMS.add(this);
	}
}
