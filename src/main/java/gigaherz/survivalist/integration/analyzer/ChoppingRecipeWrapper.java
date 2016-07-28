package gigaherz.survivalist.integration.analyzer;

import com.google.common.collect.Lists;
import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.client.StackRenderingHelper;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ChoppingRecipeWrapper extends BlankRecipeWrapper
{
    public static List<ChoppingRecipeWrapper> getRecipes()
    {
        List<ChoppingRecipeWrapper> list = Lists.newArrayList();

        for (Triple<ItemStack, ItemStack, Double> pair : Choppable.RECIPES)
        {
            list.add(new ChoppingRecipeWrapper(pair.getLeft(), copyWithSize(pair.getMiddle()), pair.getRight()));
        }

        for (Triple<String, ItemStack, Double> pair : Choppable.ORE_RECIPES)
        {
            list.add(new ChoppingRecipeWrapper(pair.getLeft(), copyWithSize(pair.getMiddle()), pair.getRight()));
        }

        return list;
    }

    private static ItemStack copyWithSize(ItemStack middle)
    {
        middle = middle.copy();
        middle.stackSize = 1;
        return middle;
    }

    private List<Object> inputs;
    private List<ItemStack> outputs;
    private double multiplier;

    private ChoppingRecipeWrapper(Object input, ItemStack output, double multiplier)
    {
        inputs = Collections.singletonList(input);
        outputs = Collections.singletonList(output);
        this.multiplier = multiplier;
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

        ItemModelMesher mesher = mc.getRenderItem().getItemModelMesher();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 100);

        long animTick = mc.theWorld.getTotalWorldTime();
        int whichAxe = (int) ((animTick / 20) % 5);

        double amount = 1.0;
        ItemStack stack = null;
        switch (whichAxe)
        {
            case 0: // Hand
                amount = 0.4 * multiplier;
                stack = null;
                break;
            case 1: // wood
                amount = 1.0 * multiplier;
                stack = new ItemStack(Items.WOODEN_AXE);
                break;
            case 2: // stone
                amount = 2.0 * multiplier;
                stack = new ItemStack(Items.STONE_AXE);
                break;
            case 3: // iron
                amount = 3.0 * multiplier;
                stack = new ItemStack(Items.IRON_AXE);
                break;
            case 4: // diamond
                amount = 4.0 * multiplier;
                stack = new ItemStack(Items.DIAMOND_AXE);
                break;
        }

        if (stack != null)
        {
            StackRenderingHelper.renderItemStack(mesher, mc.renderEngine, 28, 4, stack, 0xFFFFFFFF);
        }

        String label = amount + "x";
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 150);
        mc.fontRendererObj.drawString(label, 58 - mc.fontRendererObj.getStringWidth(label), 14, 0xFFFFFFFF, true);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }
}
