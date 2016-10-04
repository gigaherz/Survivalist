package gigaherz.survivalist.integration;

import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.api.Dryable;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.oredict.IOreDictEntry;
import minetweaker.mc1102.item.MCItemStack;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Iterator;

public class MineTweakerPlugin
{
    public static void init()
    {
        MineTweakerAPI.registerClass(DryableZen.class);
        MineTweakerAPI.registerClass(ChoppableZen.class);
    }

    static boolean isOredict(IIngredient ing)
    {
        return ing instanceof IOreDictEntry;
    }

    static ItemStack toStack(IIngredient ing)
    {
        Object internal = ing.getInternal();
        if (!(internal instanceof ItemStack))
        {
            Survivalist.logger.error("Not a valid item stack: " + ing);
            return null;
        }

        return (ItemStack) internal;
    }

    static String toOredictName(IIngredient ing) {
        if (!(ing instanceof IOreDictEntry))
            return null;

        return ((IOreDictEntry) ing).getName();
    }

    @ZenClass("gigaherz.survivalist.Dryable")
    public static class DryableZen
    {
        @ZenMethod
        public static void addRecipe(IIngredient input, IItemStack output, int time)
        {
            if (input == null || output == null)
            {
                Survivalist.logger.error("Required parameters missing for drying recipe.");
                return;
            }

            if (isOredict(input))
                Dryable.registerRecipe(toOredictName(input), toStack(output), time);
            else
                Dryable.registerRecipe(toStack(input), toStack(output), time);
        }

        @ZenMethod
        public static void removeRecipe(IIngredient ingredient)
        {
            Iterator<Triple<ItemStack, Integer, ItemStack>> it = Dryable.RECIPES.iterator();
            while(it.hasNext())
            {
                Triple<ItemStack, Integer, ItemStack> item = it.next();
                if (ingredient.matches(new MCItemStack(item.getRight())))
                    it.remove();
            }

            Iterator<Triple<String, Integer, ItemStack>> it2 = Dryable.ORE_RECIPES.iterator();
            while(it2.hasNext())
            {
                Triple<String, Integer, ItemStack> item = it2.next();
                if (ingredient.matches(new MCItemStack(item.getRight())))
                    it.remove();
            }
        }
    }

    @ZenClass("gigaherz.survivalist.Choppable")
    public static class ChoppableZen
    {
        @ZenMethod
        public static void addRecipe(IIngredient input, IItemStack output)
        {
            if (input == null || output == null)
            {
                Survivalist.logger.error("Required parameters missing for chopping recipe.");
                return;
            }

            if (isOredict(input))
                Choppable.registerRecipe(toOredictName(input), toStack(output));
            else
                Choppable.registerRecipe(toStack(input), toStack(output));
        }

        @ZenMethod
        public static void addRecipe(IIngredient input, IItemStack output, double outputMultiplier)
        {
            if (input == null || output == null)
            {
                Survivalist.logger.error("Required parameters missing for chopping recipe.");
                return;
            }

            if (isOredict(input))
                Choppable.registerRecipe(toOredictName(input), toStack(output), outputMultiplier);
            else
                Choppable.registerRecipe(toStack(input), toStack(output), outputMultiplier);
        }


        @ZenMethod
        public static void removeRecipe(IIngredient ingredient)
        {
            Iterator<Triple<ItemStack, ItemStack, Double>> it = Choppable.RECIPES.iterator();
            while(it.hasNext())
            {
                Triple<ItemStack, ItemStack, Double> item = it.next();
                if (ingredient.matches(new MCItemStack(item.getMiddle())))
                    it.remove();
            }

            Iterator<Triple<String, ItemStack, Double>> it2 = Choppable.ORE_RECIPES.iterator();
            while(it2.hasNext())
            {
                Triple<String, ItemStack, Double> item = it2.next();
                if (ingredient.matches(new MCItemStack(item.getMiddle())))
                    it.remove();
            }
        }
    }
}
