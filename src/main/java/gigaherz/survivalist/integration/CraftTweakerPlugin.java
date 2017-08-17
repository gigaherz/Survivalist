package gigaherz.survivalist.integration;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.item.MCItemStack;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.api.Dryable;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class CraftTweakerPlugin
{
    public static void init()
    {
        CraftTweakerAPI.registerClass(DryableZen.class);
        CraftTweakerAPI.registerClass(ChoppableZen.class);
    }

    private static boolean isOredict(IIngredient ing)
    {
        return ing instanceof IOreDictEntry;
    }

    private static ItemStack toStack(IIngredient ing)
    {
        Object internal = ing.getInternal();
        if (!(internal instanceof ItemStack))
        {
            Survivalist.logger.error("Not a valid item stack: " + ing);
            return ItemStack.EMPTY;
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
        }

        @ZenMethod
        public static void removeRecipe(final IIngredient output)
        {
            List<Dryable.DryingRecipe> toRemove = Dryable.RECIPES.stream()
                    .filter(recipe -> output.matches(new MCItemStack(recipe.getOutput())))
                    .collect(Collectors.toList());

            Dryable.RECIPES.removeAll(toRemove);
        }

        @ZenMethod
        public static void removeRecipe(final IIngredient output, final IIngredient input)
        {
            List<Dryable.DryingRecipe> toRemove;
            if (isOredict(input))
            {
                toRemove = Dryable.RECIPES.stream().filter(recipe ->
                        recipe instanceof Dryable.DryingOreRecipe &&
                                output.matches(new MCItemStack(recipe.getOutput())) &&
                                ((Dryable.DryingOreRecipe) recipe).getOreName().equals(toOredictName(input))
                ).collect(Collectors.toList());
            }
            else
            {
                toRemove = Dryable.RECIPES.stream().filter(recipe ->
                        recipe instanceof Dryable.DryingItemRecipe &&
                                output.matches(new MCItemStack(recipe.getOutput())) &&
                                input.matches(new MCItemStack(((Dryable.DryingItemRecipe) recipe).getInput()))
                ).collect(Collectors.toList());
            }

            Dryable.RECIPES.removeAll(toRemove);
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
            List<Choppable.ChoppingRecipe> toRemove = Choppable.RECIPES.stream()
                    .filter(recipe -> output.matches(new MCItemStack(recipe.getOutput())))
                    .collect(Collectors.toList());

            Choppable.RECIPES.removeAll(toRemove);
        }

        @ZenMethod
        public static void removeRecipe(final IIngredient output, final IIngredient input)
        {
            List<Choppable.ChoppingRecipe> toRemove;
            if (isOredict(input))
            {
                toRemove = Choppable.RECIPES.stream().filter(recipe ->
                        recipe instanceof Choppable.ChoppingOreRecipe &&
                                output.matches(new MCItemStack(recipe.getOutput())) &&
                                ((Choppable.ChoppingOreRecipe) recipe).getOreName().equals(toOredictName(input))
                ).collect(Collectors.toList());
                Choppable.RECIPES.removeIf(recipe ->
                        recipe instanceof Choppable.ChoppingOreRecipe &&
                                output.matches(new MCItemStack(recipe.getOutput())) &&
                                ((Choppable.ChoppingOreRecipe) recipe).getOreName().equals(toOredictName(input))
                );
            }
            else
            {
                toRemove = Choppable.RECIPES.stream().filter(recipe ->
                        recipe instanceof Choppable.ChoppingItemRecipe &&
                                output.matches(new MCItemStack(recipe.getOutput())) &&
                                input.matches(new MCItemStack(((Choppable.ChoppingItemRecipe) recipe).getInput()))
                ).collect(Collectors.toList());
            }

            Choppable.RECIPES.removeAll(toRemove);
        }
    }
}
