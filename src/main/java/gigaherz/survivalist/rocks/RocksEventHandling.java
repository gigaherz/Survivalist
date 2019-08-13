package gigaherz.survivalist.rocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Random;

public class RocksEventHandling
{
    public static final Tag<Item> TAG_IRON_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/iron"));
    public static final Tag<Item> TAG_GOLD_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/gold"));
    public static final Tag<Item> TAG_COPPER_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/copper"));
    public static final Tag<Item> TAG_TIN_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/tin"));
    public static final Tag<Item> TAG_LEAD_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/lead"));
    public static final Tag<Item> TAG_SILVER_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/silver"));
    public static final Tag<Item> TAG_ALUMINUM_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/aluminum"));

    public static final Tag<Item> TAG_POOR_IRON_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/poor_iron"));
    public static final Tag<Item> TAG_POOR_GOLD_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/poor_gold"));
    public static final Tag<Item> TAG_POOR_COPPER_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/poor_copper"));
    public static final Tag<Item> TAG_POOR_TIN_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/poor_tin"));
    public static final Tag<Item> TAG_POOR_LEAD_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/poor_lead"));
    public static final Tag<Item> TAG_POOR_SILVER_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/poor_silver"));
    public static final Tag<Item> TAG_POOR_ALUMINUM_ORE = new ItemTags.Wrapper(new ResourceLocation("forge", "items/ore/poor_aluminum"));

    private final Random rnd = new Random();

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new RocksEventHandling());
    }

    @SubscribeEvent
    public void onHarvestBlock(BlockEvent.HarvestDropsEvent ev)
    {
        if (ev.isSilkTouching())
            return;

        List<ItemStack> drops = ev.getDrops();
        if (drops instanceof ImmutableList)
        {
            Survivalist.logger.warn("WARNING: Some mod is returning an ImmutableList from HarvestBlocks, replacing drops will NOT be possible.");
            return;
        }

        boolean anyChanged = false;
        List<ItemStack> newDrops = Lists.newArrayList();

        int fortune = ev.getFortuneLevel();

        for (ItemStack drop : drops)
        {
            if (drop.getCount() <= 0)
                continue;

            if (drop.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE) && ConfigManager.replaceStoneDrops)
            {
                newDrops.add(new ItemStack(Survivalist.Items.rock_stone, 4));
                anyChanged = true;
            }
            else if (drop.getItem() == Item.getItemFromBlock(Blocks.STONE) && ConfigManager.replaceStoneDrops)
            {
                newDrops.add(new ItemStack(Survivalist.Items.rock_stone, 4));
                anyChanged = true;
            }
            else if (drop.getItem() == Item.getItemFromBlock(Blocks.ANDESITE) && ConfigManager.replaceStoneDrops)
            {
                newDrops.add(new ItemStack(Survivalist.Items.rock_andesite, 4));
                anyChanged = true;
            }
            else if (drop.getItem() == Item.getItemFromBlock(Blocks.DIORITE) && ConfigManager.replaceStoneDrops)
            {
                newDrops.add(new ItemStack(Survivalist.Items.rock_diorite, 4));
                anyChanged = true;
            }
            else if (drop.getItem() == Item.getItemFromBlock(Blocks.GRANITE) && ConfigManager.replaceStoneDrops)
            {
                newDrops.add(new ItemStack(Survivalist.Items.rock_granite, 4));
                anyChanged = true;
            }
            else if (ConfigManager.replaceIronOreDrops && TAG_IRON_ORE.contains(drop.getItem()))
            {
                newDrops.add(new ItemStack(Survivalist.Items.rock_iron_ore, applyFortune(getAmountNormal(), fortune)));
                anyChanged = true;
            }
            else if (ConfigManager.replaceGoldOreDrops && TAG_GOLD_ORE.contains(drop.getItem()))
            {
                newDrops.add(new ItemStack(Survivalist.Items.rock_gold_ore, applyFortune(getAmountNormal(), fortune)));
                anyChanged = true;
            }
            else if (ConfigManager.replaceModOreDrops)
            {
                if (TAG_COPPER_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_copper_ore, applyFortune(getAmountNormal(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_TIN_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_tin_ore, applyFortune(getAmountNormal(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_LEAD_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_lead_ore, applyFortune(getAmountNormal(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_SILVER_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_silver_ore, applyFortune(getAmountNormal(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_ALUMINUM_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_aluminum_ore, applyFortune(getAmountNormal(), fortune)));
                    anyChanged = true;
                }
                else
                {
                    newDrops.add(drop);
                }
            }
            else if (ConfigManager.replacePoorOreDrops)
            {
                if (TAG_POOR_IRON_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_iron_ore, applyFortune(getAmountPoor(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_POOR_GOLD_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_gold_ore, applyFortune(getAmountPoor(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_POOR_COPPER_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_copper_ore, applyFortune(getAmountPoor(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_POOR_TIN_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_tin_ore, applyFortune(getAmountPoor(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_POOR_LEAD_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_lead_ore, applyFortune(getAmountPoor(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_POOR_SILVER_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_silver_ore, applyFortune(getAmountPoor(), fortune)));
                    anyChanged = true;
                }
                else if (TAG_POOR_ALUMINUM_ORE.contains(drop.getItem()))
                {
                    newDrops.add(new ItemStack(Survivalist.Items.rock_aluminum_ore, applyFortune(getAmountPoor(), fortune)));
                    anyChanged = true;
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

        if (anyChanged)
        {
            drops.clear();
            drops.addAll(newDrops);
        }
    }

    private int getAmountPoor()
    {
        return 1 + Math.round(rnd.nextFloat());
    }

    private int getAmountNormal()
    {
        return 2 + Math.round(2 * rnd.nextFloat());
    }

    private int applyFortune(int amount, int fortune)
    {
        int i = rnd.nextInt(fortune + 2) - 1;

        if (i < 0)
        {
            i = 0;
        }

        return amount * (i + 1);
    }
}
