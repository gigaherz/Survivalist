package gigaherz.survivalist.rack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
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

        for(int j = 0;j<prevRemaining.length;j++)
        {
            if (prevRemaining[j] != tile.dryTimeRemaining[j])
            {
                for (ICrafting icrafting : this.crafters)
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
        if(id < this.tile.dryTimeRemaining.length)
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
        ItemStack stackCopy = stack.copy();

        int startIndex;
        int endIndex;

        if (slotIndex < 4)
        {
            startIndex = 4;
            endIndex = startIndex + 4 * 9;
        }
        else
        {
            startIndex = 0;
            endIndex = startIndex + 4;
        }

        if (!this.mergeItemStack(stack, startIndex, endIndex, false))
        {
            return null;
        }

        if (stack.stackSize == 0)
        {
            slot.putStack(null);
        }
        else
        {
            slot.onSlotChanged();
        }

        if (stack.stackSize == stackCopy.stackSize)
        {
            return null;
        }

        slot.onPickupFromSlot(player, stack);
        return stackCopy;
    }
}
