package gigaherz.survivalist.rocks;

import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class RocksEventHandling
{
    Random rnd = new Random();

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new RocksEventHandling());
    }

    @SubscribeEvent
    public void onHarvestBlock(BlockEvent.HarvestDropsEvent ev)
    {
        if (!ev.isSilkTouching())
        {
            Block block = ev.getState().getBlock();
            if (block == Blocks.COBBLESTONE && ConfigManager.instance.replaceStoneDrops)
            {
                ev.getDrops().clear();
                ev.getDrops().add(new ItemStack(Survivalist.rock, 4));
            }
            else if (block == Blocks.STONE && ConfigManager.instance.replaceStoneDrops)
            {
                switch (ev.getState().getValue(BlockStone.VARIANT))
                {
                    case STONE:
                        ev.getDrops().clear();
                        ev.getDrops().add(new ItemStack(Survivalist.rock, 4, 0));
                        break;
                    case ANDESITE:
                        ev.getDrops().clear();
                        ev.getDrops().add(new ItemStack(Survivalist.rock, 4, 1));
                        break;
                    case DIORITE:
                        ev.getDrops().clear();
                        ev.getDrops().add(new ItemStack(Survivalist.rock, 4, 2));
                        break;
                    case GRANITE:
                        ev.getDrops().clear();
                        ev.getDrops().add(new ItemStack(Survivalist.rock, 4, 3));
                        break;
                }
            }
            else if (block == Blocks.IRON_ORE && ConfigManager.instance.replaceIronOreDrops)
            {
                ev.getDrops().clear();
                ev.getDrops().add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 0));
            }
            else if (block == Blocks.GOLD_ORE && ConfigManager.instance.replaceGoldOreDrops)
            {
                ev.getDrops().clear();
                ev.getDrops().add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 1));
            }
        }
    }
}
