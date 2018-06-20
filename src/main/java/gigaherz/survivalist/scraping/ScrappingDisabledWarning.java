package gigaherz.survivalist.scraping;

import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ScrappingDisabledWarning
{
    @SubscribeEvent
    public static void addInformation(ItemTooltipEvent ev)
    {
        if (!ConfigManager.instance.enableScraping && EnchantmentHelper.getEnchantmentLevel(Survivalist.scraping, ev.getItemStack()) > 0)
        {
            List<String> list = ev.getToolTip();
            int lastScraping = -1;
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).startsWith(I18n.format("enchantment.survivalist.scraping")))
                {
                    lastScraping = i;
                }
            }
            if (lastScraping >= 0)
            {
                list.add(lastScraping + 1, "" + TextFormatting.DARK_GRAY + TextFormatting.ITALIC + I18n.format("tooltip.survivalist.scraping.disabled"));
            }
        }
    }
}
