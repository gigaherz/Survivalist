package gigaherz.survivalist.chopblock;

import com.google.common.collect.Lists;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TileChopping extends TileEntity implements ITickable
{
    private static List<Triple<ItemStack, ItemStack, Double>> recipes = Lists.newArrayList();
    private static List<Triple<String, ItemStack, Double>> oreDictRecipes = Lists.newArrayList();

    public static boolean isValidInput(ItemStack stack)
    {
        for (Triple<ItemStack, ItemStack, Double> recipe : recipes)
        {
            if (OreDictionary.itemMatches(recipe.getLeft(), stack, false))
                return true;
        }
        for (Triple<String, ItemStack, Double> recipe : oreDictRecipes)
        {
            if (Survivalist.hasOreName(stack, recipe.getLeft()))
                return true;
        }
        return false;
    }

    public static Pair<ItemStack, Double> getResults(ItemStack stack)
    {
        for (Triple<ItemStack, ItemStack, Double> recipe : recipes)
        {
            if (OreDictionary.itemMatches(recipe.getLeft(), stack, false))
                return Pair.of(recipe.getMiddle().copy(), recipe.getRight());
        }
        for (Triple<String, ItemStack, Double> recipe : oreDictRecipes)
        {
            if (Survivalist.hasOreName(stack, recipe.getLeft()))
                return Pair.of(recipe.getMiddle().copy(), recipe.getRight());
        }
        return null;
    }

    public static void registerStockRecipes()
    {
        registerRecipe("plankWood", new ItemStack(Items.STICK), 2.0);
    }

    public static void registerRecipe(ItemStack input, ItemStack output)
    {
        registerRecipe(input, output, 1.0);
    }

    public static void registerRecipe(ItemStack input, ItemStack output, double outputMultiplier)
    {
        recipes.add(Triple.of(input, output, outputMultiplier));
    }

    public static void registerRecipe(String input, ItemStack output)
    {
        registerRecipe(input, output, 1.0);
    }

    public static void registerRecipe(String input, ItemStack output, double outputMultiplier)
    {
        oreDictRecipes.add(Triple.of(input, output, outputMultiplier));
    }

    private ItemStackHandler slotInventory = new ItemStackHandler()
    {
        @Override
        protected int getStackLimit(int slot, ItemStack stack)
        {
            return 1;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            if (!isValidInput(stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            breakingProgress = 0;
            if (worldObj != null)
            {
                IBlockState state = worldObj.getBlockState(pos);
                worldObj.notifyBlockUpdate(pos, state, state, 3);
            }
            markDirty();
        }
    };

    // measured in the number of ticks it will take to return to 0
    private int breakingProgress = 0;

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) slotInventory;
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(slotInventory, null, compound.getTag("Inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setTag("Inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(slotInventory, null));
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void update()
    {
        if (breakingProgress > 0)
        {
            breakingProgress--;

            if (breakingProgress == 0)
            {
                IBlockState state = worldObj.getBlockState(pos);
                worldObj.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    public void chop(EntityPlayer playerIn, int axeLevel, int fortune)
    {
        if (slotInventory.getStackInSlot(0) != null)
        {
            breakingProgress += 40;
            if (breakingProgress >= 40 * 5)
            {
                Pair<ItemStack, Double> res = getResults(slotInventory.getStackInSlot(0));
                ItemStack out = res.getLeft();
                out.stackSize = (int) (res.getRight() * (1 + axeLevel) * (1 + fortune));
                slotInventory.setStackInSlot(0, null);
                breakingProgress = 0;
                if (!worldObj.isRemote)
                {
                    spawnItemStack(worldObj, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, out);
                    worldObj.playSound(playerIn, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 0.3f, 1.0f);
                }
            }

            IBlockState state = worldObj.getBlockState(pos);
            worldObj.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    private static final Random RANDOM = new Random();

    public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack)
    {
        while (stack.stackSize > 0)
        {
            int i = /*RANDOM.nextInt(3) +*/ 1;

            if (i > stack.stackSize)
            {
                i = stack.stackSize;
            }

            stack.stackSize -= i;
            EntityItem entityitem = new EntityItem(worldIn, x, y, z, new ItemStack(stack.getItem(), i, stack.getMetadata()));

            if (stack.hasTagCompound())
            {
                entityitem.getEntityItem().setTagCompound(stack.getTagCompound().copy());
            }

            entityitem.setPickupDelay(15);

            entityitem.motionX = RANDOM.nextGaussian() * 0.02;
            entityitem.motionY = RANDOM.nextGaussian() * 0.02 + 0.2;
            entityitem.motionZ = RANDOM.nextGaussian() * 0.02;
            worldIn.spawnEntityInWorld(entityitem);
        }
    }

    public ItemStackHandler getSlotInventory()
    {
        return slotInventory;
    }
}
