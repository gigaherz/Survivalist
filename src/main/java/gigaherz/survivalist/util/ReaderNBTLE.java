package gigaherz.survivalist.util;

import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;

import java.io.FileInputStream;
import java.io.IOException;

public class ReaderNBTLE
{
    public static INBT parse(FileInputStream input) throws IOException
    {
        return readCompound(input).get("");
    }

    private static INBT readSpecific(FileInputStream input, byte type) throws IOException
    {
        switch(type)
        {
            case Constants.NBT.TAG_END         : return EndNBT.INSTANCE;
            case Constants.NBT.TAG_BYTE        : return ByteNBT.valueOf(readByte(input));
            case Constants.NBT.TAG_SHORT       : return ShortNBT.valueOf(readShort(input));
            case Constants.NBT.TAG_INT         : return IntNBT.valueOf(readInt(input));
            case Constants.NBT.TAG_LONG        : return LongNBT.valueOf(readLong(input));
            case Constants.NBT.TAG_FLOAT       : return FloatNBT.valueOf(Float.intBitsToFloat(readInt(input)));
            case Constants.NBT.TAG_DOUBLE      : return DoubleNBT.valueOf(Double.longBitsToDouble(readLong(input)));
            case Constants.NBT.TAG_STRING      : return StringNBT.valueOf(readString(input));
            case Constants.NBT.TAG_LIST        : return readTagList(input);
            case Constants.NBT.TAG_COMPOUND    : return readCompound(input);
            case Constants.NBT.TAG_BYTE_ARRAY  : return readByteArray(input);
            case Constants.NBT.TAG_INT_ARRAY   : return readIntArray(input);
            case Constants.NBT.TAG_LONG_ARRAY  : return readLongArray(input);
            default:
                throw new IllegalStateException("Unrecognized tag type: " + type);
        }
    }

    private static LongArrayNBT readLongArray(FileInputStream input) throws IOException
    {
        int count = readInt(input);
        long[] longs = new long[count];
        for(int i=0;i<count;i++)
            longs[i] = readInt(input);
        return new LongArrayNBT(longs);
    }

    private static IntArrayNBT readIntArray(FileInputStream input) throws IOException
    {
        int count = readInt(input);
        int[] ints = new int[count];
        for(int i=0;i<count;i++)
            ints[i] = readInt(input);
        return new IntArrayNBT(ints);
    }

    private static ByteArrayNBT readByteArray(FileInputStream input) throws IOException
    {
        int count = readInt(input);
        byte[] bytes = new byte[count];
        for(int i=0;i<count;i++)
            bytes[i] = readByte(input);
        return new ByteArrayNBT(bytes);
    }

    private static CompoundNBT readCompound(FileInputStream input) throws IOException
    {
        CompoundNBT tag = new CompoundNBT();
        while(true) {
            int type = input.read();
            if (type <= 0) break;
            String key = readString(input);
            INBT child = readSpecific(input, (byte)type);
            tag.put(key, child);
        }
        return tag;
    }

    private static ListNBT readTagList(FileInputStream input) throws IOException
    {
        byte type = readByte(input);
        int count = readInt(input);
        ListNBT list = new ListNBT();
        for(int i=0;i<count;i++)
        {
            list.add(readSpecific(input, type));
        }
        return list;
    }

    private static String readString(FileInputStream input) throws IOException
    {
        int count = readShort(input);
        char[] chars = new char[count];
        for(int i=0;i<count;i++)
        {
            char c = (char)input.read();
            if (c >= 128)
                throw new UnsupportedOperationException("UTF codes > 127 not supported yet");
            chars[i] = c;
        }
        return new String(chars);
    }

    private static long readLong(FileInputStream input) throws IOException
    {
        return readInt(input) | ((long)readInt(input) << 32);
    }

    private static int readInt(FileInputStream input) throws IOException
    {
        return input.read() | (input.read() << 8)
                | (input.read() << 16) | (input.read() << 24);
    }

    private static short readShort(FileInputStream input) throws IOException
    {
        return (short)(input.read() | (input.read() << 8));
    }

    private static byte readByte(FileInputStream input) throws IOException
    {
        return (byte)input.read();
    }
}
