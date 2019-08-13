package gigaherz.survivalist.scraping;

import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.client.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MessageScraping
{
    public final ItemStack stack;
    public final ItemStack ret;

    public MessageScraping(ItemStack stack, ItemStack ret)
    {
        this.stack = stack;
        this.ret = ret;
    }

    public MessageScraping(PacketBuffer buf)
    {
        stack = buf.readItemStack();
        ret = buf.readItemStack();
    }

    public void encode(PacketBuffer buf)
    {
        buf.writeItemStack(stack);
        buf.writeItemStack(ret);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ClientEvents.handleScrapingMessage(this);
    }
}
