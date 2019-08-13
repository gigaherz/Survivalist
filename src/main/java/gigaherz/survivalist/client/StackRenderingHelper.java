package gigaherz.survivalist.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class StackRenderingHelper
{
    public static void renderItemStack(ItemModelMesher mesher, TextureManager renderEngine, int xPos, int yPos, ItemStack stack, int color)
    {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        renderEngine.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        renderEngine.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.pushMatrix();

        IBakedModel model = mesher.getItemModel(stack);
        setupGuiTransform(xPos, yPos, model.isGui3d());
        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);

        GlStateManager.translatef(-0.5F, -0.5F, -0.5F);

        renderItem(model, color);

        GlStateManager.popMatrix();

        renderEngine.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        renderEngine.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        RenderHelper.disableStandardItemLighting();
    }

    private static void renderItem(IBakedModel model, int color)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

        Random random = new Random();
        random.setSeed(42);

        for (BakedQuad bakedquad : model.getQuads(null, null, random))
        {
            LightUtil.renderQuadColor(worldrenderer, bakedquad, color);
        }

        tessellator.draw();
    }

    private static void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d)
    {
        GlStateManager.translatef(xPosition, yPosition, 150);
        GlStateManager.translatef(8.0F, 8.0F, 0.0F);
        GlStateManager.scalef(1.0F, -1.0F, 1.0F);
        GlStateManager.scalef(16.0F, 16.0F, 16.0F);

        if (isGui3d)
        {
            GlStateManager.enableLighting();
        }
        else
        {
            GlStateManager.disableLighting();
        }
    }
}
