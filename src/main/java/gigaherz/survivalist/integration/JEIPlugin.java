package gigaherz.survivalist.integration;

import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.integration.analyzer.ChoppingCategory;
import gigaherz.survivalist.integration.analyzer.ChoppingRecipeHandler;
import gigaherz.survivalist.integration.analyzer.ChoppingRecipeWrapper;
import gigaherz.survivalist.integration.drying.DryingCategory;
import gigaherz.survivalist.integration.drying.DryingRecipeHandler;
import gigaherz.survivalist.integration.drying.DryingRecipeWrapper;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin
{
    @Override
    public void register(@Nonnull IModRegistry registry)
    {
        if (ConfigManager.instance.enableDryingRack)
        {
            registry.addRecipeCategories(new DryingCategory(registry.getJeiHelpers().getGuiHelper()));
            registry.addRecipeHandlers(new DryingRecipeHandler());
            registry.addRecipes(DryingRecipeWrapper.getRecipes());
            if (Survivalist.rack != null)
            {
                registry.addRecipeCategoryCraftingItem(new ItemStack(Survivalist.rack), DryingCategory.UID);
            }
        }

        if (ConfigManager.instance.enableChopping)
        {
            registry.addRecipeCategories(new ChoppingCategory(registry.getJeiHelpers().getGuiHelper()));
            registry.addRecipeHandlers(new ChoppingRecipeHandler());
            registry.addRecipes(ChoppingRecipeWrapper.getRecipes());
            if (Survivalist.chopping_block != null)
            {
                registry.addRecipeCategoryCraftingItem(new ItemStack(Survivalist.chopping_block), ChoppingCategory.UID);
            }
        }

        //registry.getRecipeTransferRegistry().addRecipeTransferHandler(new EssentializerCategory.TransferInfo());
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime)
    {
    }
}
