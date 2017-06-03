package gigaherz.survivalist.rocks;

import gigaherz.common.state.IItemState;
import gigaherz.common.state.IItemStateManager;
import gigaherz.common.state.ItemStateful;
import gigaherz.common.state.implementation.ItemStateManager;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemOreRock extends ItemStateful
{
    public static final PropertyEnum<OreMaterial> ORE = PropertyEnum.create("ore", OreMaterial.class);

    public ItemOreRock(String name)
    {
        super(name);
        setHasSubtypes(true);
        setUnlocalizedName(Survivalist.MODID + ".rock");
        setCreativeTab(CreativeTabs.MATERIALS);
    }

    @Override
    public IItemStateManager createStateManager()
    {
        return new ItemStateManager(this, ORE);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        IItemState state = getStateManager().get(stack.getMetadata());

        if (state == null)
            return getUnlocalizedName();

        String subName = state.getValue(ORE).getUnlocalizedOreSuffix();

        return "item." + Survivalist.MODID + subName;
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        if (ConfigManager.instance.enableRocks)
        {
            for (OreMaterial type : ORE.getAllowedValues())
            {
                IItemState state = getDefaultState().withProperty(ORE, type);
                subItems.add(state.getStack());
            }
        }
    }

    public ItemStack getStack(OreMaterial material)
    {
        return getDefaultState().withProperty(ORE, material).getStack();
    }
}
