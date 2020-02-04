package gigaherz.survivalist.chopblock;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ChoppingBlockRenderer extends TileEntityRenderer<ChoppingBlockTileEntity>
{
    private final Minecraft mc = Minecraft.getInstance();

    public ChoppingBlockRenderer(TileEntityRendererDispatcher p_i226006_1_)
    {
        super(p_i226006_1_);
    }

    @Override
    public void render(ChoppingBlockTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_)
    {
        BlockState state = te.getWorld().getBlockState(te.getPos());
        if (!(state.getBlock() instanceof ChoppingBlock))
            return;

        //if (destroyStage < 0)
        {
            matrixStack.push();

            ItemRenderer itemRenderer = mc.getItemRenderer();

            LazyOptional<IItemHandler> linv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            linv.ifPresent((inv) -> {
                ItemStack stack = inv.getStackInSlot(0);
                if (stack.getCount() > 0)
                {
                    matrixStack.push();
                    matrixStack.translate(0.5, 0.5, 0.5);

                    matrixStack.translate(0, -4.5 / 16.0f, 0);
                    matrixStack.scale(2, 2, 2);

                    IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stack, te.getWorld(), (LivingEntity)null);
                    itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.GROUND, true, matrixStack, buffer, p_225616_5_, p_225616_6_, ibakedmodel);
                    /*int breakStage = te.getBreakStage();
                    if (breakStage >= 0)
                    {
                        renderItem(stack, ItemCameraTransforms.TransformType.GROUND, breakStage);
                    }*/

                    matrixStack.pop();
                }
            });

            matrixStack.pop();
        }
    }
}
