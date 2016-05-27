package gigaherz.survivalist.scraping;

import gigaherz.survivalist.Survivalist;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageScraping
        implements IMessage
{
    private ItemStack stack;
    private ItemStack ret;

    public MessageScraping()
    {
    }

    public MessageScraping(ItemStack stack, ItemStack ret)
    {
        this.stack = stack;
        this.ret = ret;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        stack = ByteBufUtils.readItemStack(buf);
        ret = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeItemStack(buf, stack);
        ByteBufUtils.writeItemStack(buf, ret);
    }

    public static class Handler implements IMessageHandler<MessageScraping, IMessage>
    {
        @Nullable
        @Override
        public IMessage onMessage(MessageScraping message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new TextComponentTranslation("text." + Survivalist.MODID + ".scraping.message1",
                                message.stack.getTextComponent(),
                                new TextComponentString("" + message.ret.stackSize),
                                message.ret.getTextComponent())));

            return null; // no response in this case
        }
    }
}
