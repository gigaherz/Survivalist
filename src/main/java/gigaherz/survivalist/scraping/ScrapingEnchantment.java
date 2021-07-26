package gigaherz.survivalist.scraping;

import gigaherz.survivalist.ConfigManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class ScrapingEnchantment extends Enchantment
{
    public ScrapingEnchantment()
    {
        super(Rarity.COMMON, EnchantmentType.BREAKABLE, EquipmentSlotType.values());
    }

    @Override
    public int getMaxLevel()
    {
        return ConfigManager.SERVER.enableScraping.get() ? 3 : 0;
    }

    @Override
    public boolean isTreasureEnchantment()
    {
        return ConfigManager.SERVER.scrapingIsTreasure.get();
    }

    @Override
    public boolean canApply(ItemStack stack)
    {
        return ConfigManager.SERVER.enableScraping.get();
    }
}