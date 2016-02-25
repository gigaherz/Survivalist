package gigaherz.survivalist;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class EventHandling
{
    Random rnd = new Random();

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new EventHandling());
    }

    @SubscribeEvent
    public void onHarvestBlock(BlockEvent.HarvestDropsEvent ev)
    {
        if (!ev.isSilkTouching)
        {
            Block block = ev.state.getBlock();
            if(block == Blocks.cobblestone || (block == Blocks.stone && ev.state.getValue(BlockStone.VARIANT) == BlockStone.EnumType.STONE))
            {
                ev.drops.clear();
                ev.drops.add(new ItemStack(Survivalist.rock, 4));
            }
            else if(block == Blocks.iron_ore)
            {
                ev.drops.clear();
                ev.drops.add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 0));
            }
            else if(block == Blocks.gold_ore)
            {
                ev.drops.clear();
                ev.drops.add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 1));
            }
        }
    }
}
