package gigaherz.survivalist.scraping;

import gigaherz.survivalist.Survivalist;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnchantmentScraping extends Enchantment
{
    public static EnchantmentScraping register()
    {
        EnchantmentScraping scraping = new EnchantmentScraping(Rarity.COMMON, EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
        GameRegistry.register(scraping);
        return scraping;
    }

    protected EnchantmentScraping(Enchantment.Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot... slots)
    {
        super(rarityIn, typeIn, slots);
        setRegistryName("scraping");
        setName(Survivalist.MODID + ".scraping");
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }
}
