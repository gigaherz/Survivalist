package gigaherz.survivalist.integration;

import gigaherz.survivalist.integration.analyzer.ChoppingCategory;
import gigaherz.survivalist.integration.analyzer.ChoppingRecipeHandler;
import gigaherz.survivalist.integration.analyzer.ChoppingRecipeWrapper;
import gigaherz.survivalist.integration.drying.DryingCategory;
import gigaherz.survivalist.integration.drying.DryingRecipeHandler;
import gigaherz.survivalist.integration.drying.DryingRecipeWrapper;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;

import javax.annotation.Nonnull;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin
{
    @Override
    public void register(@Nonnull IModRegistry registry)
    {
        registry.addRecipeCategories(
                new ChoppingCategory(registry.getJeiHelpers().getGuiHelper()),
                new DryingCategory(registry.getJeiHelpers().getGuiHelper()));

        registry.addRecipeHandlers(
                new ChoppingRecipeHandler(),
                new DryingRecipeHandler());

        //registry.getRecipeTransferRegistry().addRecipeTransferHandler(new EssentializerCategory.TransferInfo());

        registry.addRecipes(ChoppingRecipeWrapper.getRecipes());
        registry.addRecipes(DryingRecipeWrapper.getRecipes());
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime)
    {
    }
}
