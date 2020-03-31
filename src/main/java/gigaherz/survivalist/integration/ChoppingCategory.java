package gigaherz.survivalist.integration;

import com.google.common.collect.Lists;
import gigaherz.survivalist.SurvivalistBlocks;
import gigaherz.survivalist.SurvivalistMod;
import gigaherz.survivalist.api.ChoppingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import java.util.List;

public class ChoppingCategory implements IRecipeCategory<ChoppingRecipe>
{
    private static final ResourceLocation GUI_TEXTURE_LOCATION = SurvivalistMod.location("textures/gui/conversion.png");
    private static final ResourceLocation JEI_RECIPE_GUI_VANILLA = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");
    public static final ResourceLocation UID = SurvivalistMod.location("chopping");

    public static ChoppingCategory INSTANCE;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public ChoppingCategory(IGuiHelper guiHelper)
    {
        INSTANCE = this;
        background = guiHelper.createDrawable(GUI_TEXTURE_LOCATION, 0, 0, 112, 50);
        icon = guiHelper.createDrawableIngredient(new ItemStack(SurvivalistBlocks.OAK_CHOPPING_BLOCK.get()));
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
    public Class<? extends ChoppingRecipe> getRecipeClass()
    {
        return ChoppingRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle()
    {
        return I18n.format("text.survivalist.jei.category.chopping");
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
    public void setIngredients(ChoppingRecipe choppingRecipe, IIngredients iIngredients)
    {
        iIngredients.setInputIngredients(choppingRecipe.getIngredients());
        iIngredients.setOutput(VanillaTypes.ITEM, choppingRecipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ChoppingRecipe recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(0, true, 16, 16);
        itemStacks.init(1, false, 78, 16);

        itemStacks.set(ingredients);
    }

    private List<ItemStack> tools = Lists.newArrayList(
            ItemStack.EMPTY,
            new ItemStack(Items.WOODEN_AXE),
            new ItemStack(Items.GOLDEN_AXE),
            new ItemStack(Items.STONE_AXE),
            new ItemStack(Items.IRON_AXE),
            new ItemStack(Items.DIAMOND_AXE)
    );

    @Override
    public void draw(ChoppingRecipe recipe, double mouseX, double mouseY)
    {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        arrow.draw(42, 16);

        float gameTime = mc.world.getGameTime();
        int index = MathHelper.floor(gameTime / 20) % tools.size();
        ItemStack stack = tools.get(index);
        int axeLevel = stack.getHarvestLevel(ToolType.AXE, null, null);

        double outputMultiplier = recipe.getOutputMultiplier(axeLevel);
        String text = String.format("x%1.1f", outputMultiplier);
        int width = mc.fontRenderer.getStringWidth(text);
        mc.fontRenderer.drawStringWithShadow(text, (112 - 2) - width, 40, 0xFFFFFFFF);

        int clickMultiplier = MathHelper.ceil(200 / recipe.getHitProgress(axeLevel));
        itemRenderer.renderItemAndEffectIntoGUI(stack, 36, 30);
        mc.fontRenderer.drawStringWithShadow(String.format("x%d", clickMultiplier), 50, 40, 0xFFFFFFFF);
    }
}
