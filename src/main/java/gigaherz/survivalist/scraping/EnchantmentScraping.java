package gigaherz.survivalist.scraping;

import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.crash.CrashReport;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

import javax.management.openmbean.KeyAlreadyExistsException;

public class EnchantmentScraping extends Enchantment
{
    public static EnchantmentScraping register()
    {
        int enchId;
        if (ConfigManager.instance.idScraping.isDefault())
        {
            int firstFree = 0;
            while (Enchantment.getEnchantmentById(firstFree) != null)
            {
                firstFree++;
            }
            enchId = firstFree;
            ConfigManager.instance.idScraping.set(enchId);
            ConfigManager.instance.save();
        }
        else
        {
            enchId = ConfigManager.instance.idScraping.getInt();
            if (Enchantment.getEnchantmentById(enchId) != null)
            {
                throw new ReportedException(new CrashReport("Error registering enchantment",
                        new KeyAlreadyExistsException("The configured enchantment id, " + enchId + ", is already in use.")));
            }
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
    public int getMaxLevel()
    {
        return 3;
    }
}
