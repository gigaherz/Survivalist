package gigaherz.survivalist.sawmill.gui;

import gigaherz.survivalist.sawmill.TileSawmill;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class GuiSawmill extends GuiContainer
{
    public static final ResourceLocation GUI_TEXTURE_LOCATION = new ResourceLocation("minecraft:textures/gui/container/furnace.png");

    private InventoryPlayer player;
    private TileSawmill tile;

    public GuiSawmill(TileSawmill tileEntity, InventoryPlayer playerInventory)
    {
        super(new ContainerSawmill(tileEntity, playerInventory));
        this.tile = tileEntity;
        this.player = playerInventory;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        mc.renderEngine.bindTexture(GUI_TEXTURE_LOCATION);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        if (tile.isBurning())
        {
            int k = this.getBurnLeftScaled(13);
            this.drawTexturedModalRect(x + 56, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, l + 1, 16);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        mc.fontRenderer.drawString(I18n.format("text.survivalist.sawmill"), 8, 6, 0x404040);
        mc.fontRenderer.drawString(I18n.format(this.player.getName()), 8, ySize - 96 + 3, 0x404040);
    }

    private int getCookProgressScaled(int pixels)
    {
        int total = tile.getTotalCookTime();
        if (total == 0) return 0;
        int progress = tile.getCookTime();
        return pixels * progress / total;
    }

    private int getBurnLeftScaled(int pixels)
    {
        int total = tile.getTotalBurnTime();

        if (total == 0)
        {
            total = 200;
        }

        return pixels * tile.getRemainingBurnTime() / total;
    }
}