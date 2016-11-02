package gigaherz.survivalist.chaining;

import gigaherz.survivalist.Survivalist;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnchantmentChaining extends Enchantment
{
    public static EnchantmentChaining register()
    {
        EnchantmentChaining scraping = new EnchantmentChaining(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.values());
        GameRegistry.register(scraping);
        return scraping;
    }

    protected EnchantmentChaining(Enchantment.Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot... slots)
    {
        super(rarityIn, typeIn, slots);
        setRegistryName("chaining");
        setName(Survivalist.MODID + ".chaining");
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }
}
