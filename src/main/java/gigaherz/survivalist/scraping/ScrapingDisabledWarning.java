package gigaherz.survivalist.scraping;

import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.SurvivalistMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SurvivalistMod.MODID)
public class ScrapingDisabledWarning
{
    @SubscribeEvent
    public static void addInformation(ItemTooltipEvent ev)
    {
        if (!ConfigManager.SERVER.enableScraping.get() && EnchantmentHelper.getEnchantmentLevel(SurvivalistMod.SCRAPING.get(), ev.getItemStack()) > 0)
        {
            List<ITextComponent> list = ev.getToolTip();
            /*int lastScraping = -1;
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).getFormattedText().startsWith(I18n.format("enchantment.survivalist.scraping")))
                {
                    lastScraping = i;
                }
            }
            if (lastScraping >= 0)
            {
                list.add(lastScraping + 1, "" + TextFormatting.DARK_GRAY + TextFormatting.ITALIC + I18n.format("tooltip.survivalist.scraping.disabled"));
            }*/
            list.add(new TranslationTextComponent("tooltip.survivalist.scraping.disabled").func_240701_a_(TextFormatting.DARK_GRAY, TextFormatting.ITALIC));
        }
    }
}
