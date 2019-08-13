package gigaherz.survivalist.api;

import com.google.common.collect.Lists;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class Dryable
{
    public static abstract class DryingRecipe
    {
        private int time;
        private ItemStack output;

        public DryingRecipe(int time, ItemStack output)
        {
            this.time = time;
            this.output = output;
        }

        public int getTime()
        {
            return time;
        }

        public ItemStack getOutput()
        {
            return output;
        }

        public abstract boolean accepts(ItemStack stack);
    }

    public static class DryingItemRecipe extends DryingRecipe
    {
        private ItemStack input;

        public DryingItemRecipe(ItemStack input, int time, ItemStack output)
        {
            super(time, output);
            this.input = input;
        }

        public ItemStack getInput()
        {
            return input;
        }

        public boolean accepts(ItemStack stack)
        {
            return ItemStack.areItemStacksEqual(input, stack);
        }
    }

    public static final List<DryingRecipe> RECIPES = Lists.newArrayList();

    public static void registerStockRecipes()
    {
        /*
        if (ConfigManager.instance.enableLeatherTanning)
        {
            registerRecipe(new ItemStack(Items.LEATHER), new ItemStack(Survivalist.Items.tanned_leather), 30 * 20);
        }

        if (ConfigManager.instance.enableMeatRotting)
        {
            registerRecipe(new ItemStack(Items.BEEF), new ItemStack(Items.ROTTEN_FLESH), 15 * 20);
            registerRecipe(new ItemStack(Items.MUTTON), new ItemStack(Items.ROTTEN_FLESH), 15 * 20);
            registerRecipe(new ItemStack(Items.PORKCHOP), new ItemStack(Items.ROTTEN_FLESH), 15 * 20);
        }

        if (ConfigManager.instance.enableJerky)
        {
            if (ConfigManager.instance.enableRottenDrying)
            {
                registerRecipe(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Survivalist.Items.jerky), 15 * 20);
            }
            if (ConfigManager.instance.enableMeatDrying)
            {
                registerRecipe(new ItemStack(Items.COOKED_BEEF), new ItemStack(Survivalist.Items.jerky), 15 * 20);
                registerRecipe(new ItemStack(Items.COOKED_MUTTON), new ItemStack(Survivalist.Items.jerky), 15 * 20);
                registerRecipe(new ItemStack(Items.COOKED_PORKCHOP), new ItemStack(Survivalist.Items.jerky), 15 * 20);
            }
        }
         */
    }

    public static int getDryingTime(ItemStack stack)
    {
        for (DryingRecipe recipe : RECIPES)
        {
            if (recipe.accepts(stack))
                return recipe.getTime();
        }

        return -1;
    }

    public static ItemStack getDryingResult(ItemStack stack)
    {
        for (DryingRecipe recipe : RECIPES)
        {
            if (recipe.accepts(stack))
                return recipe.getOutput().copy();
        }

        return ItemStack.EMPTY;
    }
}
