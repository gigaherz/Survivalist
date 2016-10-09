package gigaherz.survivalist.api.state;

import net.minecraft.block.properties.IProperty;

public interface IItemState
{
    int getMetadata();

    <T extends Comparable<T>> IItemState withProperty(IProperty<T> property, T value);
    <T extends Comparable<T>> T getValue(IProperty<T> property);
}
