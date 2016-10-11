package gigaherz.common;

import gigaherz.survivalist.Survivalist;
import net.minecraft.item.ItemAxe;

public class ItemRegisteredAxe extends ItemAxe
{
    public ItemRegisteredAxe(String name, ToolMaterial material, float damage, float speed)
    {
        super(material, damage, speed);
        setRegistryName(name);
        setUnlocalizedName(Survivalist.MODID + "." + name);
    }
}
