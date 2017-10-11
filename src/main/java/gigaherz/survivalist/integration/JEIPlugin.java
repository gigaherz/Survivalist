package gigaherz.survivalist.integration;

import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.api.Dryable;
import gigaherz.survivalist.integration.chopping.ChoppingCategory;
import gigaherz.survivalist.integration.chopping.ChoppingRecipeWrapper;
import gigaherz.survivalist.integration.drying.DryingCategory;
import gigaherz.survivalist.integration.drying.DryingRecipeWrapper;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin
{
    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry)
    {

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry)
    {

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(new DryingCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new ChoppingCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(@Nonnull IModRegistry registry)
    {
        registry.handleRecipes(Dryable.DryingRecipe.class, DryingRecipeWrapper::wrap, DryingCategory.UID);
        registry.addRecipes(Dryable.RECIPES, DryingCategory.UID);
        if (Survivalist.rack != null)
        {
            registry.addRecipeCatalyst(new ItemStack(Survivalist.rack), DryingCategory.UID);
        }

        registry.handleRecipes(Choppable.ChoppingRecipe.class, ChoppingRecipeWrapper::wrap, ChoppingCategory.UID);
        registry.addRecipes(Choppable.RECIPES, ChoppingCategory.UID);
        if (Survivalist.chopping_block != null)
        {
            registry.addRecipeCatalyst(new ItemStack(Survivalist.chopping_block, 1,0), ChoppingCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(Survivalist.chopping_block, 1, 4), ChoppingCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(Survivalist.chopping_block, 1, 8), ChoppingCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(Survivalist.chopping_block, 1, 12), ChoppingCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(Survivalist.chopping_block2, 1, 0), ChoppingCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(Survivalist.chopping_block2, 1, 4), ChoppingCategory.UID);
        }
        if (Survivalist.sawmill != null)
        {
            registry.addRecipeCatalyst(new ItemStack(Survivalist.sawmill), ChoppingCategory.UID);
        }
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime)
    {
    }
}
