package gigaherz.survivalist.chaining;

import gigaherz.survivalist.Survivalist;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class ChainingEnchantment extends Enchantment
{
    public ChainingEnchantment()
    {
        super(Rarity.UNCOMMON, EnchantmentType.WEAPON, EquipmentSlotType.values());
        setRegistryName("chaining");
        //setName(Survivalist.MODID + ".chaining");
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }
}
