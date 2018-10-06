package gigaherz.survivalist.rocks;

import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.state.IItemState;
import gigaherz.survivalist.state.IItemStateManager;
import gigaherz.survivalist.state.ItemStateful;
import gigaherz.survivalist.state.implementation.ItemStateManager;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemOreRock extends ItemStateful
{
    public static final PropertyEnum<OreMaterial> ORE = PropertyEnum.create("ore", OreMaterial.class);

    public ItemOreRock()
    {
        super();
        setHasSubtypes(true);
        setTranslationKey(Survivalist.MODID + ".rock");
        setCreativeTab(CreativeTabs.MATERIALS);
    }

    @Override
    public IItemStateManager createStateManager()
    {
        return new ItemStateManager(this, ORE);
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        IItemState state = getStateManager().get(stack.getMetadata());

        if (state == null)
            return getTranslationKey();

        String subName = state.getValue(ORE).getUnlocalizedOreSuffix();

        return "item." + Survivalist.MODID + subName;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (isInCreativeTab(tab))
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
