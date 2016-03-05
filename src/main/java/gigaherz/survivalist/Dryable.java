package gigaherz.survivalist;

import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class Dryable
{
    public static final List<Triple<ItemStack, Integer, ItemStack>> dryingRegistry = Lists.newArrayList();

    public static void register()
    {
        if (ConfigManager.instance.enableLeatherTanning)
        {
            dryingRegistry.add(Triple.of(new ItemStack(Items.leather), 30 * 20, new ItemStack(Survivalist.tanned_leather)));
        }
        if (ConfigManager.instance.enableMeatRotting)
        {
            dryingRegistry.add(Triple.of(new ItemStack(Items.beef), 15 * 20, new ItemStack(Items.rotten_flesh)));
            dryingRegistry.add(Triple.of(new ItemStack(Items.mutton), 15 * 20, new ItemStack(Items.rotten_flesh)));
            dryingRegistry.add(Triple.of(new ItemStack(Items.porkchop), 15 * 20, new ItemStack(Items.rotten_flesh)));
        }

        if (ConfigManager.instance.enableJerky)
        {
            if (ConfigManager.instance.enableRottenDrying)
            {
                dryingRegistry.add(Triple.of(new ItemStack(Items.rotten_flesh), 15 * 20, new ItemStack(Survivalist.jerky)));
            }
            if (ConfigManager.instance.enableMeatDrying)
            {
                dryingRegistry.add(Triple.of(new ItemStack(Items.cooked_beef), 15 * 20, new ItemStack(Survivalist.jerky)));
                dryingRegistry.add(Triple.of(new ItemStack(Items.cooked_mutton), 15 * 20, new ItemStack(Survivalist.jerky)));
                dryingRegistry.add(Triple.of(new ItemStack(Items.cooked_porkchop), 15 * 20, new ItemStack(Survivalist.jerky)));
            }
        }
    }

    public static int getDryingTime(ItemStack stack)
    {
        if (stack == null)
            return 0;

        for (Triple<ItemStack, Integer, ItemStack> scraping : dryingRegistry)
        {
            ItemStack source = scraping.getLeft();

            if (!ItemStack.areItemsEqual(source, stack))
                continue;

            return scraping.getMiddle();
        }

        return 0;
    }

    public static ItemStack getDryingResult(ItemStack stack)
    {
        if (stack == null)
            return null;

        for (Triple<ItemStack, Integer, ItemStack> scraping : dryingRegistry)
        {
            ItemStack source = scraping.getLeft();

            if (!ItemStack.areItemsEqual(source, stack))
                continue;

            return scraping.getRight().copy();
        }

        return null;
    }
}
