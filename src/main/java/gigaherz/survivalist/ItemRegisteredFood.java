package gigaherz.survivalist;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;

public class ItemRegisteredFood extends ItemFood
{
    public ItemRegisteredFood(String name, int amount, float saturation, boolean isWolfFood)
    {
        super(amount, saturation, isWolfFood);
        setRegistryName(name);
        setUnlocalizedName(Survivalist.MODID + "." + name);
        setCreativeTab(CreativeTabs.tabFood);
    }
}
