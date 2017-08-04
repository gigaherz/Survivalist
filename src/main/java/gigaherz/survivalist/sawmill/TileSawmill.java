package gigaherz.survivalist.sawmill;

import gigaherz.survivalist.api.Choppable;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileSawmill extends TileEntity implements ITickable
{
    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEMS_CAP;

    private final ItemStackHandler inventory = new ItemStackHandler(3);
    private final RangedWrapper top = new RangedWrapper(inventory, 0, 1);
    private final RangedWrapper sides = new RangedWrapper(inventory, 1, 2)
    {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (!TileEntityFurnace.isItemFuel(stack))
                return stack;

            return super.insertItem(slot, stack, simulate);
        }
    };
    private final RangedWrapper bottom = new RangedWrapper(inventory, 2, 3)
    {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            return stack;
        }
    };

    private int remainingBurnTime;
    private int totalBurnTime;
    private int cookTime;
    private int totalCookTime;

    public boolean isBurning()
    {
        return remainingBurnTime > 0;
    }

    public ItemStackHandler getInventory()
    {
        return inventory;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == ITEMS_CAP)
            return true;

        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == ITEMS_CAP)
        {
            if (facing == EnumFacing.UP) return (T)top;
            if (facing == EnumFacing.DOWN) return (T)bottom;
            if (facing != null) return (T)sides;
            return (T)inventory;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        ITEMS_CAP.readNBT(inventory, null, compound.getTag("Items"));

        remainingBurnTime = compound.getInteger("BurnTime");
        totalBurnTime = TileEntityFurnace.getItemBurnTime(inventory.getStackInSlot(1));

        cookTime = compound.getInteger("CookTime");
        totalCookTime = Choppable.find(inventory.getStackInSlot(0)).getSawmillTime();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);

        compound.setTag("Items", ITEMS_CAP.writeNBT(inventory, null));

        compound.setInteger("BurnTime", (short)this.remainingBurnTime);
        compound.setInteger("CookTime", (short)this.cookTime);

        return compound;
    }

    public int[] getFields()
    {
        return new int[]{remainingBurnTime, totalBurnTime, cookTime, totalCookTime};
    }

    public void setFields(int[] values)
    {
        remainingBurnTime = values[0];
        totalBurnTime = values[1];
        cookTime = values[2];
        totalCookTime = values[3];
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

        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void update()
    {
        boolean wasBurning = this.isBurning();
        boolean changes = false;

        if (this.isBurning())
        {
            --this.remainingBurnTime;
        }

        if (!this.world.isRemote)
        {
            ItemStack fuel = this.inventory.getStackInSlot(1);
            ItemStack input = inventory.getStackInSlot(0);

            Choppable.ChoppingRecipe choppingRecipe = Choppable.find(input);
            if (this.isBurning() || !fuel.isEmpty() && choppingRecipe != null)
            {
                if (!this.isBurning() && this.canWork(choppingRecipe))
                {
                    this.totalBurnTime = TileEntityFurnace.getItemBurnTime(fuel);
                    this.remainingBurnTime = this.totalBurnTime;

                    if (this.isBurning())
                    {
                        changes = true;

                        if (!fuel.isEmpty())
                        {
                            Item item = fuel.getItem();
                            fuel.shrink(1);

                            if (fuel.isEmpty())
                            {
                                ItemStack containerItem = item.getContainerItem(fuel);
                                this.inventory.setStackInSlot(1, containerItem);
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canWork(choppingRecipe))
                {
                    ++this.cookTime;

                    if (this.cookTime >= this.totalCookTime)
                    {
                        this.cookTime = 0;
                        this.totalCookTime = choppingRecipe.getSawmillTime();
                        this.processItem();
                        changes = true;
                    }
                }
                else
                {
                    this.cookTime = 0;
                }
            }
            else if (!this.isBurning() && this.cookTime > 0)
            {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
            }
        }

        if (wasBurning != this.isBurning())
        {
            changes = true;
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
        }

        if (changes)
        {
            this.markDirty();
        }
    }

    private boolean canWork(@Nullable Choppable.ChoppingRecipe choppingRecipe)
    {
        return getResult(choppingRecipe).getCount() > 0;
    }

    private void processItem()
    {
        ItemStack input = inventory.getStackInSlot(0);

        if (input.isEmpty())
            return;

        Choppable.ChoppingRecipe choppingRecipe = Choppable.find(input);
        if (choppingRecipe == null)
            return;

        ItemStack result = getResult(choppingRecipe);
        if (result.getCount() <= 0)
            return;

        inventory.insertItem(2, result, false);

        input.shrink(1);
    }

    private ItemStack getResult(@Nullable Choppable.ChoppingRecipe choppingRecipe)
    {
        if (choppingRecipe == null)
            return ItemStack.EMPTY;

        ItemStack result = choppingRecipe.getResultsSawmill();

        ItemStack output = inventory.getStackInSlot(2);

        int max = Math.min(inventory.getSlotLimit(2), output.getMaxStackSize());

        int space = max - output.getCount();

        if (space < result.getCount())
            return ItemStack.EMPTY;

        return result;
    }

    public int getCookTime()
    {
        return cookTime;
    }

    public int getTotalCookTime()
    {
        return totalCookTime;
    }

    public int getRemainingBurnTime()
    {
        return remainingBurnTime;
    }

    public int getTotalBurnTime()
    {
        return totalBurnTime;
    }
}
