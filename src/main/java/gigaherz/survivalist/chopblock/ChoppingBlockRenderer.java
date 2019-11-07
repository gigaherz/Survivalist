package gigaherz.survivalist.chopblock;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ChoppingBlockRenderer extends TileEntityRenderer<ChoppingBlockTileEntity>
{
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(ChoppingBlockTileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        BlockState state = te.getWorld().getBlockState(te.getPos());
        if (!(state.getBlock() instanceof ChoppingBlock))
            return;

        if (destroyStage < 0)
        {
            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translated(x + 0.5, y + 0.65, z + 0.5);

            LazyOptional<IItemHandler> linv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            linv.ifPresent((inv) -> {
                ItemStack stack = inv.getStackInSlot(0);
                if (stack.getCount() > 0)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translated(0, -4.5 / 16.0f, 0);
                    GlStateManager.scaled(2, 2, 2);
                    GlStateManager.color4f(1f, 1f, 1f, 1f);

                    mc.getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
                /*int breakStage = te.getBreakStage();
                if (breakStage >= 0)
                {
                    renderItem(stack, ItemCameraTransforms.TransformType.GROUND, breakStage);
                }*/

                    GlStateManager.popMatrix();
                }
            });

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
        }
    }
}
