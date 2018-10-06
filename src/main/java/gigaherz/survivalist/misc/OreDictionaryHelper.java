package gigaherz.survivalist.misc;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryHelper
{
    public static boolean hasOreName(ItemStack stack, String oreName)
    {
        if (stack.isEmpty())
            return false;

        if (stack.getItem() == null)
        {
            return false;
        }

        int id = OreDictionary.getOreID(oreName);
        for (int i : OreDictionary.getOreIDs(stack))
        {
            if (i == id) return true;
        }
        return false;
    }
}
