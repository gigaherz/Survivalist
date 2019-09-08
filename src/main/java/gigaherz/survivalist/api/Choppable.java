package gigaherz.survivalist.api;

import com.google.common.collect.Lists;
import gigaherz.survivalist.ConfigManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.Random;

public class Choppable
{
    public static class ChoppingContext implements IInventory
    {
        final IItemHandlerModifiable inner;

        final PlayerEntity player;
        final int axeLevel;
        final int fortune;
        final Random random;

        public ChoppingContext(IItemHandlerModifiable inner, PlayerEntity player, int axeLevel, int fortune, Random random)
        {
            this.inner = inner;
            this.player = player;
            this.axeLevel = axeLevel;
            this.fortune = fortune;
            this.random = random;
        }

        @Override
        public int getSizeInventory()
        {
            return inner.getSlots();
        }

        @Override
        public boolean isEmpty()
        {
            for(int i=0;i<inner.getSlots();i++)
            {
                if (inner.getStackInSlot(i).getCount() > 0)
                    return false;
            }
            return true;
        }

        @Override
        public ItemStack getStackInSlot(int index)
        {
            return inner.getStackInSlot(index);
        }

        @Override
        public ItemStack decrStackSize(int index, int count)
        {
            return inner.extractItem(index, count, false);
        }

        @Override
        public ItemStack removeStackFromSlot(int index)
        {
            return inner.extractItem(index, 64, false);
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack)
        {
            inner.setStackInSlot(index, stack);
        }

        @Override
        public void markDirty()
        {
        }

        @Override
        public boolean isUsableByPlayer(PlayerEntity player)
        {
            return true;
        }

        @Override
        public void clear()
        {
            for(int i=0;i<inner.getSlots();i++)
            {
                inner.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        public PlayerEntity getPlayer()
        {
            return player;
        }

        public IItemHandlerModifiable getInner()
        {
            return inner;
        }

        public int getAxeLevel()
        {
            return axeLevel;
        }

        public int getFortune()
        {
            return fortune;
        }

        public Random getRandom()
        {
            return random;
        }
    }

    public static class ChoppingRecipe implements IRecipe<ChoppingContext>
    {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient input;
        private final ItemStack output;
        private final double outputMultiplier ;
        private final double hitCountMultiplier;
        private final int maxOutput;
        private final int sawingTime;

        public ChoppingRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output, double outputMultiplier, double hitCountMultiplier, int maxOutput,
                              int sawingTime)
        {
            this.id = id;
            this.group = group;
            this.input = input;
            this.output = output;
            this.outputMultiplier = outputMultiplier;
            this.hitCountMultiplier = hitCountMultiplier;
            this.maxOutput = maxOutput;
            this.sawingTime = sawingTime;
        }

        public ItemStack getOutput()
        {
            return output;
        }

        public double getOutputMultiplier()
        {
            return outputMultiplier;
        }


        public double getHitCountMultiplier()
        {
            return hitCountMultiplier;
        }

        public int getSawingTime()
        {
            return sawingTime;
        }

        public int getMaxOutput()
        {
            return maxOutput;
        }

        public ItemStack getResults(ItemStack input, PlayerEntity player, int axeLevel, int fortune, Random random)
        {
            double number = ConfigManager.choppingWithEmptyHand * getOutputMultiplier();

            if (axeLevel >= 0)
                number = Math.max(0, getOutputMultiplier() * ConfigManager.getAxeLevelMultiplier(axeLevel)) * (1 + random.nextFloat() * fortune);

            int whole = (int) Math.floor(number);
            double remainder = number - whole;

            if (random.nextFloat() < remainder)
            {
                whole++;
            }

            if (getMaxOutput() > 0)
                whole = Math.min(whole, getMaxOutput());

            if (whole > 0)
            {
                ItemStack out = getOutput().copy();
                out.setCount(whole);
                return out;
            }

            return ItemStack.EMPTY;
        }

        public ItemStack getResultsSawmill()
        {
            double number = Math.max(0, getOutputMultiplier() * 4);

            int whole = (int) Math.floor(number);

            if (getMaxOutput() > 0)
                whole = Math.min(whole, getMaxOutput());

            if (whole > 0)
            {
                ItemStack out = getOutput().copy();
                out.setCount(whole);
                return out;
            }

            return ItemStack.EMPTY;
        }

        @Override
        public boolean matches(ChoppingContext inv, World worldIn)
        {
            return input.test(inv.getStackInSlot(0));
        }

        @Override
        public ItemStack getCraftingResult(ChoppingContext inv)
        {
            return inv.getPlayer() != null
                    ? getResults(inv.getStackInSlot(0), inv.getPlayer(), inv.getAxeLevel(), inv.getFortune(), inv.getRandom())
                    : getResultsSawmill();
        }

        @Override
        public boolean canFit(int width, int height)
        {
            return true;
        }

        @Override
        public ItemStack getRecipeOutput()
        {
            return output;
        }

        @Override
        public ResourceLocation getId()
        {
            return id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer()
        {
            return null;
        }

        @Override
        public IRecipeType<?> getType()
        {
            return null;
        }
    }

    public static void registerStockRecipes()
    {
        //registerRecipe("plankWood", new ItemStack(Items.STICK)).setOutputMultiplier(2.0);
        if (ConfigManager.enableStringCrafting)
        {
            //registerRecipe(new ItemStack(Tags.Items., 1), new ItemStack(Items.STRING)).setMaxOutput(4);
        }
    }

    public static ChoppingRecipe find(ItemStack stack)
    {
        /*if (stack.getCount() <= 0)
            return null;

        for (ChoppingRecipe recipe : RECIPES)
        {
            if (recipe.accepts(stack))
                return recipe;
        }
*/
        return null;
    }

    public static boolean isValidInput(ItemStack stack)
    {
        return find(stack) != null;
    }

    public static double getHitCountMultiplier(ItemStack stack)
    {
        ChoppingRecipe recipe = find(stack);
        return recipe != null ? recipe.getHitCountMultiplier() : 0;
    }

    public static int getSawmillTime(ItemStack stack)
    {
        ChoppingRecipe recipe = find(stack);
        return recipe != null ? recipe.getSawingTime() : 0;
    }
}
