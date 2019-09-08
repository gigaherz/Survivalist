package gigaherz.survivalist.rack;

import com.mojang.blaze3d.platform.GlStateManager;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.api.DryingRecipe;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiRack extends ContainerScreen<ContainerRack>
{
    protected ResourceLocation guiTextureLocation = Survivalist.location("textures/gui/rack.png");

    public GuiRack(ContainerRack container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, inventory, title);
        this.ySize = 165;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int j)
    {
        String name = this.title.getFormattedText();
        font.drawString(name, (xSize - font.getStringWidth(name)) / 2, 6, 0x404040);
        font.drawString(playerInventory.getDisplayName().getFormattedText(), 8, ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        minecraft.textureManager.bindTexture(guiTextureLocation);
        this.blit(x, y, 0, 0, xSize, ySize);



        for (int s = 0; s < container.dryTimeRemainingArray.size(); s++)
        {
            int mt = DryingRecipe.getDryingTime(minecraft.world, container.getSlot(s).getStack());
            int ct = container.dryTimeRemainingArray.get(s);

            if (ct > 0 && mt > 0)
            {
                int sx = x + 44 + 36 * s;
                int ny = (int) Math.ceil(ct * 20.0 / mt);
                int sy = 20 - ny;

                this.blit(sx, y + 32 + sy, 176, sy, 9, ny);
            }
        }
    }
}
