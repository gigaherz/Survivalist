package gigaherz.survivalist.integration;

import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.api.Dryable;
import gigaherz.survivalist.integration.chopping.ChoppingRecipeWrapper;
import gigaherz.survivalist.integration.drying.DryingRecipeWrapper;
import minetweaker.MineTweakerAPI;
import net.minecraftforge.fml.common.Loader;

public class WrapperHelper
{
    public static void addJeiRecipe(Dryable.DryingRecipe recipe)
    {
        Object rcp = recipe;
        if (Loader.isModLoaded("JEI"))
        {
            rcp = JeiWrapper.wrap(recipe);
        }

        MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(rcp);
    }

    public static void addJeiRecipe(Choppable.ChoppingRecipe recipe)
    {
        Object rcp = recipe;
        if (Loader.isModLoaded("JEI"))
        {
            rcp = JeiWrapper.wrap(recipe);
        }

        MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(rcp);
    }

    public static void removeJeiRecipe(Dryable.DryingRecipe recipe)
    {
        Object rcp = recipe;
        if (Loader.isModLoaded("JEI"))
        {
            rcp = JeiWrapper.wrap(recipe);
        }

        MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(rcp);
    }

    public static void removeJeiRecipe(Choppable.ChoppingRecipe recipe)
    {
        Object rcp = recipe;
        if (Loader.isModLoaded("JEI"))
        {
            rcp = JeiWrapper.wrap(recipe);
        }

        MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(rcp);
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
