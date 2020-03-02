package gigaherz.survivalist.rack;

import gigaherz.survivalist.misc.IntArrayWrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class DryingRackContainer extends Container
{
    @ObjectHolder("survivalist:rack")
    public static ContainerType<DryingRackContainer> TYPE = null;

    public final IIntArray dryTimeRemainingArray;

    public DryingRackContainer(int windowId, PlayerInventory playerInventory)
    {
        this(windowId, playerInventory, new ItemStackHandler(4), new IntArray(4));
    }

    public DryingRackContainer(int windowId, DryingRackTileEntity dryingRackTileEntity, PlayerInventory playerInventory)
    {
        this(windowId, playerInventory, dryingRackTileEntity.items, dryingRackTileEntity.dryTimeArray);
    }

    private DryingRackContainer(int windowId, PlayerInventory playerInventory, IItemHandler inventory, IIntArray dryTimes)
    {
        super(TYPE, windowId);

        dryTimeRemainingArray = dryTimes;

        addSlot(new SlotItemHandler(inventory, 0, 26, 34));
        addSlot(new SlotItemHandler(inventory, 1, 62, 34));
        addSlot(new SlotItemHandler(inventory, 2, 98, 34));
        addSlot(new SlotItemHandler(inventory, 3, 134, 34));

        bindPlayerInventory(playerInventory);

        this.trackIntArray(dryTimeRemainingArray);
    }

    protected void bindPlayerInventory(PlayerInventory playerInventory)
    {
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlot(new Slot(playerInventory,
                        x + y * 9 + 9,
                        8 + x * 18, 84 + y * 18));
            }
        }

        for (int x = 0; x < 9; x++)
        {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    /*@Override
    public void updateProgressBar(int id, int data)
    {
        if (id < this.tile.dryTimeRemaining.length)
            this.tile.dryTimeRemaining[id] = data;
    }*/

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return true; // fixme
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex)
    {
        Slot slot = this.inventorySlots.get(slotIndex);
        if (slot == null || !slot.getHasStack())
        {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getStack();
        ItemStack stackCopy = stack.copy();

        int startIndex;
        int endIndex;

        if (slotIndex < 4)
        {
            startIndex = 4;
            endIndex = startIndex + 4 * 9;

            if (!mergeItemStack(stack, startIndex, endIndex, false))
            {
                return ItemStack.EMPTY;
            }
        }
        else
        {
            startIndex = 0;
            endIndex = startIndex + 4;

            if (!mergeItemStack(stack, startIndex, endIndex))
            {
                return ItemStack.EMPTY;
            }
        }

        if (stack.getCount() == 0)
        {
            slot.putStack(ItemStack.EMPTY);
        }
        else
        {
            slot.onSlotChanged();
        }

        if (stack.getCount() == stackCopy.getCount())
        {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return stackCopy;
    }

    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex)
    {
        boolean transferred = false;
        for (int i = startIndex; i < endIndex; i++)
        {
            Slot slot = this.inventorySlots.get(i);
            ItemStack existing = slot.getStack();

            if (existing.getCount() <= 0 && slot.isItemValid(stack))
            {
                slot.putStack(copyWithSize(stack, 1));
                slot.onSlotChanged();
                stack.grow(-1);
                transferred = true;
                if (stack.getCount() <= 0)
                    break;
            }
        }
        return transferred;
    }

    private ItemStack copyWithSize(ItemStack stack, int count)
    {
        ItemStack stack1 = stack.copy();
        stack1.setCount(count);
        return stack1;
    }
}
