package gigaherz.survivalist.network;

import gigaherz.survivalist.Survivalist;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateFields
        implements IMessage
{
    public int windowId;
    public int[] fields;

    public UpdateFields()
    {
    }

    public UpdateFields(int windowId, int[] values)
    {
        this.windowId = windowId;
        this.fields = values;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        windowId = buf.readInt();
        fields = new int[buf.readByte()];
        for (int i = 0; i < fields.length; i++)
        {
            fields[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(windowId);
        buf.writeByte(fields.length);
        for (int i = 0; i < fields.length; i++)
        {
            buf.writeInt(fields[i]);
        }
    }

    public static class Handler implements IMessageHandler<UpdateFields, IMessage>
    {
        @Override
        public IMessage onMessage(UpdateFields message, MessageContext ctx)
        {
            Survivalist.proxy.handleUpdateField(message);

            return null; // no response in this case
        }
    }
}
