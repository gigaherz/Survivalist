package gigaherz.survivalist.chaining;

import gigaherz.survivalist.Survivalist;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentChaining extends Enchantment
{
    public EnchantmentChaining()
    {
        super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.values());
        setRegistryName("chaining");
        setName(Survivalist.MODID + ".chaining");
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }
}
