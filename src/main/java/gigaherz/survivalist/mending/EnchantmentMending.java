package gigaherz.survivalist.mending;

import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.crash.CrashReport;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import javax.management.openmbean.KeyAlreadyExistsException;

public class EnchantmentMending extends Enchantment
{
    public static EnchantmentMending register()
    {
        int enchId;
        if (ConfigManager.instance.idMending.isDefault())
        {
            int firstFree = 0;
            while (Enchantment.getEnchantmentById(firstFree) != null)
            {
                firstFree++;
            }
            enchId = firstFree;
            ConfigManager.instance.idMending.set(enchId);
            ConfigManager.instance.save();
        }
        else
        {
            enchId = ConfigManager.instance.idMending.getInt();
            if (Enchantment.getEnchantmentById(enchId) != null)
            {
                throw new ReportedException(new CrashReport("Error registering enchantment",
                        new KeyAlreadyExistsException("The configured enchantment id, " + enchId + ", is already in use.")));
            }
        }

        EnchantmentMending scraping = new EnchantmentMending(enchId, new ResourceLocation(Survivalist.MODID, "mending"), 2);
        Enchantment.addToBookList(scraping);

        return scraping;
    }

    protected EnchantmentMending(int enchID, ResourceLocation enchName, int enchWeight)
    {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.BREAKABLE);
        setName("mending");
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return enchantmentLevel * 25;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean canApply(ItemStack stack)
    {
        return super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean isAllowedOnBooks()
    {
        return false;
    }

    @Override
    public String getTranslatedName(int level)
    {
        if(level == 1)
            return StatCollector.translateToLocal(this.getName());
        return super.getTranslatedName(level);
    }
}