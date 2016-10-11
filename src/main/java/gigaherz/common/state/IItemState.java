package gigaherz.common.state;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.ItemStack;

public interface IItemState
{
    int getMetadata();

    ItemStack getStack();

    ItemStack getStack(int count);

    <T extends Comparable<T>> IItemState withProperty(IProperty<T> property, T value);

    <T extends Comparable<T>> T getValue(IProperty<T> property);

    ImmutableList<Comparable> getValues();
}
