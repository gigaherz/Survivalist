package gigaherz.survivalist.common;

import gigaherz.common.ItemRegisteredArmor;
import gigaherz.survivalist.ConfigManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemTannedArmor extends ItemRegisteredArmor
{
    public ItemTannedArmor(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
    {
        super(name, materialIn, renderIndexIn, equipmentSlotIn);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (ConfigManager.instance.enableLeatherTanning)
        {
            super.getSubItems(tab, subItems);
        }
    }
}
