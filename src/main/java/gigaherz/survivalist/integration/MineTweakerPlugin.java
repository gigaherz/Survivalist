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
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;

public class MineTweakerPlugin
{
    public static void init()
    {
        MineTweakerAPI.registerClass(DryableZen.class);
        MineTweakerAPI.registerClass(ChoppableZen.class);
    }

    private static boolean isOredict(IIngredient ing)
    {
        return ing instanceof IOreDictEntry;
    }

    @Nullable
    private static ItemStack toStack(IIngredient ing)
    {
        Object internal = ing.getInternal();
        if (!(internal instanceof ItemStack))
        {
            Survivalist.logger.error("Not a valid item stack: " + ing);
            return null;
        }

        return (ItemStack) internal;
    }

    @Nullable
    private static String toOredictName(IIngredient ing)
    {
        if (!(ing instanceof IOreDictEntry))
            return null;

        return ((IOreDictEntry) ing).getName();
    }

    @ZenClass("gigaherz.survivalist.Dryable")
    public static class DryableZen
    {
        @ZenMethod
        public static void addRecipe(@Nullable IIngredient input, @Nullable IItemStack output, int time)
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
        public static void removeRecipe(final IIngredient ingredient)
        {
            Dryable.RECIPES.removeIf(item -> ingredient.matches(new MCItemStack(item.getOutput())));
        }
    }

    @ZenClass("gigaherz.survivalist.Choppable")
    public static class ChoppableZen
    {
        @ZenMethod
        public static void addRecipe(@Nullable IIngredient input, @Nullable IItemStack output)
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
        public static void addRecipe(@Nullable IIngredient input, @Nullable IItemStack output, double outputMultiplier)
        {
            if (input == null || output == null)
            {
                Survivalist.logger.error("Required parameters missing for chopping recipe.");
                return;
            }

            if (isOredict(input))
                Choppable.registerRecipe(toOredictName(input), toStack(output)).setOutputMultiplier(outputMultiplier);
            else
                Choppable.registerRecipe(toStack(input), toStack(output)).setOutputMultiplier(outputMultiplier);
        }

        @ZenMethod
        public static void addRecipe(@Nullable IIngredient input, @Nullable IItemStack output, double outputMultiplier, double hitCountMultiplier)
        {
            if (input == null || output == null)
            {
                Survivalist.logger.error("Required parameters missing for chopping recipe.");
                return;
            }

            if (isOredict(input))
                Choppable.registerRecipe(toOredictName(input), toStack(output)).setOutputMultiplier(outputMultiplier).setHitCountMultiplier(hitCountMultiplier);
            else
                Choppable.registerRecipe(toStack(input), toStack(output)).setOutputMultiplier(outputMultiplier).setHitCountMultiplier(hitCountMultiplier);
        }

        @ZenMethod
        public static void removeRecipe(final IIngredient ingredient)
        {
            Choppable.RECIPES.removeIf(item -> ingredient.matches(new MCItemStack(item.getOutput())));
        }
    }
}
