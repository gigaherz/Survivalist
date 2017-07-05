package gigaherz.survivalist.scraping;

import gigaherz.survivalist.Survivalist;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnchantmentScraping extends Enchantment
{
    public EnchantmentScraping()
    {
        super(Rarity.COMMON, EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
        setRegistryName("scraping");
        setName(Survivalist.MODID + ".scraping");
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }
}
