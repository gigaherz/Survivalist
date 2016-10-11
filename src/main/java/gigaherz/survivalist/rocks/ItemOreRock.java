package gigaherz.survivalist.rocks;

import gigaherz.common.state.IItemState;
import gigaherz.common.state.IItemStateManager;
import gigaherz.common.state.ItemStateful;
import gigaherz.common.state.implementation.ItemStateManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.List;

public class ItemOreRock extends ItemStateful
{
    public static final PropertyEnum<Subtype> ORE = PropertyEnum.create("ore", Subtype.class);

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

        String subName = state.getValue(ORE).getUnlocalizedSuffix();

        return "item." + Survivalist.MODID + subName;
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (Subtype type : ORE.getAllowedValues())
        {
            IItemState state = getDefaultState().withProperty(ORE, type);
            subItems.add(state.getStack());
        }
    }

    public ItemStack getStack(Subtype iron)
    {
        return getDefaultState().withProperty(ORE, iron).getStack();
    }

    public enum Subtype implements IStringSerializable
    {
        IRON("iron", ".iron_rock"),
        GOLD("gold", ".gold_rock"),
        COPPER("copper", ".copper_rock"),
        TIN("tin", ".tin_rock"),
        LEAD("lead", ".lead_rock"),
        SILVER("silver", ".silver_rock");

        final String name;
        final String unlocalizedSuffix;

        Subtype(String name, String unlocalizedSuffix)
        {
            this.name = name;
            this.unlocalizedSuffix = unlocalizedSuffix;
        }

        @Override
        public String getName()
        {
            return name;
        }

        public String getUnlocalizedSuffix()
        {
            return unlocalizedSuffix;
        }
    }
}
