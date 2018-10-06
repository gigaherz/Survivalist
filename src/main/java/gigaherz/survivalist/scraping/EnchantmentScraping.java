package gigaherz.survivalist.scraping;

import gigaherz.survivalist.ConfigManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentScraping extends Enchantment
{
    public EnchantmentScraping()
    {
        super(Rarity.COMMON, EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
    }

    @Override
    public int getMaxLevel()
    {
        return ConfigManager.instance.enableScraping ? 3 : 0;
    }

    @Override
    public boolean isTreasureEnchantment()
    {
        return ConfigManager.instance.scrapingIsTreasure;
    }

    @Override
    public boolean canApply(ItemStack stack)
    {
        return ConfigManager.instance.enableScraping;
    }
}
