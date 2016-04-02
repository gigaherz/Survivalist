package gigaherz.survivalist;

import net.minecraft.item.Item;

public class ItemRegistered extends Item
{
    public ItemRegistered(String name)
    {
        setRegistryName(name);
        setUnlocalizedName(Survivalist.MODID + "." + name);
    }
}
