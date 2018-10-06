package gigaherz.survivalist.state.implementation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import gigaherz.survivalist.state.IItemState;
import gigaherz.survivalist.state.IItemStateManager;
import gigaherz.survivalist.state.StatefulItem;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;

public class ItemStateManager implements IItemStateManager
{
    private final Item item;
    private final ItemState[] stateTable;
    private final IProperty[] properties;
    private final Comparable[][] propertyValues;

    private IItemState defaultState;

    public <T extends Item & StatefulItem> ItemStateManager(T item, IProperty... properties)
    {
        this.item = item;
        this.properties = properties;
        this.propertyValues = new Comparable[properties.length][];

        Arrays.sort(properties);

        int combinations = 1;
        for (int i = 0; i < properties.length; i++)
        {
            IProperty prop = properties[i];
            @SuppressWarnings("unchecked")
            Collection<Comparable> allowed = prop.getAllowedValues();
            Comparable[] values = allowed.toArray(new Comparable[allowed.size()]);
            propertyValues[i] = values;
            combinations *= values.length;
        }

        stateTable = new ItemState[combinations];

        Deque<Comparable> values = new ArrayDeque<>();
        int lastState = enumStates(properties, 0, 0, values);
        assert lastState == combinations;

        defaultState = stateTable[0];
    }

    private int enumStates(IProperty[] properties, int p, int state, Deque<Comparable> values)
    {
        Comparable[] propValues = propertyValues[p];
        for (Object o : propValues)
        {
            values.push((Comparable) o);

            if (p + 1 >= properties.length)
            {
                stateTable[state] = new ItemState(state, values.toArray(new Comparable[values.size()]));
                state++;
            }
            else
            {
                state = enumStates(properties, p + 1, state, values);
            }

            values.pop();
        }
        return state;
    }

    @Override
    @Nullable
    public IItemState get(int metadata)
    {
        if (metadata < 0 || metadata >= stateTable.length)
            return null;
        return stateTable[metadata];
    }

    @Override
    public Item getItem()
    {
        return item;
    }

    @Override
    public ImmutableList<IProperty> getProperties()
    {
        return ImmutableList.copyOf(properties);
    }

    @Override
    public IItemState getDefaultState()
    {
        return defaultState;
    }

    @Override
    public void setDefaultState(IItemState defaultState)
    {
        this.defaultState = defaultState;
    }

    @Override
    public ImmutableList<IItemState> getStateTable()
    {
        return ImmutableList.copyOf(stateTable);
    }

    public class ItemState implements IItemState
    {
        private final int metadata;
        private Comparable[] values;

        ItemState(int metadata, Comparable[] values)
        {
            this.metadata = metadata;
            this.values = values;
        }

        @Override
        public int getMetadata()
        {
            return metadata;
        }

        @Override
        public ItemStack getStack()
        {
            return getStack(1);
        }

        @Override
        public ItemStack getStack(int count)
        {
            return new ItemStack(item, count, metadata);
        }

        @Override
        public <T extends Comparable<T>> IItemState withProperty(IProperty<T> property, T value)
        {
            int lastSize = 1;
            int meta = 0;
            for (int i = 0; i < properties.length; i++)
            {
                Comparable[] values = propertyValues[i];
                int idx;
                if (properties[i] == property)
                    idx = ArrayUtils.indexOf(values, value);
                else
                    idx = ArrayUtils.indexOf(values, this.values[i]);
                meta = meta * lastSize + idx;
                lastSize = values.length;
            }
            return stateTable[meta];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Comparable<T>> T getValue(IProperty<T> property)
        {
            int i = ArrayUtils.indexOf(properties, property);

            return (T) values[i];
        }

        @Override
        public ImmutableList<Comparable> getValues()
        {
            return ImmutableList.copyOf(values);
        }

        @Override
        public ImmutableMap<IProperty<?>, Comparable<?>> getProperties()
        {
            ImmutableMap.Builder<IProperty<?>, Comparable<?>> b = new ImmutableMap.Builder<>();

            for (int i = 0; i < properties.length; i++)
            {
                IProperty prop = properties[i];
                Comparable value = values[i];
                b.put(prop, value);
            }

            return b.build();
        }

        @SuppressWarnings("unchecked")
        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < properties.length; i++)
            {
                if (i != 0)
                    sb.append(",");
                sb.append(properties[i].getName());
                sb.append("=");
                sb.append(properties[i].getName(values[i]));
            }
            return sb.toString();
        }
    }
}
