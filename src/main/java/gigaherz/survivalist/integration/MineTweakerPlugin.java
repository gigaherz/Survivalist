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

            Dryable.DryingRecipe recipe;
            if (isOredict(input))
                recipe = Dryable.registerRecipe(toOredictName(input), toStack(output), time);
            else
                recipe = Dryable.registerRecipe(toStack(input), toStack(output), time);

            WrapperHelper.addJeiRecipe(recipe);
        }

        @ZenMethod
        public static void removeRecipe(final IIngredient output)
        {
            Dryable.RECIPES.removeIf(recipe -> output.matches(new MCItemStack(recipe.getOutput())));
        }

        @ZenMethod
        public static void removeRecipe(final IIngredient output, final IIngredient input)
        {
            if (isOredict(input))
            {
                Dryable.RECIPES.removeIf(recipe ->
                        recipe instanceof Dryable.DryingOreRecipe &&
                                output.matches(new MCItemStack(recipe.getOutput())) &&
                                ((Dryable.DryingOreRecipe) recipe).getOreName().equals(toOredictName(input))
                );
            }
            else
            {
                Dryable.RECIPES.removeIf(recipe ->
                        recipe instanceof Dryable.DryingItemRecipe &&
                                output.matches(new MCItemStack(recipe.getOutput())) &&
                                input.matches(new MCItemStack(((Dryable.DryingItemRecipe) recipe).getInput()))
                );
            }
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

            Choppable.ChoppingRecipe recipe;
            if (isOredict(input))
                recipe = Choppable.registerRecipe(toOredictName(input), toStack(output));
            else
                recipe = Choppable.registerRecipe(toStack(input), toStack(output));

            WrapperHelper.addJeiRecipe(recipe);
        }

        @ZenMethod
        public static void addRecipe(@Nullable IIngredient input, @Nullable IItemStack output, double outputMultiplier)
        {
            if (input == null || output == null)
            {
                Survivalist.logger.error("Required parameters missing for chopping recipe.");
                return;
            }

            Choppable.ChoppingRecipe recipe;
            if (isOredict(input))
                recipe = Choppable.registerRecipe(toOredictName(input), toStack(output)).setOutputMultiplier(outputMultiplier);
            else
                recipe = Choppable.registerRecipe(toStack(input), toStack(output)).setOutputMultiplier(outputMultiplier);

            WrapperHelper.addJeiRecipe(recipe);
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
        public static void removeRecipe(final IIngredient output)
        {
            Choppable.RECIPES.removeIf(recipe -> output.matches(new MCItemStack(recipe.getOutput())));
        }

        @ZenMethod
        public static void removeRecipe(final IIngredient output, final IIngredient input)
        {
            if (isOredict(input))
            {
                Choppable.RECIPES.removeIf(recipe ->
                        recipe instanceof Choppable.ChoppingOreRecipe &&
                        output.matches(new MCItemStack(recipe.getOutput())) &&
                        ((Choppable.ChoppingOreRecipe) recipe).getOreName().equals(toOredictName(input))
                );
            }
            else
            {
                Choppable.RECIPES.removeIf(recipe ->
                        recipe instanceof Choppable.ChoppingItemRecipe &&
                        output.matches(new MCItemStack(recipe.getOutput())) &&
                        input.matches(new MCItemStack(((Choppable.ChoppingItemRecipe) recipe).getInput()))
                );
            }
        }
    }
}
