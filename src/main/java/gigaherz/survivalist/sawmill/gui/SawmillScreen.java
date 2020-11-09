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
    public final AbstractRecipeBookGui recipeBookGui = new AbstractRecipeBookGui()
    {
        @Override
        protected ITextComponent func_230479_g_()
        {
            return new TranslationTextComponent("gui.recipebook.toggleRecipes.smeltable");
        }

        protected Set<Item> func_212958_h() {
            return AbstractFurnaceTileEntity.getBurnTimes().keySet();
        }
    };
    private boolean widthTooNarrow;

    public SawmillScreen(SawmillContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
    }

    @Override
    public void init()
    {
        super.init();

        this.widthTooNarrow = this.width < 379;
        this.recipeBookGui.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.container);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        this.addButton(new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
            this.recipeBookGui.initSearchBar(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            ((ImageButton) button).setPosition(this.guiLeft + 20, this.height / 2 - 49);
        }));
        this.titleX = (this.xSize - this.font.getStringPropertyWidth(this.title)) / 2;
    }

    @Override
    public void tick()
    {
        super.tick();
        this.recipeBookGui.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow)
        {
            this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            this.recipeBookGui.render(matrixStack, mouseX, mouseY, partialTicks);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            this.recipeBookGui.func_230477_a_(matrixStack, this.guiLeft, this.guiTop, true, partialTicks);
        }

        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        this.recipeBookGui.func_238924_c_(matrixStack, this.guiLeft, this.guiTop, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE_LOCATION);
        int x = this.guiLeft;
        int y = this.guiTop;
        this.blit(matrixStack, x, y, 0, 0, xSize, ySize);

        if (container.isBurning())
        {
            int k = this.getBurnLeftScaled(13);
            this.blit(matrixStack, x + 56, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.getCookProgressScaled(24);
        this.blit(matrixStack, x + 79, y + 34, 176, 14, l + 1, 16);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBookGui.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookGui.isVisible() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.recipeBookGui.keyPressed(keyCode, scanCode, modifiers) ? false : super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        boolean flag = mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
        return this.recipeBookGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, mouseButton) && flag;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return this.recipeBookGui.charTyped(codePoint, modifiers) ? true : super.charTyped(codePoint, modifiers);
    }

    @Override
    public void recipesUpdated()
    {
        this.recipeBookGui.recipesUpdated();
    }

    @Override
    public RecipeBookGui getRecipeGui()
    {
        return this.recipeBookGui;
    }

    @Override
    public void onClose()
    {
        this.recipeBookGui.removed();
        super.onClose();
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
}