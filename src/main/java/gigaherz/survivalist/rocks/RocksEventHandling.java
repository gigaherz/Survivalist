package gigaherz.survivalist.rocks;

import com.google.common.collect.Lists;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
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
            List<ItemStack> newDrops = Lists.newArrayList();

            for (ItemStack drop : ev.getDrops())
            {
                if (drop.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE) && ConfigManager.instance.replaceStoneDrops)
                {
                    newDrops.add(new ItemStack(Survivalist.rock, 4));
                }
                else if (drop.getItem() == Item.getItemFromBlock(Blocks.STONE) && ConfigManager.instance.replaceStoneDrops)
                {
                    switch (drop.getMetadata())
                    {
                        case 0:
                            newDrops.add(new ItemStack(Survivalist.rock, 4, 0));
                            break;
                        case 5:
                            newDrops.add(new ItemStack(Survivalist.rock, 4, 1));
                            break;
                        case 3:
                            newDrops.add(new ItemStack(Survivalist.rock, 4, 2));
                            break;
                        case 1:
                            newDrops.add(new ItemStack(Survivalist.rock, 4, 3));
                            break;
                        default:
                            newDrops.add(drop);
                    }
                }
                else if (drop.getItem() == Item.getItemFromBlock(Blocks.IRON_ORE) && ConfigManager.instance.replaceIronOreDrops)
                {
                    newDrops.add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 0));
                }
                else if (drop.getItem() == Item.getItemFromBlock(Blocks.GOLD_ORE) && ConfigManager.instance.replaceGoldOreDrops)
                {
                    newDrops.add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 1));
                }
                else if (ConfigManager.instance.replaceModOreDrops)
                {
                    if (Survivalist.hasOreName(drop, "oreCopper"))
                    {
                        newDrops.add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 2));
                    }
                    else if (Survivalist.hasOreName(drop, "oreTin"))
                    {
                        newDrops.add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 3));
                    }
                    else if (Survivalist.hasOreName(drop, "oreLead"))
                    {
                        newDrops.add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 4));
                    }
                    else if (Survivalist.hasOreName(drop, "oreSilver"))
                    {
                        newDrops.add(new ItemStack(Survivalist.rock_ore, 2 + Math.round(2 * rnd.nextFloat()), 5));
                    }
                    else
                    {
                        newDrops.add(drop);
                    }
                }
                else
                {
                    newDrops.add(drop);
                }
            }

            ev.getDrops().clear();
            ev.getDrops().addAll(newDrops);
        }
    }
}
