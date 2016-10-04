package gigaherz.survivalist.integration.drying;

import com.google.common.collect.Lists;
import gigaherz.survivalist.api.Dryable;
import gigaherz.survivalist.integration.MixedRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.List;

public class DryingRecipeWrapper extends MixedRecipeWrapper
{
    public static List<DryingRecipeWrapper> getRecipes()
    {
        List<DryingRecipeWrapper> list = Lists.newArrayList();

        for (Triple<ItemStack, Integer, ItemStack> pair : Dryable.RECIPES)
        {
            list.add(new DryingRecipeWrapper(pair.getLeft(), pair.getRight(), pair.getMiddle()));
        }

        return list;
    }

    int time;

    private DryingRecipeWrapper(String input, ItemStack output, int time)
    {
        super(input, output);
        this.time = time;
    }

    private DryingRecipeWrapper(ItemStack input, ItemStack output, int time)
    {
        super(input, output);
        this.time = time;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        super.drawInfo(mc, recipeWidth, recipeHeight, mouseX, mouseY);

        String label = (time / 20.0) + "s";
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 150);
        mc.fontRendererObj.drawString(label, 58 - mc.fontRendererObj.getStringWidth(label), 14, 0xFFFFFFFF, true);
        GlStateManager.popMatrix();
    }
}
