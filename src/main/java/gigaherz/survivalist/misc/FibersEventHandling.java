package gigaherz.survivalist.misc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

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
                drops.add(new ItemStack(Survivalist.Items.plant_fibres));
        }
        catch (UnsupportedOperationException ex)
        {
            if (!(drops instanceof NonNullList))
                throw ex;
/*
            // Workaround for getDrops using a fixed-length NonNullList
            if (nonNullListDelegate == null)
            {
                nonNullListDelegate = ObfuscationReflectionHelper.findField(drops.getClass(), "field_191198_a");
            }

            try
            {
                nonNullListDelegate.set(drops, Lists.newArrayList(drops));
                drops.add(new ItemStack(Survivalist.Items.plant_fibres));
            }
            catch (IllegalAccessException e)
            {
                throw ex;
            }
 */
        }
    }

    /*@ObjectHolder("biomesoplenty:plant_0")
    public static Block bopPlant0 = null;

    @ObjectHolder("biomesoplenty:plant_1")
    public static Block bopPlant1 = null;

    @ObjectHolder("biomesoplenty:double_plant")
    public static Block bopPlantDouble = null;

    @ObjectHolder("biomesoplenty:ivy")
    public static Block bopVineIvy = null;

    @ObjectHolder("biomesoplenty:willow_vine")
    public static Block bopVineWillow = null;

    @ObjectHolder("biomesoplenty:flower_vine")
    public static Block bopVineFlowering = null;*/

    public static Tag<Block> FIBRE_SOURCES = new BlockTags.Wrapper(new ResourceLocation("survivalist:fibre_sources"));

    private boolean isFibreSource(BlockState state)
    {
        /*Block block = state.getBlock();

        // vanilla grass
        if (block == Blocks.TALL_GRASS || block == Blocks.GRASS)
            return true;
        // vanila vines
        if (block == Blocks.VINE)
            return true;
        // BOP grass
        if (block == bopPlant0 || block == bopPlant1 || block == bopPlantDouble)
            return true;
        // BOP vines
        if (block == bopVineIvy || block == bopVineWillow || block == bopVineFlowering)
            return true;*/

        return state.isIn(FIBRE_SOURCES);
    }
}
