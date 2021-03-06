package twistedgate.bedrocked.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import twistedgate.bedrocked.BRInfo;
import twistedgate.bedrocked.BRStuff;

public class BRBlockBase extends Block{
	public BRBlockBase(Material materialIn, String name){
		super(materialIn);
		setRegistryName(new ResourceLocation(BRInfo.ID, name));
		setTranslationKey(BRInfo.ID+"."+name);
		
		BRStuff.BLOCKS.add(this);
	}
}
