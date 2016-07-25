package gigaherz.survivalist.rocks;

import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.base.ItemRegistered;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemNugget extends ItemRegistered
{
    static final String[] subNames = {
            ".iron_nugget", ".copper_nugget", ".tin_nugget", ".lead_nugget", ".silver_nugget"
    };

    public ItemNugget(String name)
    {
        super(name);
        setHasSubtypes(true);
        setUnlocalizedName(Survivalist.MODID + ".nugget");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = stack.getMetadata();

        if (meta > subNames.length)
            return getUnlocalizedName();

        return "item." + Survivalist.MODID + subNames[meta];
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (int i = 0; i < subNames.length; i++)
        {
            subItems.add(new ItemStack(this, 1, i));
        }
    }
}
