package gigaherz.common.state;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IItemStateManager
{
    @Nullable
    IItemState get(int metadata);

    Item getItem();

    ImmutableList<IProperty> getProperties();

    IItemState getDefaultState();

    void setDefaultState(IItemState defaultState);

    ImmutableList<IItemState> getStateTable();

    @Nullable
    static IItemState lookup(ItemStack stack)
    {
        Item item = stack.getItem();
        if (!(item instanceof ItemStateful))
            throw new IllegalArgumentException("The stack represents a non-stateful item");
        IItemStateManager stateData = ((ItemStateful) item).getStateManager();
        return stateData.get(stack.getMetadata());
    }
}
