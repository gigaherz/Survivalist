package gigaherz.survivalist.sawmill.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SawmillScreen extends ContainerScreen<SawmillContainer>
{
    public static final ResourceLocation GUI_TEXTURE_LOCATION = new ResourceLocation("minecraft:textures/gui/container/furnace.png");

    public SawmillScreen(SawmillContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        minecraft.getTextureManager().bindTexture(GUI_TEXTURE_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
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
}