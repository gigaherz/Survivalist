package gigaherz.survivalist.chopblock;

import gigaherz.survivalist.api.Choppable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class TileChopping extends TileEntity
{
    private static final Random RANDOM = new Random();

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
            if (!Choppable.isValidInput(stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            breakingProgress = 0;
            if (world != null)
            {
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
            markDirty();
        }
    };

    // measured in the number of ticks it will take to return to 0
    private int breakingProgress = 0;

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
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
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

    public boolean chop(EntityPlayer playerIn, int axeLevel, int fortune)
    {
        boolean completed = false;
        if (slotInventory.getStackInSlot(0) != null)
        {
            breakingProgress += 25 + Choppable.getHitCountMultiplier(slotInventory.getStackInSlot(0)) * 25 * Math.max(0, axeLevel);
            if (breakingProgress >= 200)
            {
                if (!world.isRemote)
                {
                    Choppable.ChoppingRecipe recipe = Choppable.find(slotInventory.getStackInSlot(0));
                    if (recipe != null)
                    {
                        ItemStack out = recipe.getResults(slotInventory.getStackInSlot(0), playerIn, axeLevel, fortune, RANDOM);

                        if (out != null)
                        {
                            //ItemHandlerHelper.giveItemToPlayer(playerIn, out);
                            spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, out);
                        }
                    }

                    completed = true;
                }
                world.playSound(playerIn, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                slotInventory.setStackInSlot(0, null);
                breakingProgress = 0;
            }

            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
        return completed;
    }

    public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack)
    {
        while (stack.stackSize > 0)
        {
            int i = /*RANDOM.nextInt(3) +*/ 1;

            if (i > stack.stackSize)
            {
                i = stack.stackSize;
            }

            ItemStack copy = stack.copy();
            copy.stackSize = i;
            stack.stackSize -= i;
            EntityItem entityitem = new EntityItem(worldIn, x, y, z, copy);

            entityitem.setPickupDelay(15);

            entityitem.motionX = RANDOM.nextGaussian() * 0.02;
            entityitem.motionY = RANDOM.nextGaussian() * 0.02 + 0.2;
            entityitem.motionZ = RANDOM.nextGaussian() * 0.02;
            worldIn.spawnEntity(entityitem);
        }
    }

    public ItemStackHandler getSlotInventory()
    {
        return slotInventory;
    }

    public int getBreakStage()
    {
        if (breakingProgress <= 0)
            return -1;
        return breakingProgress * 10 / 200;
    }
}
