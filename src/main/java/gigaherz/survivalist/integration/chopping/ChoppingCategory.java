package gigaherz.survivalist.integration.chopping;

import gigaherz.survivalist.Survivalist;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChoppingCategory extends BlankRecipeCategory<ChoppingRecipeWrapper>
{
    private static final ResourceLocation GUI_TEXTURE_LOCATION = Survivalist.location("textures/gui/conversion.png");
    public static final String UID = Survivalist.MODID + "_chopping";

    public static ChoppingCategory INSTANCE;

    @Nonnull
    private final IDrawable background;

    public ChoppingCategory(IGuiHelper guiHelper)
    {
        INSTANCE = this;
        background = guiHelper.createDrawable(GUI_TEXTURE_LOCATION, 0, 0, 88, 26, 0, 8, 0, 0);
    }

    @Nonnull
    @Override
    public String getUid()
    {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle()
    {
        return I18n.format("text." + Survivalist.MODID + ".jei.category.chopping");
    }

    @Nonnull
    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ChoppingRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(0, true, 4, 4);
        itemStacks.init(1, false, 66, 4);

        itemStacks.set(ingredients);
    }
}
