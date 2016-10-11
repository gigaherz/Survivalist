package gigaherz.common;

import gigaherz.survivalist.Survivalist;
import net.minecraft.item.Item;

public class ItemRegistered extends Item
{
    public ItemRegistered(String name)
    {
        setRegistryName(name);
        setUnlocalizedName(Survivalist.MODID + "." + name);
    }
}
