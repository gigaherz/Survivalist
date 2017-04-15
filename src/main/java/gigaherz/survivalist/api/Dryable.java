package gigaherz.survivalist.api;

import com.google.common.collect.Lists;
import gigaherz.common.OreDictionaryHelper;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
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
            return OreDictionary.itemMatches(input, stack, false);
        }
    }

    public static class DryingOreRecipe extends DryingRecipe
    {
        private String oreName;

        public DryingOreRecipe(String oreName, int time, ItemStack right)
        {
            super(time, right);
            this.oreName = oreName;
        }

        public String getOreName()
        {
            return oreName;
        }

        @Override
        public boolean accepts(ItemStack stack)
        {
            return OreDictionaryHelper.hasOreName(stack, oreName);
        }
    }

    public static final List<DryingRecipe> RECIPES = Lists.newArrayList();

    public static void registerStockRecipes()
    {
        if (ConfigManager.instance.enableLeatherTanning)
        {
            registerRecipe(new ItemStack(Items.LEATHER), new ItemStack(Survivalist.tanned_leather), 30 * 20);
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
                registerRecipe(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Survivalist.jerky), 15 * 20);
            }
            if (ConfigManager.instance.enableMeatDrying)
            {
                registerRecipe(new ItemStack(Items.COOKED_BEEF), new ItemStack(Survivalist.jerky), 15 * 20);
                registerRecipe(new ItemStack(Items.COOKED_MUTTON), new ItemStack(Survivalist.jerky), 15 * 20);
                registerRecipe(new ItemStack(Items.COOKED_PORKCHOP), new ItemStack(Survivalist.jerky), 15 * 20);
            }
        }
    }

    public static DryingRecipe registerRecipe(ItemStack input, ItemStack output, int time)
    {
        return registerRecipe(new DryingItemRecipe(input, time, output));
    }

    public static DryingRecipe registerRecipe(String input, ItemStack output, int time)
    {
        return registerRecipe(new DryingOreRecipe(input, time, output));
    }

    private static DryingRecipe registerRecipe(DryingRecipe recipe)
    {
        RECIPES.add(recipe);
        return recipe;
    }

    public static int getDryingTime(@Nullable ItemStack stack)
    {
        if (stack == null)
            return -1;

        for (DryingRecipe recipe : RECIPES)
        {
            if (recipe.accepts(stack))
                return recipe.getTime();
        }

        return -1;
    }

    @Nullable
    public static ItemStack getDryingResult(@Nullable ItemStack stack)
    {
        if (stack == null)
            return null;

        for (DryingRecipe recipe : RECIPES)
        {
            if (recipe.accepts(stack))
                return recipe.getOutput().copy();
        }

        return null;
    }
}
