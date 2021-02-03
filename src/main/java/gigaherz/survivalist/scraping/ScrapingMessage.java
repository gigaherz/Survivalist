package gigaherz.survivalist.scraping;

import gigaherz.survivalist.client.ClientEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ScrapingMessage
{
    public final ItemStack stack;
    public final ItemStack ret;

    public ScrapingMessage(ItemStack stack, ItemStack ret)
    {
        this.stack = stack.copy();
        this.ret = ret.copy();
    }

    public ScrapingMessage(PacketBuffer buf)
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