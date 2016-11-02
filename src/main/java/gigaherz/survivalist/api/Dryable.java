package gigaherz.survivalist.api;

import com.google.common.collect.Lists;
import gigaherz.common.OreDictionaryHelper;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.List;

public class Dryable
{
    public static final List<Triple<ItemStack, Integer, ItemStack>> RECIPES = Lists.newArrayList();
    public static final List<Triple<String, Integer, ItemStack>> ORE_RECIPES = Lists.newArrayList();

    public static void registerStockRecipes()
    {
        if (ConfigManager.instance.enableLeatherTanning)
        {
            registerRecipe(new ItemStack(Items.LEATHER), new ItemStack(Survivalist.tanned_leather), 30 * 20);
        }
        if (ConfigManager.instance.enableMeatRotting)
        {
            registerRecipe(new ItemStack(Items.BEEF), new ItemStack(Items.ROTTEN_FLESH), 15 * 20);
            registerRecipe(new ItemStack(Items.MUTTON), new ItemStack(Items.ROTTEN_FLESH), 15 * 20);
            registerRecipe(new ItemStack(Items.PORKCHOP), new ItemStack(Items.ROTTEN_FLESH), 15 * 20);
        }

        if (ConfigManager.instance.enableJerky)
        {
            if (ConfigManager.instance.enableRottenDrying)
            {
                registerRecipe(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Survivalist.jerky), 15 * 20);
            }
            if (ConfigManager.instance.enableMeatDrying)
            {
                registerRecipe(new ItemStack(Items.COOKED_BEEF), new ItemStack(Survivalist.jerky), 15 * 20);
                registerRecipe(new ItemStack(Items.COOKED_MUTTON), new ItemStack(Survivalist.jerky), 15 * 20);
                registerRecipe(new ItemStack(Items.COOKED_PORKCHOP), new ItemStack(Survivalist.jerky), 15 * 20);
            }
        }
    }

    public static void registerRecipe(ItemStack input, ItemStack output, int time)
    {
        RECIPES.add(Triple.of(input, time, output));
    }

    public static void registerRecipe(String input, ItemStack output, int time)
    {
        ORE_RECIPES.add(Triple.of(input, time, output));
    }

    public static int getDryingTime(@Nullable ItemStack stack)
    {
        if (stack == null)
            return -1;

        for (Triple<ItemStack, Integer, ItemStack> recipe : RECIPES)
        {
            if (ItemStack.areItemsEqual(recipe.getLeft(), stack))
                return recipe.getMiddle();
        }
        for (Triple<String, Integer, ItemStack> recipe : ORE_RECIPES)
        {
            if (OreDictionaryHelper.hasOreName(stack, recipe.getLeft()))
                return recipe.getMiddle();
        }

        return -1;
    }

    @Nullable
    public static ItemStack getDryingResult(@Nullable ItemStack stack)
    {
        if (stack == null)
            return null;

        for (Triple<ItemStack, Integer, ItemStack> recipe : RECIPES)
        {
            if (ItemStack.areItemsEqual(recipe.getLeft(), stack))
                return recipe.getRight().copy();
        }
        for (Triple<String, Integer, ItemStack> recipe : ORE_RECIPES)
        {
            if (OreDictionaryHelper.hasOreName(stack, recipe.getLeft()))
                return recipe.getRight().copy();
        }
        return null;
    }
}
