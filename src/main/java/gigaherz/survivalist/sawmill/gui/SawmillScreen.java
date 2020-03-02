package gigaherz.survivalist.sawmill.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SawmillScreen extends DisplayEffectsScreen<SawmillContainer> implements IRecipeShownListener
{
    public static final ResourceLocation GUI_TEXTURE_LOCATION = new ResourceLocation("minecraft:textures/gui/container/furnace.png");
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    private float oldMouseX;
    private float oldMouseY;
    private final RecipeBookGui recipeBookGui = new RecipeBookGui();
    private boolean hasRecipeBook;
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public SawmillScreen(SawmillContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
    }

    @Override
    protected void init()
    {
        super.init();

        this.widthTooNarrow = this.width < 379;
        this.recipeBookGui.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.container);
        this.hasRecipeBook = true;
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        this.children.add(this.recipeBookGui);
        this.setFocusedDefault(this.recipeBookGui);
        this.addButton((new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
            this.recipeBookGui.initSearchBar(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            ((ImageButton)button).setPosition(this.guiLeft + 20, this.height / 2 - 49);
        })));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow)
        {
            this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
        }
        else
        {
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
            super.render(mouseX, mouseY, partialTicks);
            this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, false, partialTicks);
        }

        this.renderHoveredToolTip(mouseX, mouseY);
        this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;
        this.func_212932_b(this.recipeBookGui);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        minecraft.getTextureManager().bindTexture(GUI_TEXTURE_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int x = this.guiLeft;
        int y = this.guiTop;
        this.blit(x, y, 0, 0, xSize, ySize);

        if (container.isBurning())
        {
            int k = this.getBurnLeftScaled(13);
            this.blit(x + 56, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.getCookProgressScaled(24);
        this.blit(x + 79, y + 34, 176, 14, l + 1, 16);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
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
    protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_)
    {
        if (this.widthTooNarrow && this.recipeBookGui.isVisible())
            return false;

        return super.isPointInRegion(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
    {
        if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_))
            return true;

        if (this.widthTooNarrow && this.recipeBookGui.isVisible())
            return false;

        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_)
    {
        if (this.buttonClicked)
        {
            this.buttonClicked = false;
            return true;
        }
        else
        {
            return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
        }
    }

    @Override
    protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_)
    {
        boolean flag = p_195361_1_ < (double) p_195361_5_ || p_195361_3_ < (double) p_195361_6_ || p_195361_1_ >= (double) (p_195361_5_ + this.xSize) || p_195361_3_ >= (double) (p_195361_6_ + this.ySize);
        return this.recipeBookGui.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && flag;
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    @Override
    public void recipesUpdated()
    {
        this.recipeBookGui.recipesUpdated();
    }

    @Override
    public void removed()
    {
        if (this.hasRecipeBook)
        {
            this.recipeBookGui.removed();
        }

        super.removed();
    }

    @Override
    public RecipeBookGui getRecipeGui()
    {
        return this.recipeBookGui;
    }
}