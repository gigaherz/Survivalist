package gigaherz.survivalist.sawmill.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotItemHandlerFuel extends SlotItemHandler
{
    public SlotItemHandlerFuel(IItemHandler inventory, int index, int xPosition, int yPosition)
    {
        super(inventory, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return AbstractFurnaceTileEntity.isFuel(stack);
    }

    public int getItemStackLimit(ItemStack stack)
    {
        return isBucket(stack) ? 1 : super.getItemStackLimit(stack);
    }

    public static boolean isBucket(ItemStack stack)
    {
        return stack.getItem() == Items.BUCKET;
    }
}
