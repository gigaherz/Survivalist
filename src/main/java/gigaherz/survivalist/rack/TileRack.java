package gigaherz.survivalist.rack;

import gigaherz.survivalist.api.Dryable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Arrays;

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

            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    };

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 0, tag);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        super.onDataPacket(net, packet);
        readFromNBT(packet.getNbtCompound());

        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public void update()
    {
        if (world.isRemote)
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
            return (T) items;
        return super.getCapability(capability, facing);
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return world.getTileEntity(pos) == this
                && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setTag("Items", items.serializeNBT());
        compound.setIntArray("RemainingTime", dryTimeRemaining);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        items.deserializeNBT(compound.getCompoundTag("Items"));
        int[] remaining = compound.getIntArray("RemainingTime");

        dryTimeRemaining = Arrays.copyOf(remaining, 4);
    }

    public IItemHandler inventory()
    {
        return items;
    }

    public ItemStack[] getItems()
    {
        return new ItemStack[] {
                items.getStackInSlot(0),
                items.getStackInSlot(1),
                items.getStackInSlot(2),
                items.getStackInSlot(3)
        };
    }
}
