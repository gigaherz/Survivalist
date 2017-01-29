package gigaherz.survivalist.api;

import com.google.common.collect.Lists;
import gigaherz.common.OreDictionaryHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class Choppable
{

    public static abstract class ChoppingRecipe
    {
        private ItemStack output;
        private double outputMultiplier;
        private double hitCountMultiplier;

        public ChoppingRecipe(ItemStack output, double outputMultiplier, double hitCountMultiplier)
        {
            this.outputMultiplier = outputMultiplier;
            this.output = output;
            this.hitCountMultiplier = hitCountMultiplier;
        }

        public double getOutputMultiplier()
        {
            return outputMultiplier;
        }

        public ItemStack getOutput()
        {
            return output;
        }

        public abstract boolean accepts(ItemStack stack);

        public double getHitCountMultiplier()
        {
            return hitCountMultiplier;
        }
    }

    public static class ChoppingItemRecipe extends ChoppingRecipe
    {
        private ItemStack input;

        public ChoppingItemRecipe(ItemStack input, ItemStack output, double outputMultiplier, double hitCountMultiplier)
        {
            super(output, outputMultiplier, hitCountMultiplier);
            this.input = input;
        }

        public ItemStack getInput()
        {
            return input;
        }

        public boolean accepts(ItemStack stack)
        {
            return OreDictionary.itemMatches(input, stack, false);
        }
    }

    public static class ChoppingOreRecipe extends ChoppingRecipe
    {
        private String oreName;

        public ChoppingOreRecipe(String oreName, ItemStack right, double outputMultiplier, double hitCountMultiplier)
        {
            super(right, outputMultiplier, hitCountMultiplier);
            this.oreName = oreName;
        }

        public String getOreName()
        {
            return oreName;
        }

        @Override
        public boolean accepts(ItemStack stack)
        {
            return OreDictionaryHelper.hasOreName(stack, oreName);
        }
    }

    public static final List<ChoppingRecipe> RECIPES = Lists.newArrayList();

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
        registerRecipe(input, output, outputMultiplier, 1);
    }

    public static void registerRecipe(ItemStack input, ItemStack output, double outputMultiplier, double hitCountMultiplier)
    {
        RECIPES.add(new ChoppingItemRecipe(input, output, outputMultiplier, hitCountMultiplier));
    }

    public static void registerRecipe(String input, ItemStack output)
    {
        registerRecipe(input, output, 1.0);
    }

    public static void registerRecipe(String input, ItemStack output, double outputMultiplier)
    {
        registerRecipe(input, output, outputMultiplier, 1);
    }

    public static void registerRecipe(String input, ItemStack output, double outputMultiplier, double hitCountMultiplier)
    {
        RECIPES.add(new ChoppingOreRecipe(input, output, outputMultiplier, hitCountMultiplier));
    }

    public static boolean isValidInput(ItemStack stack)
    {
        if (stack == null)
            return false;

        for (ChoppingRecipe recipe : RECIPES)
        {
            if (recipe.accepts(stack))
                return true;
        }

        return false;
    }

    public static Pair<ItemStack, Double> getResults(ItemStack stack)
    {
        if (stack == null)
            return null;

        for (ChoppingRecipe recipe : RECIPES)
        {
            if (recipe.accepts(stack))
                return Pair.of(recipe.getOutput().copy(), recipe.getOutputMultiplier());
        }

        return null;
    }

    public static double getHitCountMultiplier(ItemStack stack)
    {
        if (stack == null)
            return 0;

        for (ChoppingRecipe recipe : RECIPES)
        {
            if (recipe.accepts(stack))
                return recipe.getHitCountMultiplier();
        }

        return 0;
    }
}
