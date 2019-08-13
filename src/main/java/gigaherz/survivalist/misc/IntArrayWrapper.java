package gigaherz.survivalist.misc;

import net.minecraft.util.IIntArray;

public class IntArrayWrapper implements IIntArray
{
    private final int[] array;

    public IntArrayWrapper(int[] values)
    {
        this.array = values;
    }

    @Override
    public int get(int index)
    {
        return array[index];
    }

    @Override
    public void set(int index, int value)
    {
        array[index] = value;
    }

    @Override
    public int size()
    {
        return 4;
    }
}
