package gigaherz.survivalist.rack;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import gigaherz.survivalist.SurvivalistMod;
import gigaherz.survivalist.api.DryingRecipe;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DryingRackScreen extends ContainerScreen<DryingRackContainer>
{
    protected ResourceLocation guiTextureLocation = SurvivalistMod.location("textures/gui/rack.png");

    public DryingRackScreen(DryingRackContainer container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, inventory, title);
        this.ySize = 165;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void func_230450_a_(MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_)
    {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        minecraft.textureManager.bindTexture(guiTextureLocation);
        this.blit(matrixStack, x, y, 0, 0, xSize, ySize);


        for (int s = 0; s < container.dryTimeRemainingArray.size(); s++)
        {
            int mt = DryingRecipe.getDryingTime(minecraft.world, container.getSlot(s).getStack());
            int ct = container.dryTimeRemainingArray.get(s);

            if (ct > 0 && mt > 0)
            {
                int sx = x + 44 + 36 * s;
                int ny = (int) Math.ceil(ct * 20.0 / mt);
                int sy = 20 - ny;

                this.blit(matrixStack, sx, y + 32 + sy, 176, sy, 9, ny);
            }
        }
    }
}
