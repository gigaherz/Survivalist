package gigaherz.survivalist.integration.drying;

import com.google.common.collect.Lists;
import gigaherz.survivalist.api.Dryable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class DryingRecipeWrapper extends BlankRecipeWrapper
{
    public static List<DryingRecipeWrapper> getRecipes()
    {
        List<DryingRecipeWrapper> list = Lists.newArrayList();

        for (Dryable.DryingRecipe pair : Dryable.RECIPES)
        {
            if (pair instanceof Dryable.DryingItemRecipe)
            {
                list.add(new DryingRecipeWrapper.ItemInput((Dryable.DryingItemRecipe) pair));
            }
            else if (pair instanceof Dryable.DryingOreRecipe)
            {
                list.add(new DryingRecipeWrapper.OreInput((Dryable.DryingOreRecipe) pair));
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

    private int time;
    private ItemStack output;

    private DryingRecipeWrapper(ItemStack output, int time)
    {
        this.output = output;
        this.time = time;
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

        String label = (time / 20.0) + "s";
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 150);
        mc.fontRendererObj.drawString(label, 58 - mc.fontRendererObj.getStringWidth(label), 14, 0xFFFFFFFF, true);
        GlStateManager.popMatrix();
    }

    private static class ItemInput extends DryingRecipeWrapper
    {
        private ItemStack inputStack;

        private ItemInput(ItemStack input, ItemStack output, int time)
        {
            super(output, time);
            this.inputStack = input;
        }

        public ItemInput(Dryable.DryingItemRecipe recipe)
        {
            this(recipe.getInput(), copyWithSize(recipe.getOutput()), recipe.getTime());
        }

        @Override
        public void getIngredients(IIngredients ingredients)
        {
            ingredients.setInput(ItemStack.class, inputStack);
            super.getIngredients(ingredients);
        }
    }

    private static class OreInput extends DryingRecipeWrapper
    {
        private String inputOredict;

        private OreInput(String input, ItemStack output, int time)
        {
            super(output, time);
            this.inputOredict = input;
        }

        public OreInput(Dryable.DryingOreRecipe recipe)
        {
            this(recipe.getOreName(), copyWithSize(recipe.getOutput()), recipe.getTime());
        }

        @Override
        public void getIngredients(IIngredients ingredients)
        {
            ingredients.setInputLists(ItemStack.class, Collections.singletonList(OreDictionary.getOres(inputOredict)));
            super.getIngredients(ingredients);
        }
    }
}
