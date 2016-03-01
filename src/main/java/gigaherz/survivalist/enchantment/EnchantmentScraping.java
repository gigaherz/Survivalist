package gigaherz.survivalist.enchantment;

import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.util.ResourceLocation;

public class EnchantmentScraping extends Enchantment
{
    public static EnchantmentScraping register()
    {
        int enchId;
        if(ConfigManager.instance.idScraping.isDefault())
        {
            boolean found;
            int firstFree = 0;
            do
            {
                found = false;
                for (Enchantment ench : Enchantment.enchantmentsBookList)
                {
                    if (ench.effectId == firstFree)
                    {
                        found = true;
                        break;
                    }
                }
                if(found)
                    firstFree++;
            }
            while(found);
            enchId = firstFree;
            ConfigManager.instance.idScraping.set(enchId);
            ConfigManager.instance.save();
        }
        else
        {
            enchId = ConfigManager.instance.idScraping.getInt();
        }

        EnchantmentScraping scraping = new EnchantmentScraping(enchId, new ResourceLocation(Survivalist.MODID, "scraping"), 1);
        Enchantment.addToBookList(scraping);

        return scraping;
    }

    protected EnchantmentScraping(int enchID, ResourceLocation enchName, int enchWeight)
    {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.ALL);
        setName(Survivalist.MODID + ".scraping");
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

}
