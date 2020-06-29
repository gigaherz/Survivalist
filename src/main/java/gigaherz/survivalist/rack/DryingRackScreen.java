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
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.func_230446_a_(matrixStack);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void func_230450_a_(MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_)
    {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        int x = (field_230708_k_ - xSize) / 2;
        int y = (field_230709_l_ - ySize) / 2;

        field_230706_i_.textureManager.bindTexture(guiTextureLocation);
        this.func_238474_b_(matrixStack, x, y, 0, 0, xSize, ySize);


        for (int s = 0; s < container.dryTimeRemainingArray.size(); s++)
        {
            int mt = DryingRecipe.getDryingTime(field_230706_i_.world, container.getSlot(s).getStack());
            int ct = container.dryTimeRemainingArray.get(s);

            if (ct > 0 && mt > 0)
            {
                int sx = x + 44 + 36 * s;
                int ny = (int) Math.ceil(ct * 20.0 / mt);
                int sy = 20 - ny;

                this.func_238474_b_(matrixStack, sx, y + 32 + sy, 176, sy, 9, ny);
            }
        }
    }
}
