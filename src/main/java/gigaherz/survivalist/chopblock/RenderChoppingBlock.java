package gigaherz.survivalist.chopblock;

import gigaherz.survivalist.Survivalist;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RenderChoppingBlock extends TileEntitySpecialRenderer<TileChopping>
{
    final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void renderTileEntityAt(TileChopping te, double x, double y, double z, float partialTicks, int destroyStage, float p_192841_10_)
    {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (state.getBlock() != Survivalist.chopping_block)
            return;

        if (destroyStage < 0)
        {
            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.65, z + 0.5);

            IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            assert inv != null;
            ItemStack stack = inv.getStackInSlot(0);
            if (stack.getCount() > 0)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, -4.5 / 16.0f, 0);
                GlStateManager.scale(2, 2, 2);
                GlStateManager.color(1f, 1f, 1f, 1f);

                mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
                /*int breakStage = te.getBreakStage();
                if (breakStage >= 0)
                {
                    renderItem(stack, ItemCameraTransforms.TransformType.GROUND, breakStage);
                }*/

                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
        }
    }
}
