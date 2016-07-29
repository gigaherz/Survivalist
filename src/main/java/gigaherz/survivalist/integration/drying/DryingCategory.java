package gigaherz.survivalist.integration.drying;

import gigaherz.survivalist.Survivalist;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

public class DryingCategory implements IRecipeCategory<DryingRecipeWrapper>
{
    private static final ResourceLocation GUI_TEXTURE_LOCATION = Survivalist.location("textures/gui/conversion.png");
    public static final String UID = Survivalist.MODID + "_drying";

    public static DryingCategory INSTANCE;

    @Nonnull
    private final IDrawable background;

    public DryingCategory(IGuiHelper guiHelper)
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
        return I18n.format("text." + Survivalist.MODID + ".jei.category.drying");
    }

    @Nonnull
    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft mc)
    {
    }

    @Override
    public void drawAnimations(@Nonnull Minecraft minecraft)
    {
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull DryingRecipeWrapper recipeWrapper)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(0, true, 4, 4);
        itemStacks.init(1, false, 66, 4);

        List inputs = recipeWrapper.getInputs();
        List outputs = recipeWrapper.getOutputs();

        itemStacks.setFromRecipe(0, inputs.get(0));
        itemStacks.setFromRecipe(1, outputs.get(0));
    }
}
