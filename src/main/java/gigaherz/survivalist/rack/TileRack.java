package gigaherz.survivalist.rack;

import gigaherz.survivalist.Dryable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileRack extends TileEntity implements ITickable
{
    public int[] dryTimeRemaining = new int[4];

    public ItemStackHandler items = new ItemStackHandler(4)
    {
        @Override
        protected int getStackLimit(int slot, ItemStack stack)
        {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            TileRack.this.markDirty();
        }
    };

    @Override
    public void update()
    {
        if (worldObj.isRemote)
            return;

        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = items.getStackInSlot(i);
            int dryTime = Dryable.getDryingTime(stack);
            if (dryTime >= 0)
            {
                if (dryTimeRemaining[i] <= 0)
                {
                    dryTimeRemaining[i] = dryTime;
                }
                else
                {
                    dryTimeRemaining[i]--;
                    if (dryTimeRemaining[i] <= 0)
                    {
                        stack = Dryable.getDryingResult(stack);
                        items.setStackInSlot(i, stack);
                    }
                }
            }
            else
            {
                dryTimeRemaining[i] = 0;
            }
        }
    }

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
            return (T) items;
        return super.getCapability(capability, facing);
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(pos) == this
                && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("Items", items.serializeNBT());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        items.deserializeNBT(compound.getCompoundTag("Items"));
    }
}
