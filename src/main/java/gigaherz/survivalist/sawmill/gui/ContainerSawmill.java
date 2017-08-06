package gigaherz.survivalist.sawmill.gui;

import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.network.UpdateFields;
import gigaherz.survivalist.sawmill.TileSawmill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerSawmill
        extends Container
{
    private TileSawmill tile;
    private int[] prevFields;

    public ContainerSawmill(TileSawmill tileEntity, InventoryPlayer playerInventory)
    {
        this.tile = tileEntity;
        prevFields = this.tile.getFields();
        for (int i = 0; i < prevFields.length; i++) { prevFields[i]--; }

        IItemHandler inv = tileEntity.getInventory();

        addSlotToContainer(new SlotItemHandler(inv, 0, 56, 17));
        addSlotToContainer(new SlotItemHandlerFuel(inv, 1, 56, 53));
        addSlotToContainer(new SlotItemHandlerOutput(inv, 2, 116, 35));

        bindPlayerInventory(playerInventory);
    }

    private void bindPlayerInventory(InventoryPlayer playerInventory)
    {
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        boolean needUpdate = false;

        int[] fields = this.tile.getFields();
        for (int i = 0; i < prevFields.length; i++)
        {
            if (prevFields[i] != fields[i])
            {
                prevFields[i] = fields[i];
                needUpdate = true;
            }
        }

        if (needUpdate)
        {
            this.listeners.stream().filter(watcher -> watcher instanceof EntityPlayerMP).forEach(watcher ->
                    Survivalist.channel.sendTo(new UpdateFields(this.windowId, prevFields), (EntityPlayerMP) watcher));
        }
    }

    public void updateFields(int[] data)
    {
        this.tile.setFields(data);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 2)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index != 1 && index != 0)
            {
                if (Choppable.isValidInput(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
