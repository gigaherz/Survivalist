package gigaherz.survivalist.integration.chopping;

import com.google.common.collect.Lists;
import gigaherz.common.client.StackRenderingHelper;
import gigaherz.survivalist.api.Choppable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ChoppingRecipeWrapper extends BlankRecipeWrapper
{
    public static List<ChoppingRecipeWrapper> getRecipes()
    {
        List<ChoppingRecipeWrapper> list = Lists.newArrayList();

        for (Choppable.ChoppingRecipe pair : Choppable.RECIPES)
        {
            if (pair instanceof Choppable.ChoppingItemRecipe)
            {
                list.add(new ChoppingRecipeWrapper.ItemInput((Choppable.ChoppingItemRecipe) pair));
            }
            else if (pair instanceof Choppable.ChoppingOreRecipe)
            {
                list.add(new ChoppingRecipeWrapper.OreInput((Choppable.ChoppingOreRecipe) pair));
            }
        }

        return list;
    }

    private static ItemStack copyWithSize(ItemStack middle)
    {
        middle = middle.copy();
        middle.stackSize = 1;
        return middle;
    }

    private double multiplier;
    private ItemStack output;

    private ChoppingRecipeWrapper(ItemStack output, double multiplier)
    {
        this.output = output;
        this.multiplier = multiplier;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        super.drawInfo(mc, recipeWidth, recipeHeight, mouseX, mouseY);

        ItemModelMesher mesher = mc.getRenderItem().getItemModelMesher();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 100);

        long animTick = mc.world.getTotalWorldTime();
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

    private static class ItemInput extends ChoppingRecipeWrapper
    {
        private ItemStack inputStack;

        private ItemInput(ItemStack input, ItemStack output, double multiplier)
        {
            super(output, multiplier);
            this.inputStack = input;
        }

        public ItemInput(Choppable.ChoppingItemRecipe recipe)
        {
            this(recipe.getInput(), copyWithSize(recipe.getOutput()), recipe.getOutputMultiplier());
        }

        @Override
        public void getIngredients(IIngredients ingredients)
        {
            ingredients.setInput(ItemStack.class, inputStack);
            super.getIngredients(ingredients);
        }
    }

    private static class OreInput extends ChoppingRecipeWrapper
    {
        private String inputOredict;

        private OreInput(String input, ItemStack output, double multiplier)
        {
            super(output, multiplier);
            this.inputOredict = input;
        }

        public OreInput(Choppable.ChoppingOreRecipe recipe)
        {
            this(recipe.getOreName(), copyWithSize(recipe.getOutput()), recipe.getOutputMultiplier());
        }

        @Override
        public void getIngredients(IIngredients ingredients)
        {
            ingredients.setInputLists(ItemStack.class, Collections.singletonList(OreDictionary.getOres(inputOredict)));
            super.getIngredients(ingredients);
        }
    }
}
