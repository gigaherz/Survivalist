package gigaherz.survivalist.sawmill.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Set;

public class SawmillScreen extends DisplayEffectsScreen<SawmillContainer> implements IRecipeShownListener
{
    public static final ResourceLocation GUI_TEXTURE_LOCATION = new ResourceLocation("minecraft:textures/gui/container/furnace.png");
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    public final AbstractRecipeBookGui recipeGui = new AbstractRecipeBookGui()
    {
        @Override
        protected boolean func_212962_b()
        {
            return this.recipeBook.isFurnaceFilteringCraftable();
        }

        @Override
        protected void func_212959_a(boolean filteringCraftable)
        {
            this.recipeBook.setFurnaceFilteringCraftable(filteringCraftable);
        }

        @Override
        protected boolean func_212963_d()
        {
            return this.recipeBook.isFurnaceGuiOpen();
        }

        @Override
        protected void func_212957_c(boolean isOpen)
        {
            this.recipeBook.setFurnaceGuiOpen(isOpen);
        }

        @Override
        protected ITextComponent func_230479_g_()
        {
            return new TranslationTextComponent("gui.recipebook.toggleRecipes.smeltable");
        }

        protected Set<Item> func_212958_h() {
            return AbstractFurnaceTileEntity.getBurnTimes().keySet();
        }
    };
    private boolean widthTooNarrowIn;

    public SawmillScreen(SawmillContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
    }

    @Override
    public void func_231160_c_()
    {
        super.func_231160_c_();

        this.widthTooNarrowIn = this.field_230708_k_ < 379;
        this.recipeGui.init(this.field_230708_k_, this.field_230709_l_, this.field_230706_i_, this.widthTooNarrowIn, this.container);
        this.guiLeft = this.recipeGui.updateScreenPosition(this.widthTooNarrowIn, this.field_230708_k_, this.xSize);
        this.func_230480_a_(new ImageButton(this.guiLeft + 20, this.field_230709_l_ / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
            this.recipeGui.initSearchBar(this.widthTooNarrowIn);
            this.recipeGui.toggleVisibility();
            this.guiLeft = this.recipeGui.updateScreenPosition(this.widthTooNarrowIn, this.field_230708_k_, this.xSize);
            ((ImageButton) button).setPosition(this.guiLeft + 20, this.field_230709_l_ / 2 - 49);
        }));
        this.field_238742_p_ = (this.xSize - this.field_230712_o_.func_238414_a_(this.field_230704_d_)) / 2;
    }

    @Override
    public void func_231023_e_()
    {
        super.func_231023_e_();
        this.recipeGui.tick();
    }

    @Override
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.func_230446_a_(matrixStack);
        if (this.recipeGui.isVisible() && this.widthTooNarrowIn)
        {
            this.func_230450_a_(matrixStack, partialTicks, mouseX, mouseY);
            this.recipeGui.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            this.recipeGui.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
            super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
            this.recipeGui.func_230477_a_(matrixStack, this.guiLeft, this.guiTop, true, partialTicks);
        }

        this.func_230459_a_(matrixStack, mouseX, mouseY);
        this.recipeGui.func_238924_c_(matrixStack, this.guiLeft, this.guiTop, mouseX, mouseY);
    }

    @Override
    protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_230706_i_.getTextureManager().bindTexture(GUI_TEXTURE_LOCATION);
        int x = this.guiLeft;
        int y = this.guiTop;
        this.func_238474_b_(matrixStack, x, y, 0, 0, xSize, ySize);

        if (container.isBurning())
        {
            int k = this.getBurnLeftScaled(13);
            this.func_238474_b_(matrixStack, x + 56, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.getCookProgressScaled(24);
        this.func_238474_b_(matrixStack, x + 79, y + 34, 176, 14, l + 1, 16);
    }

    private int getCookProgressScaled(int pixels)
    {
        int total = container.getTotalCookTime();
        if (total == 0) return 0;
        int progress = container.getCookTime();
        return pixels * progress / total;
    }

    private int getBurnLeftScaled(int pixels)
    {
        int total = container.getTotalBurnTime();

        if (total == 0)
        {
            total = 200;
        }

        return pixels * container.getRemainingBurnTime() / total;
    }

    @Override
    public boolean func_231044_a_(double p_231044_1_, double p_231044_3_, int p_231044_5_)
    {
        if (this.recipeGui.func_231044_a_(p_231044_1_, p_231044_3_, p_231044_5_))
        {
            return true;
        }
        else
        {
            return this.widthTooNarrowIn &&
                    (this.recipeGui.isVisible() || super.func_231044_a_(p_231044_1_, p_231044_3_, p_231044_5_));
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeGui.slotClicked(slotIn);
    }

    @Override
    public boolean func_231046_a_(int p_231046_1_, int p_231046_2_, int p_231046_3_)
    {
        return !this.recipeGui.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_)
                && super.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton)
    {
        boolean flag = mouseX < (double) guiLeftIn || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + this.xSize) || mouseY >= (double) (guiTopIn + this.ySize);
        return this.recipeGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, mouseButton) && flag;
    }

    @Override
    public boolean func_231042_a_(char charTyped, int keyCode)
    {
        return this.recipeGui.func_231042_a_(charTyped, keyCode) ? true : super.func_231042_a_(charTyped, keyCode);
    }

    @Override
    public void recipesUpdated()
    {
        this.recipeGui.recipesUpdated();
    }

    @Override
    public RecipeBookGui getRecipeGui()
    {
        return this.recipeGui;
    }

    @Override
    public void func_231164_f_()
    {
        this.recipeGui.removed();
        super.func_231164_f_();
    }
}