package gigaherz.survivalist.integration;

import com.mojang.blaze3d.matrix.MatrixStack;
import gigaherz.survivalist.SurvivalistBlocks;
import gigaherz.survivalist.SurvivalistMod;
import gigaherz.survivalist.api.DryingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class DryingCategory implements IRecipeCategory<DryingRecipe>
{
    private static final ResourceLocation GUI_TEXTURE_LOCATION = SurvivalistMod.location("textures/gui/conversion.png");
    private static final ResourceLocation JEI_RECIPE_GUI_VANILLA = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");
    public static final ResourceLocation UID = SurvivalistMod.location("drying");

    public static DryingCategory INSTANCE;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public DryingCategory(IGuiHelper guiHelper)
    {
        INSTANCE = this;
        background = guiHelper.createDrawable(GUI_TEXTURE_LOCATION, 12, 12, 88, 26);
        icon = guiHelper.createDrawableIngredient(new ItemStack(SurvivalistBlocks.RACK.get()));
        arrow = guiHelper.drawableBuilder(JEI_RECIPE_GUI_VANILLA, 82, 128, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Nonnull
    @Override
    public ResourceLocation getUid()
    {
        return UID;
    }

    @Override
    public Class<? extends DryingRecipe> getRecipeClass()
    {
        return DryingRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle()
    {
        return I18n.format("text.survivalist.jei.category.drying");
    }

    @Nonnull
    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public void setIngredients(DryingRecipe dryingRecipe, IIngredients iIngredients)
    {
        iIngredients.setInputIngredients(dryingRecipe.getIngredients());
        iIngredients.setOutput(VanillaTypes.ITEM, dryingRecipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DryingRecipe recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(0, true, 4, 4);
        itemStacks.init(1, false, 66, 4);

        itemStacks.set(ingredients);
    }

    @Override
    public void draw(DryingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY)
    {
        arrow.draw(matrixStack, 30, 4);
    }
}
