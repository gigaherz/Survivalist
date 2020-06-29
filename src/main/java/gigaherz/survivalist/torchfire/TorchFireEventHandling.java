package gigaherz.survivalist.torchfire;

import gigaherz.survivalist.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class TorchFireEventHandling
{
    private final Random rnd = new Random();

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent ev)
    {
        if (!ConfigManager.SERVER.enableTorchFire.get())
            return;

        if (!ev.getTarget().func_230279_az_() && !ev.getTarget().world.isRemote)
        {
            PlayerEntity player = ev.getPlayer();
            ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
            if (stack.getCount() > 0 && stack.getItem() instanceof BlockItem)
            {
                BlockItem b = (BlockItem) stack.getItem();
                Block bl = b.getBlock();
                if (bl == Blocks.TORCH)
                {
                    ev.getTarget().setFire(2);
                    if (!ev.getPlayer().isCreative() && rnd.nextFloat() > 0.25)
                    {
                        stack.grow(-1);
                        if (stack.getCount() <= 0)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
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
