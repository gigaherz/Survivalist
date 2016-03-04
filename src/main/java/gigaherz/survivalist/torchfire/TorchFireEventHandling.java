package gigaherz.survivalist.torchfire;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
        if (!ev.target.isImmuneToFire() && !ev.target.worldObj.isRemote)
        {
            ItemStack stack = ev.entityPlayer.getHeldItem();
            if(stack.getItem() instanceof ItemBlock)
            {
                ItemBlock b = (ItemBlock)stack.getItem();
                Block bl = b.getBlock();
                if(bl == Blocks.torch)
                {
                    ev.target.setFire(2);
                    if(rnd.nextFloat() > 0.25)
                    {
                        stack.stackSize--;
                        if(stack.stackSize <= 0)
                        {
                            ev.entityPlayer.inventory.setInventorySlotContents(ev.entityPlayer.inventory.currentItem, null);
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
