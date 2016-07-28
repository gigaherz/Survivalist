package gigaherz.survivalist.integration.drying;

import com.google.common.collect.Lists;
import gigaherz.survivalist.api.Dryable;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class DryingRecipeWrapper extends BlankRecipeWrapper
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

    List<Object> inputs;
    List<ItemStack> outputs;
    int time;

    private DryingRecipeWrapper(Object input, ItemStack output, int time)
    {
        inputs = Collections.singletonList(input);
        outputs = Collections.singletonList(output);
        this.time = time;
    }

    @Nonnull
    @Override
    public List getInputs()
    {
        return inputs;
    }

    @Nonnull
    @Override
    public List getOutputs()
    {
        return outputs;
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
