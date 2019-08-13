package gigaherz.survivalist.scraping;

import gigaherz.survivalist.ConfigManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class EnchantmentScraping extends Enchantment
{
    public EnchantmentScraping()
    {
        super(Rarity.COMMON, EnchantmentType.BREAKABLE, EquipmentSlotType.values());
    }

    @Override
    public int getMaxLevel()
    {
        return ConfigManager.enableScraping ? 3 : 0;
    }

    @Override
    public boolean isTreasureEnchantment()
    {
        return ConfigManager.scrapingIsTreasure;
    }

    @Override
    public boolean canApply(ItemStack stack)
    {
        return ConfigManager.enableScraping;
    }
}
