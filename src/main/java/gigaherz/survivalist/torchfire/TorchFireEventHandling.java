package gigaherz.survivalist.torchfire;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class TorchFireEventHandling
{
    Random rnd = new Random();

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent ev)
    {
        if (!ev.getTarget().isImmuneToFire() && !ev.getTarget().world.isRemote)
        {
            ItemStack stack = ev.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
            if (stack.getCount() > 0 && stack.getItem() instanceof ItemBlock)
            {
                ItemBlock b = (ItemBlock) stack.getItem();
                Block bl = b.getBlock();
                if (bl == Blocks.TORCH)
                {
                    ev.getTarget().setFire(2);
                    if (rnd.nextFloat() > 0.25)
                    {
                        stack.grow(-1);
                        if (stack.getCount() <= 0)
                        {
                            ev.getEntityPlayer().inventory.setInventorySlotContents(ev.getEntityPlayer().inventory.currentItem, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
    }

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new TorchFireEventHandling());
    }
}
