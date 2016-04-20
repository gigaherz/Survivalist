package gigaherz.survivalist.rack;

import gigaherz.survivalist.Survivalist;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RenderRack extends TileEntitySpecialRenderer<TileRack>
{
    @Override
    public void renderTileEntityAt(TileRack te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (state.getBlock() != Survivalist.rack)
            return;

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.disableLighting();

        IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        GlStateManager.pushMatrix();

        float angle = state.getValue(BlockRack.FACING).getHorizontalAngle();
        GlStateManager.translate(x + 0.5, y + 0.65, z + 0.5);
        GlStateManager.rotate(angle, 0, 1, 0);

        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null)
            {
                GlStateManager.pushMatrix();

                float zz = (i - 1.5f) * 0.1875f;

                GlStateManager.translate(0, 0, zz);

                GlStateManager.color(1f, 1f, 1f, 1f);

                Minecraft mc = Minecraft.getMinecraft();
                mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();

        GlStateManager.enableLighting();
    }
}
