package gigaherz.survivalist.base;

import gigaherz.survivalist.Survivalist;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ItemRegisteredArmor extends ItemArmor
{
    public ItemRegisteredArmor(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
    {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        setRegistryName(name);
        setUnlocalizedName(Survivalist.MODID + "." + name);
        setCreativeTab(CreativeTabs.tabCombat);
    }
}
