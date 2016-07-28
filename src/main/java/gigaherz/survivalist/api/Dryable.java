package gigaherz.survivalist.api;

import com.google.common.collect.Lists;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.List;

public class Dryable
{
    public static final List<Triple<ItemStack, Integer, ItemStack>> RECIPES = Lists.newArrayList();

    public static void register()
    {
        if (ConfigManager.instance.enableLeatherTanning)
        {
            registerDryingRecipe(Items.LEATHER, Survivalist.tanned_leather, 30 * 20);
        }
        if (ConfigManager.instance.enableMeatRotting)
        {
            registerDryingRecipe(Items.BEEF, Items.ROTTEN_FLESH, 15 * 20);
            registerDryingRecipe(Items.MUTTON, Items.ROTTEN_FLESH, 15 * 20);
            registerDryingRecipe(Items.PORKCHOP, Items.ROTTEN_FLESH, 15 * 20);
        }

        if (ConfigManager.instance.enableJerky)
        {
            if (ConfigManager.instance.enableRottenDrying)
            {
                registerDryingRecipe(Items.ROTTEN_FLESH, Survivalist.jerky, 15 * 20);
            }
            if (ConfigManager.instance.enableMeatDrying)
            {
                registerDryingRecipe(Items.COOKED_BEEF, Survivalist.jerky, 15 * 20);
                registerDryingRecipe(Items.COOKED_MUTTON, Survivalist.jerky, 15 * 20);
                registerDryingRecipe(Items.COOKED_PORKCHOP, Survivalist.jerky, 15 * 20);
            }
        }
    }

    public static void registerDryingRecipe(Item input, Item output, int time)
    {
        registerDryingRecipe(new ItemStack(input), new ItemStack(output), time);
    }

    public static void registerDryingRecipe(Item input, ItemStack output, int time)
    {
        registerDryingRecipe(new ItemStack(input), output, time);
    }

    public static void registerDryingRecipe(ItemStack input, Item output, int time)
    {
        registerDryingRecipe(input, new ItemStack(output), time);
    }

    public static void registerDryingRecipe(ItemStack input, ItemStack output, int time)
    {
        RECIPES.add(Triple.of(input, time, output));
    }

    public static int getDryingTime(@Nullable ItemStack stack)
    {
        if (stack == null)
            return 0;

        for (Triple<ItemStack, Integer, ItemStack> scraping : RECIPES)
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

        for (Triple<ItemStack, Integer, ItemStack> scraping : RECIPES)
        {
            ItemStack source = scraping.getLeft();

            if (!ItemStack.areItemsEqual(source, stack))
                continue;

            return scraping.getRight().copy();
        }

        return null;
    }
}
