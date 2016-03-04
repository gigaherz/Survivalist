package gigaherz.survivalist;

import gigaherz.survivalist.rack.BlockRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.rocks.RocksEventHandling;
import gigaherz.survivalist.scraping.EnchantmentScraping;
import gigaherz.survivalist.scraping.ItemBreakingTracker;
import gigaherz.survivalist.rocks.ItemOreRock;
import gigaherz.survivalist.rocks.ItemRock;
import gigaherz.survivalist.torchfire.TorchFireEventHandling;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(modid = Survivalist.MODID, version = Survivalist.VERSION)
public class Survivalist
{
    public static final String MODID = "survivalist";
    public static final String VERSION = "1.0";

    // The instance of your mod that Forge uses.
    @Mod.Instance(value = Survivalist.MODID)
    public static Survivalist instance;

    @SidedProxy(clientSide = "gigaherz.survivalist.client.ClientProxy", serverSide = "gigaherz.survivalist.server.ServerProxy")
    public static ISidedProxy proxy;

    public static Logger logger;

    private GuiHandler guiHandler = new GuiHandler();

    public static EnchantmentScraping scraping;

    public static Item chainmail;
    public static Item tanned_leather;
    public static Item jerky;
    public static Item iron_nugget;
    public static Item rock;
    public static Item rock_ore;

    public static Item tanned_helmet;
    public static Item tanned_chestplate;
    public static Item tanned_leggings;
    public static Item tanned_boots;

    public static ItemStack rock_normal;
    public static ItemStack rock_andesite;
    public static ItemStack rock_diorite;
    public static ItemStack rock_granite;

    public static ItemStack iron_ore_rock;
    public static ItemStack gold_ore_rock;

    public static Block rack;

    public static ItemArmor.ArmorMaterial TANNED_LEATHER =
            EnumHelper.addArmorMaterial("tanned_leather", MODID + ":tanned_leather", 12,
                    new int[]{2, 4, 3, 1}, 15);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        ConfigManager.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));

        if (ConfigManager.instance.enableScraping)
        {
            ItemBreakingTracker.register();

            scraping = EnchantmentScraping.register();
        }

        if(ConfigManager.instance.enableTorchFire)
        {
            TorchFireEventHandling.register();
        }

        if (ConfigManager.instance.enableChainmailCrafting)
        {
            chainmail = new Item().setUnlocalizedName(MODID + ".chainmail").setCreativeTab(CreativeTabs.tabMaterials);
            GameRegistry.registerItem(chainmail, "chainmail");
        }

        if (ConfigManager.instance.enableDryingRack)
        {
            rack = new BlockRack();
            GameRegistry.registerBlock(rack, "rack");
            GameRegistry.registerTileEntity(TileRack.class, "tileRack");
        }

        if(ConfigManager.instance.enableLeatherTanning)
        {
            tanned_leather = new Item().setUnlocalizedName(MODID + ".tanned_leather").setCreativeTab(CreativeTabs.tabMaterials);
            GameRegistry.registerItem(tanned_leather, "tanned_leather");
            OreDictionary.registerOre("materialLeather", tanned_leather);
            OreDictionary.registerOre("materialTannedLeather", tanned_leather);
            OreDictionary.registerOre("materialHardenedLeather", tanned_leather);

            tanned_helmet = new ItemArmor(TANNED_LEATHER, 0, 0).setUnlocalizedName(MODID + ".helmetLeather").setCreativeTab(CreativeTabs.tabCombat);
            GameRegistry.registerItem(tanned_helmet, "tanned_helmet");
            tanned_chestplate = new ItemArmor(TANNED_LEATHER, 0, 1).setUnlocalizedName(MODID + ".chestplateLeather").setCreativeTab(CreativeTabs.tabCombat);
            GameRegistry.registerItem(tanned_chestplate, "tanned_chestplate");
            tanned_leggings = new ItemArmor(TANNED_LEATHER, 0, 2).setUnlocalizedName(MODID + ".leggingsLeather").setCreativeTab(CreativeTabs.tabCombat);
            GameRegistry.registerItem(tanned_leggings, "tanned_leggings");
            tanned_boots = new ItemArmor(TANNED_LEATHER, 0, 3).setUnlocalizedName(MODID + ".bootsLeather").setCreativeTab(CreativeTabs.tabCombat);
            GameRegistry.registerItem(tanned_boots, "tanned_boots");
        }

        if(ConfigManager.instance.enableJerky)
        {
            jerky = new ItemFood(4, 1, true).setUnlocalizedName(Survivalist.MODID + ".jerky").setCreativeTab(CreativeTabs.tabFood);
            GameRegistry.registerItem(jerky, "jerky");
        }

        if(ConfigManager.instance.enableIronNugget)
        {
            iron_nugget = new Item().setUnlocalizedName(Survivalist.MODID + ".iron_nugget").setCreativeTab(CreativeTabs.tabMaterials);
            GameRegistry.registerItem(iron_nugget, "iron_nugget");
            OreDictionary.registerOre("nuggetIron", iron_nugget);
        }

        if(ConfigManager.instance.enableRocks)
        {
            RocksEventHandling.register();

            rock = new ItemRock().setCreativeTab(CreativeTabs.tabMaterials);
            GameRegistry.registerItem(rock, "rock");

            rock_ore = new ItemOreRock().setCreativeTab(CreativeTabs.tabMaterials);
            GameRegistry.registerItem(rock_ore, "rock_ore");

            iron_ore_rock = new ItemStack(rock_ore, 1, 0);
            gold_ore_rock = new ItemStack(rock_ore, 1, 1);
            OreDictionary.registerOre("rockOreIron", iron_ore_rock);
            OreDictionary.registerOre("rockOreGold", gold_ore_rock);

            rock_normal = new ItemStack(rock, 1, 0);
            rock_andesite = new ItemStack(rock, 1, 1);
            rock_diorite = new ItemStack(rock, 1, 2);
            rock_granite = new ItemStack(rock, 1, 3);
            OreDictionary.registerOre("rock", rock_normal);
            OreDictionary.registerOre("rock", rock_andesite);
            OreDictionary.registerOre("rock", rock_diorite);
            OreDictionary.registerOre("rock", rock_granite);
            OreDictionary.registerOre("rockAndesite", rock_andesite);
            OreDictionary.registerOre("rockDiorite", rock_diorite);
            OreDictionary.registerOre("rockGranite", rock_granite);
        }

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

        if(ConfigManager.instance.removeSticksFromPlanks)
        {
            List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
            for (int i = 0; i < recipes.size(); )
            {
                boolean removed = false;
                IRecipe r = recipes.get(i);
                if (r instanceof ShapedOreRecipe)
                {
                    if (r.getRecipeOutput().getItem() == Items.stick)
                    {
                        recipes.remove(r);
                        removed = true;
                    }
                }

                if (!removed) i++;
            }
        }

        if(ConfigManager.instance.enableDryingRack)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rack),
                    "sss",
                    " p ",
                    "p p",
                    's', "stickWood",
                    'p', "plankWood"));

            Dryable.register();
        }

        if(ConfigManager.instance.sticksFromLeaves)
            GameRegistry.addRecipe(new ShapelessOreRecipe(Items.stick,"treeLeaves"));

        if(ConfigManager.instance.sticksFromSaplings)
            GameRegistry.addRecipe(new ShapelessOreRecipe(Items.stick,"treeSapling"));

        if(ConfigManager.instance.enableIronNugget)
        {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(iron_nugget, 9), "ingotIron"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.iron_ingot),
                    "nnn",
                    "nnn",
                    "nnn",
                    'n', "nuggetIron"));
        }

        if(ConfigManager.instance.enableChainmailCrafting)
        {
            if(ConfigManager.instance.enableIronNugget)
            {
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chainmail),
                        " n ",
                        "n n",
                        " n ",
                        'n', "nuggetIron"));
            }

            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(chainmail, 9),
                    " n ",
                    "n n",
                    " n ",
                    'n', "ingotIron"));

            GameRegistry.addRecipe(new ItemStack(Items.chainmail_helmet),
                    "ccc",
                    "c c",
                    'c', chainmail);

            GameRegistry.addRecipe(new ItemStack(Items.chainmail_chestplate),
                    "c c",
                    "ccc",
                    "ccc",
                    'c', chainmail);

            GameRegistry.addRecipe(new ItemStack(Items.chainmail_leggings),
                    "ccc",
                    "c c",
                    "c c",
                    'c', chainmail);

            GameRegistry.addRecipe(new ItemStack(Items.chainmail_boots),
                    "c c",
                    "c c",
                    'c', chainmail);
        }

        if(ConfigManager.instance.enableRocks)
        {
            GameRegistry.addSmelting(iron_ore_rock, new ItemStack(iron_nugget), 0.1f);
            GameRegistry.addSmelting(gold_ore_rock, new ItemStack(Items.gold_nugget), 0.1f);

            GameRegistry.addRecipe(new ItemStack(Blocks.cobblestone),
                    "rrr",
                    "rcr",
                    "rrr",
                    'r', rock_normal,
                    'c', Items.clay_ball);

            GameRegistry.addRecipe(new ItemStack(Blocks.stone, 1, 5),
                    "rrr",
                    "rcr",
                    "rrr",
                    'r', rock_andesite,
                    'c', Items.clay_ball);

            GameRegistry.addRecipe(new ItemStack(Blocks.stone, 1, 3),
                    "rrr",
                    "rcr",
                    "rrr",
                    'r', rock_diorite,
                    'c', Items.clay_ball);

            GameRegistry.addRecipe(new ItemStack(Blocks.stone, 1, 1),
                    "rrr",
                    "rcr",
                    "rrr",
                    'r', rock_granite,
                    'c', Items.clay_ball);

            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.gravel),
                    "rr",
                    "rr",
                    'r', "rock"));

            GameRegistry.addShapelessRecipe(new ItemStack(rock, 4, 0), Blocks.gravel);
            GameRegistry.addShapelessRecipe(new ItemStack(Items.flint), Blocks.gravel, Blocks.gravel, Blocks.gravel, Blocks.gravel);
        }
    }
}
