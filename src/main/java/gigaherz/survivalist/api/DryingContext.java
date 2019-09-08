package gigaherz.survivalist.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class DryingContext implements IInventory
{
    final IItemHandlerModifiable inner;

    public DryingContext(IItemHandlerModifiable inner)
    {
        this.inner = inner;
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
}
