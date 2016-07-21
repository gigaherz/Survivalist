package gigaherz.survivalist.chopblock;

import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.rack.BlockRack;
import gigaherz.survivalist.rack.TileRack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

public class RenderChoppingBlock extends TileEntitySpecialRenderer<TileChopping>
{
    @Override
    public void renderTileEntityAt(TileChopping te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (state.getBlock() != Survivalist.chopping_block)
            return;

        if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scale(8, 8, 1);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
        else
        {
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        }

        GlStateManager.disableLighting();

        IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + 0.5, y + 0.65, z + 0.5);

        ItemStack stack = inv.getStackInSlot(0);
        if (stack != null)
        {
            GlStateManager.pushMatrix();

            GlStateManager.translate(0, -4.5/16.0f, 0);

            GlStateManager.scale(2,2,2);

            GlStateManager.color(1f, 1f, 1f, 1f);

            Minecraft mc = Minecraft.getMinecraft();
            if (destroyStage >= 0)
                mc.renderEngine.bindTexture(DESTROY_STAGES[destroyStage]);
            else
                mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();

        GlStateManager.enableLighting();

        if (destroyStage >= 0)
        {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }
}
