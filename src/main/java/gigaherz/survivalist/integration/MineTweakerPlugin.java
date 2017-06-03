package gigaherz.survivalist.integration;

import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.api.Dryable;
import gigaherz.survivalist.integration.chopping.ChoppingRecipeWrapper;
import gigaherz.survivalist.integration.drying.DryingRecipeWrapper;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.oredict.IOreDictEntry;
import minetweaker.mc1112.item.MCItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

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

            WrapperHelper.addJeiRecipe(recipe);
        }

        @ZenMethod
        public static void removeRecipe(final IIngredient output)
        {
            List<Dryable.DryingRecipe> toRemove = Dryable.RECIPES.stream()
                    .filter(recipe -> output.matches(new MCItemStack(recipe.getOutput())))
                    .collect(Collectors.toList());

            Dryable.RECIPES.removeAll(toRemove);
            WrapperHelper.removeDryingRecipes(toRemove);
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
            WrapperHelper.removeDryingRecipes(toRemove);
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
            List<Choppable.ChoppingRecipe> toRemove = Choppable.RECIPES.stream()
                    .filter(recipe -> output.matches(new MCItemStack(recipe.getOutput())))
                    .collect(Collectors.toList());

            Choppable.RECIPES.removeAll(toRemove);
            WrapperHelper.removeChoppingRecipes(toRemove);
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
            WrapperHelper.removeChoppingRecipes(toRemove);
        }
    }

    public static class WrapperHelper
    {
        public static void addJeiRecipe(Dryable.DryingRecipe recipe)
        {
            Object rcp = recipe;
            if (Loader.isModLoaded("jei"))
            {
                rcp = JeiWrapper.wrap(recipe);
            }

            MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(rcp);
        }

        public static void addJeiRecipe(Choppable.ChoppingRecipe recipe)
        {
            Object rcp = recipe;
            if (Loader.isModLoaded("jei"))
            {
                rcp = JeiWrapper.wrap(recipe);
            }

            MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(rcp);
        }

        public static void removeJeiRecipe(Dryable.DryingRecipe recipe)
        {
            Object rcp = recipe;
            if (Loader.isModLoaded("jei"))
            {
                rcp = JeiWrapper.wrap(recipe);
            }

            MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(rcp);
        }

        public static void removeDryingRecipes(List<Dryable.DryingRecipe> recipe)
        {
            recipe.forEach(WrapperHelper::removeJeiRecipe);
        }

        public static void removeJeiRecipe(Choppable.ChoppingRecipe recipe)
        {
            Object rcp = recipe;
            if (Loader.isModLoaded("jei"))
            {
                rcp = JeiWrapper.wrap(recipe);
            }

            MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(rcp);
        }

        public static void removeChoppingRecipes(List<Choppable.ChoppingRecipe> recipe)
        {
            recipe.forEach(WrapperHelper::removeJeiRecipe);
        }

        private static class JeiWrapper
        {
            static Object wrap(Dryable.DryingRecipe recipe)
            {
                Object wrap = DryingRecipeWrapper.wrap(recipe);
                return wrap != null ? wrap : recipe;
            }

            static Object wrap(Choppable.ChoppingRecipe recipe)
            {
                Object wrap = ChoppingRecipeWrapper.wrap(recipe);
                return wrap != null ? wrap : recipe;
            }
        }
    }
}
