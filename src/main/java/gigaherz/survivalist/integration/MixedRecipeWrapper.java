package gigaherz.survivalist.integration;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;

public abstract class MixedRecipeWrapper extends BlankRecipeWrapper
{
    protected String inputOredict;
    protected ItemStack inputStack;
    protected ItemStack output;

    protected MixedRecipeWrapper(String input, ItemStack output)
    {
        this.inputOredict = input;
        this.output = output;
    }

    protected MixedRecipeWrapper(ItemStack input, ItemStack output)
    {
        this.inputStack = input;
        this.output = output;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        if (inputOredict != null)
            ingredients.setInputLists(ItemStack.class, Collections.singletonList(OreDictionary.getOres(inputOredict)));
        if (inputStack != null)
            ingredients.setInput(ItemStack.class, inputStack);
        ingredients.setOutput(ItemStack.class, output);
    }
}
