package gigaherz.survivalist.rack;

import com.google.common.collect.Lists;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.List;

public class Dryable
{
    public static final List<Triple<ItemStack, Integer, ItemStack>> dryingRegistry = Lists.newArrayList();

    public static void register()
    {
        if (ConfigManager.instance.enableLeatherTanning)
        {
            dryingRegistry.add(Triple.of(new ItemStack(Items.LEATHER), 30 * 20, new ItemStack(Survivalist.tanned_leather)));
        }
        if (ConfigManager.instance.enableMeatRotting)
        {
            dryingRegistry.add(Triple.of(new ItemStack(Items.BEEF), 15 * 20, new ItemStack(Items.ROTTEN_FLESH)));
            dryingRegistry.add(Triple.of(new ItemStack(Items.MUTTON), 15 * 20, new ItemStack(Items.ROTTEN_FLESH)));
            dryingRegistry.add(Triple.of(new ItemStack(Items.PORKCHOP), 15 * 20, new ItemStack(Items.ROTTEN_FLESH)));
        }

        if (ConfigManager.instance.enableJerky)
        {
            if (ConfigManager.instance.enableRottenDrying)
            {
                dryingRegistry.add(Triple.of(new ItemStack(Items.ROTTEN_FLESH), 15 * 20, new ItemStack(Survivalist.jerky)));
            }
            if (ConfigManager.instance.enableMeatDrying)
            {
                dryingRegistry.add(Triple.of(new ItemStack(Items.COOKED_BEEF), 15 * 20, new ItemStack(Survivalist.jerky)));
                dryingRegistry.add(Triple.of(new ItemStack(Items.COOKED_MUTTON), 15 * 20, new ItemStack(Survivalist.jerky)));
                dryingRegistry.add(Triple.of(new ItemStack(Items.COOKED_PORKCHOP), 15 * 20, new ItemStack(Survivalist.jerky)));
            }
        }
    }

    public static int getDryingTime(@Nullable ItemStack stack)
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

    @Nullable
    public static ItemStack getDryingResult(@Nullable ItemStack stack)
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
