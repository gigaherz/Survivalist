package gigaherz.survivalist.rack;

import com.mojang.blaze3d.matrix.MatrixStack;
import gigaherz.survivalist.SurvivalistBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

public class DryingRackRenderer extends TileEntityRenderer<DryingRackTileEntity>
{
    public DryingRackRenderer(TileEntityRendererDispatcher p_i226006_1_)
    {
        super(p_i226006_1_);
    }

    @Override
    public void render(DryingRackTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_)
    {
        BlockState state = te.getWorld().getBlockState(te.getPos());
        if (state.getBlock() != SurvivalistBlocks.RACK.get())
            return;

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((inv) -> {
            matrixStack.push(); // pushMatrix

            float angle = -state.get(DryingRackBlock.FACING).getHorizontalAngle();

            matrixStack.translate(0.5, 0.5, 0.5);    // translate
            matrixStack.rotate(Vector3f.YP.rotationDegrees(angle));        // rotate
            matrixStack.translate(-0.5, -0.5, -0.5); // translate
            //matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180));

            Minecraft mc = Minecraft.getInstance();

            ItemRenderer itemRenderer = mc.getItemRenderer();
            for (int i = 0; i < 4; i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.getCount() > 0)
                {
                    matrixStack.push(); // pushMatrix

                    float zz = (i - 1.5f) * 0.1875f;

                    matrixStack.translate(0, 0, zz); // translate

                    matrixStack.scale(0.7f, 0.7f, 0.7f); // scale

                    matrixStack.translate(0.715, 0.93, 0.635); // translate
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(180)); // rotate

                    IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stack, te.getWorld(), (LivingEntity)null);
                    // FIXME: Fix baked model.
                    //if (ibakedmodel.isBuiltInRenderer())
                    {
                        itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, true, matrixStack, buffer, p_225616_5_, p_225616_6_, ibakedmodel); // renderItem
                    }

                    matrixStack.pop(); // popMatrix
                }
            }

            matrixStack.pop(); // popMatrix
        });
    }
}
