package gigaherz.survivalist;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Dryable
{
    public static int getDryingTime(ItemStack stack)
    {
        if(stack==null)
            return 0;
        Item item = stack.getItem();
        if (item == Items.leather)
            return 30 * 20;
        if(item == Items.rotten_flesh
                || item == Items.beef
                || item == Items.mutton
                || item == Items.porkchop
                || item == Items.cooked_beef
                || item == Items.cooked_mutton
                || item == Items.cooked_porkchop)
            return 15 * 20;
        return 0;
    }

    public static ItemStack getDryingResult(ItemStack stack)
    {
        if(stack==null)
            return null;
        Item item = stack.getItem();
        if (item == Items.leather)
            return new ItemStack(Survivalist.tanned_leather);
        if (item == Items.beef
                || item == Items.mutton
                || item == Items.porkchop)
            return new ItemStack(Items.rotten_flesh);
        if (item == Items.rotten_flesh
                || item == Items.cooked_beef
                || item == Items.cooked_mutton
                || item == Items.cooked_porkchop)
            return new ItemStack(Survivalist.jerky);
        return null;
    }
}
