package gigaherz.survivalist.rack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerRack extends Container
{
    protected TileRack tile;
    private int[] prevRemaining;

    public ContainerRack(TileRack tileEntity, InventoryPlayer playerInventory)
    {
        this.tile = tileEntity;
        prevRemaining = new int[tile.dryTimeRemaining.length];

        addSlotToContainer(new SlotItemHandler(tile.items, 0, 26, 34));
        addSlotToContainer(new SlotItemHandler(tile.items, 1, 62, 34));
        addSlotToContainer(new SlotItemHandler(tile.items, 2, 98, 34));
        addSlotToContainer(new SlotItemHandler(tile.items, 3, 134, 34));

        bindPlayerInventory(playerInventory);
    }

    protected void bindPlayerInventory(InventoryPlayer playerInventory)
    {
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(playerInventory,
                        x + y * 9 + 9,
                        8 + x * 18, 84 + y * 18));
            }
        }

        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int j = 0; j < prevRemaining.length; j++)
        {
            if (prevRemaining[j] != tile.dryTimeRemaining[j])
            {
                for (IContainerListener icrafting : this.listeners)
                {
                    icrafting.sendProgressBarUpdate(this, j, tile.dryTimeRemaining[j]);
                }
                prevRemaining[j] = tile.dryTimeRemaining[j];
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        if (id < this.tile.dryTimeRemaining.length)
            this.tile.dryTimeRemaining[id] = data;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        Slot slot = this.inventorySlots.get(slotIndex);
        if (slot == null || !slot.getHasStack())
        {
            return null;
        }

        ItemStack stack = slot.getStack();
        assert stack != null;
        ItemStack stackCopy = stack.copy();

        int startIndex;
        int endIndex;

        if (slotIndex < 4)
        {
            startIndex = 4;
            endIndex = startIndex + 4 * 9;

            if (!mergeItemStack(stack, startIndex, endIndex, false))
            {
                return null;
            }
        }
        else
        {
            startIndex = 0;
            endIndex = startIndex + 4;

            if (!mergeItemStack(stack, startIndex, endIndex))
            {
                return null;
            }
        }

        if (stack.func_190916_E() == 0)
        {
            slot.putStack(null);
        }
        else
        {
            slot.onSlotChanged();
        }

        if (stack.func_190916_E() == stackCopy.func_190916_E())
        {
            return null;
        }

        slot.func_190901_a(player, stack);
        return stackCopy;
    }

    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex)
    {
        boolean transferred = false;
        for (int i = startIndex; i < endIndex; i++)
        {
            Slot slot = this.inventorySlots.get(i);
            ItemStack existing = slot.getStack();

            if (existing.func_190916_E() <= 0 && slot.isItemValid(stack))
            {
                slot.putStack(copyWithSize(stack, 1));
                slot.onSlotChanged();
                stack.func_190917_f(-1);
                transferred = true;
                if (stack.func_190916_E() <= 0)
                    break;
            }
        }
        return transferred;
    }

    private ItemStack copyWithSize(ItemStack stack, int count)
    {
        ItemStack stack1 = stack.copy();
        stack1.func_190920_e(count);
        return stack1;
    }
}
