package gigaherz.survivalist.rack;

import com.mojang.blaze3d.platform.GlStateManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RenderRack extends TileEntityRenderer<TileRack>
{
    @Override
    public void render(TileRack te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        BlockState state = te.getWorld().getBlockState(te.getPos());
        if (state.getBlock() != Survivalist.Blocks.rack)
            return;

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((inv) -> {
            bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.disableLighting();

            GlStateManager.pushMatrix();

            float angle = -state.get(BlockRack.FACING).getHorizontalAngle();
            GlStateManager.translated(x + 0.5, y + 0.65, z + 0.5);
            GlStateManager.rotated(angle, 0, 1, 0);

            Minecraft mc = Minecraft.getInstance();

            ItemRenderer renderItem = mc.getItemRenderer();
            World world = mc.world;
            for (int i = 0; i < 4; i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.getCount() > 0)
                {
                    GlStateManager.pushMatrix();

                    float zz = (i - 1.5f) * 0.1875f;

                    GlStateManager.translated(0, 0, zz);

                    GlStateManager.scalef(1.5f,1.5f,1.5f);

                    GlStateManager.color4f(1f, 1f, 1f, 1f);

                    mc.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

                    IBakedModel model = renderItem.getItemModelWithOverrides(stack, world, null);
                    model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.FIXED, false);
                    if (model.isBuiltInRenderer())
                    {
                        renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                    }

                    GlStateManager.popMatrix();
                }
            }

            GlStateManager.popMatrix();

            GlStateManager.enableLighting();
        });
    }
}
