package gigaherz.survivalist.api;

import com.google.common.collect.Lists;
import gigaherz.survivalist.Survivalist;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class Choppable
{
    public static final List<Triple<ItemStack, ItemStack, Double>> RECIPES = Lists.newArrayList();
    public static final List<Triple<String, ItemStack, Double>> ORE_RECIPES = Lists.newArrayList();

    public static void registerStockRecipes()
    {
        registerRecipe("plankWood", new ItemStack(Items.STICK), 2.0);
    }

    public static void registerRecipe(ItemStack input, ItemStack output)
    {
        registerRecipe(input, output, 1.0);
    }

    public static void registerRecipe(ItemStack input, ItemStack output, double outputMultiplier)
    {
        RECIPES.add(Triple.of(input, output, outputMultiplier));
    }

    public static void registerRecipe(String input, ItemStack output)
    {
        registerRecipe(input, output, 1.0);
    }

    public static void registerRecipe(String input, ItemStack output, double outputMultiplier)
    {
        ORE_RECIPES.add(Triple.of(input, output, outputMultiplier));
    }

    public static boolean isValidInput(ItemStack stack)
    {
        if (stack == null)
            return false;

        for (Triple<ItemStack, ItemStack, Double> recipe : RECIPES)
        {
            if (OreDictionary.itemMatches(recipe.getLeft(), stack, false))
                return true;
        }
        for (Triple<String, ItemStack, Double> recipe : ORE_RECIPES)
        {
            if (Survivalist.hasOreName(stack, recipe.getLeft()))
                return true;
        }
        return false;
    }

    public static Pair<ItemStack, Double> getResults(ItemStack stack)
    {
        if (stack == null)
            return null;

        for (Triple<ItemStack, ItemStack, Double> recipe : RECIPES)
        {
            if (OreDictionary.itemMatches(recipe.getLeft(), stack, false))
                return Pair.of(recipe.getMiddle().copy(), recipe.getRight());
        }
        for (Triple<String, ItemStack, Double> recipe : ORE_RECIPES)
        {
            if (Survivalist.hasOreName(stack, recipe.getLeft()))
                return Pair.of(recipe.getMiddle().copy(), recipe.getRight());
        }
        return null;
    }
}
