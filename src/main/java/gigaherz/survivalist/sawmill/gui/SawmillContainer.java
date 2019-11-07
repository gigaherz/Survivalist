package gigaherz.survivalist.sawmill.gui;

import gigaherz.survivalist.api.ChoppingRecipe;
import gigaherz.survivalist.sawmill.SawmillTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

public class SawmillContainer extends Container
{
    @ObjectHolder("survivalist:sawmill")
    public static ContainerType<SawmillContainer> TYPE;

    private IIntArray fields;

    public SawmillContainer(int windowId, PlayerInventory playerInventory)
    {
        this(windowId, playerInventory, new ItemStackHandler(4), new IntArray(4));
    }

    public SawmillContainer(int windowId, SawmillTileEntity tileEntity, PlayerInventory playerInventory)
    {
        this(windowId, playerInventory, tileEntity.inventory, tileEntity);
    }

    public SawmillContainer(int windowId, PlayerInventory playerInventory, IItemHandler inventory, IIntArray dryTimes)
    {
        super(TYPE, windowId);

        fields = dryTimes;

        addSlot(new SlotItemHandler(inventory, 0, 56, 17));
        addSlot(new SawmillFuelSlot(inventory, 1, 56, 53));
        addSlot(new SawmillOutputSlot(inventory, 2, 116, 35));

        bindPlayerInventory(playerInventory);

        trackIntArray(fields);
    }

    private void bindPlayerInventory(PlayerInventory playerInventory)
    {
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return true;
    }

    public int getRemainingBurnTime()
    {
        return fields.get(0);
    }

    public int getTotalBurnTime()
    {
        return fields.get(1);
    }

    public int getCookTime()
    {
        return fields.get(2);
    }

    public int getTotalCookTime()
    {
        return fields.get(3);
    }

    public boolean isBurning()
    {
        return getRemainingBurnTime() > 0;
    }


    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
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
                if (ChoppingRecipe.getRecipe(playerIn.world, itemstack1)
                        .isPresent())
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (AbstractFurnaceTileEntity.isFuel(itemstack1))
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
