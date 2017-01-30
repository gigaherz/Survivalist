package gigaherz.survivalist.misc;

import com.google.common.collect.ImmutableList;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Random;

import static net.minecraft.block.BlockTallGrass.EnumType.DEAD_BUSH;

public class FibersEventHandling
{
    private final Random rnd = new Random();

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new FibersEventHandling());
    }

    @SubscribeEvent
    public void onHarvestBlock(BlockEvent.HarvestDropsEvent ev)
    {
        if (ev.isSilkTouching())
            return;

        if (ev.getState().getBlock() != Blocks.TALLGRASS || ev.getState().getValue(BlockTallGrass.TYPE) == DEAD_BUSH)
            return;

        List<ItemStack> drops = ev.getDrops();
        if (drops instanceof ImmutableList)
        {
            Survivalist.logger.warn("WARNING: Some mod is returning an ImmutableList from HarvestBlocks, replacing drops will NOT be possible.");
            return;
        }

        if (rnd.nextFloat() < 0.12f)
            drops.add(new ItemStack(Survivalist.plant_fibres));
    }
}
