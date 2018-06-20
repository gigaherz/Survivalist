package gigaherz.survivalist.misc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
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

    private Field nonNullListDelegate;

    @SubscribeEvent
    public void onHarvestBlock(BlockEvent.HarvestDropsEvent ev)
    {
        if (ev.isSilkTouching())
            return;

        if (!isFibreSource(ev.getState()))
            return;

        List<ItemStack> drops = ev.getDrops();
        if (drops instanceof ImmutableList)
        {
            Survivalist.logger.warn("WARNING: Some mod is returning an ImmutableList from HarvestBlocks, replacing drops will NOT be possible.");
            return;
        }

        try
        {
            if (rnd.nextFloat() < 0.12f)
                drops.add(new ItemStack(Survivalist.plant_fibres));
        }
        catch (UnsupportedOperationException ex)
        {
            if (!(drops instanceof NonNullList))
                throw ex;

            // Workaround for getDrops using a fixed-length NonNullList
            if (nonNullListDelegate == null)
            {
                nonNullListDelegate = ReflectionHelper.findField(drops.getClass(), "field_191198_a", "delegate");
            }

            try
            {
                nonNullListDelegate.set(drops, Lists.newArrayList(drops));
                drops.add(new ItemStack(Survivalist.plant_fibres));
            }
            catch (IllegalAccessException e)
            {
                throw ex;
            }
        }
    }

    @GameRegistry.ObjectHolder("biomesoplenty:plant_0")
    public static Block bopPlant0 = null;

    @GameRegistry.ObjectHolder("biomesoplenty:plant_1")
    public static Block bopPlant1 = null;

    @GameRegistry.ObjectHolder("biomesoplenty:double_plant")
    public static Block bopPlantDouble = null;

    @GameRegistry.ObjectHolder("biomesoplenty:ivy")
    public static Block bopVineIvy = null;

    @GameRegistry.ObjectHolder("biomesoplenty:willow_vine")
    public static Block bopVineWillow = null;

    @GameRegistry.ObjectHolder("biomesoplenty:flower_vine")
    public static Block bopVineFlowering = null;

    private boolean isFibreSource(IBlockState state)
    {
        // vanilla grass
        if (state.getBlock() == Blocks.TALLGRASS && state.getValue(BlockTallGrass.TYPE) != DEAD_BUSH)
            return true;
        // vanila vines
        if (state.getBlock() == Blocks.VINE)
            return true;
        // BOP grass
        if (state.getBlock() == bopPlant0 || state.getBlock() == bopPlant1 || state.getBlock() == bopPlantDouble)
            return true;
        // BOP vines
        if (state.getBlock() == bopVineIvy || state.getBlock() == bopVineWillow || state.getBlock() == bopVineFlowering)
            return true;
        return false;
    }
}
