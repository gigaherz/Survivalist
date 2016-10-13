package gigaherz.survivalist;

import com.google.common.collect.Lists;
import gigaherz.common.*;
import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.api.Dryable;
import gigaherz.survivalist.chopblock.BlockChopping;
import gigaherz.survivalist.chopblock.TileChopping;
import gigaherz.survivalist.rack.BlockRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.rocks.*;
import gigaherz.survivalist.scraping.EnchantmentScraping;
import gigaherz.survivalist.scraping.ItemBreakingTracker;
import gigaherz.survivalist.scraping.MessageScraping;
import gigaherz.survivalist.torchfire.TorchFireEventHandling;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
@Mod(modid = Survivalist.MODID, version = Survivalist.VERSION, acceptedMinecraftVersions = "[1.9.4,1.11.0)",
        dependencies = "required-after:Forge@[12.17.0.1916,)")
public class Survivalist
{
    public static final String MODID = "survivalist";
    public static final String VERSION = "@VERSION@";
    private static final String CHANNEL = "survivalist";

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
    public static ItemNugget nugget;
    public static ItemRock rock;
    public static ItemOreRock rock_ore;
    public static Item dough;
    public static Item round_bread;
    public static Item hatchet;

    public static Item tanned_helmet;
    public static Item tanned_chestplate;
    public static Item tanned_leggings;
    public static Item tanned_boots;

    public static BlockRegistered rack;

    public static BlockRegistered chopping_block;

    public static ItemArmor.ArmorMaterial TANNED_LEATHER =
            EnumHelper.addArmorMaterial("tanned_leather", MODID + ":tanned_leather", 12,
                    new int[]{2, 4, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1);

    public static ItemTool.ToolMaterial TOOL_FLINT =
            EnumHelper.addToolMaterial("flint", 1, 150, 5.0F, 1.5F, 5);

    public static SimpleNetworkWrapper channel;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        rack = new BlockRack("rack");
        GameRegistry.register(rack);
        GameRegistry.register(rack.createItemBlock());
        GameRegistry.registerTileEntity(TileRack.class, rack.getRegistryName().toString());
        addAlternativeName(TileRack.class, "tileRack");

        chopping_block = new BlockChopping("chopping_block");
        GameRegistry.register(chopping_block);
        GameRegistry.register(chopping_block.createItemBlock());
        GameRegistry.registerTileEntity(TileChopping.class, "tile_chopping_block");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        chainmail = new ItemRegistered("chainmail").setCreativeTab(CreativeTabs.MATERIALS);
        GameRegistry.register(chainmail);

        tanned_leather = new ItemRegistered("tanned_leather").setCreativeTab(CreativeTabs.MATERIALS);
        GameRegistry.register(tanned_leather);
        OreDictionary.registerOre("materialLeather", tanned_leather);
        OreDictionary.registerOre("materialTannedLeather", tanned_leather);
        OreDictionary.registerOre("materialHardenedLeather", tanned_leather);

        tanned_helmet = new ItemRegisteredArmor("tanned_helmet", TANNED_LEATHER, 0, EntityEquipmentSlot.HEAD);
        GameRegistry.register(tanned_helmet);

        tanned_chestplate = new ItemRegisteredArmor("tanned_chestplate", TANNED_LEATHER, 0, EntityEquipmentSlot.CHEST);
        GameRegistry.register(tanned_chestplate);

        tanned_leggings = new ItemRegisteredArmor("tanned_leggings", TANNED_LEATHER, 0, EntityEquipmentSlot.LEGS);
        GameRegistry.register(tanned_leggings);

        tanned_boots = new ItemRegisteredArmor("tanned_boots", TANNED_LEATHER, 0, EntityEquipmentSlot.FEET);
        GameRegistry.register(tanned_boots);

        jerky = new ItemRegisteredFood("jerky", 4, 1, true);
        GameRegistry.register(jerky);

        nugget = new ItemNugget("nugget");
        GameRegistry.register(nugget);

        OreDictionary.registerOre("nuggetIron", nugget.getStack(ItemNugget.Subtype.IRON));
        OreDictionary.registerOre("nuggetCopper", nugget.getStack(ItemNugget.Subtype.COPPER));
        OreDictionary.registerOre("nuggetTin", nugget.getStack(ItemNugget.Subtype.TIN));
        OreDictionary.registerOre("nuggetLead", nugget.getStack(ItemNugget.Subtype.LEAD));
        OreDictionary.registerOre("nuggetSilver", nugget.getStack(ItemNugget.Subtype.SILVER));

        RocksEventHandling.register();

        rock = new ItemRock("rock");
        GameRegistry.register(rock);

        rock_ore = new ItemOreRock("rock_ore");
        GameRegistry.register(rock_ore);

        OreDictionary.registerOre("rockOreIron",   rock_ore.getStack(ItemOreRock.Subtype.IRON));
        OreDictionary.registerOre("rockOreGold",   rock_ore.getStack(ItemOreRock.Subtype.GOLD));
        OreDictionary.registerOre("rockOreCopper", rock_ore.getStack(ItemOreRock.Subtype.COPPER));
        OreDictionary.registerOre("rockOreTin",    rock_ore.getStack(ItemOreRock.Subtype.TIN));
        OreDictionary.registerOre("rockOreLead",   rock_ore.getStack(ItemOreRock.Subtype.LEAD));
        OreDictionary.registerOre("rockOreSilver", rock_ore.getStack(ItemOreRock.Subtype.SILVER));

        OreDictionary.registerOre("rock", rock.getStack(ItemRock.Subtype.NORMAL));
        OreDictionary.registerOre("rock", rock.getStack(ItemRock.Subtype.ANDESITE));
        OreDictionary.registerOre("rock", rock.getStack(ItemRock.Subtype.DIORITE));
        OreDictionary.registerOre("rock", rock.getStack(ItemRock.Subtype.GRANITE));
        OreDictionary.registerOre("rockAndesite", rock.getStack(ItemRock.Subtype.ANDESITE));
        OreDictionary.registerOre("rockDiorite", rock.getStack(ItemRock.Subtype.DIORITE));
        OreDictionary.registerOre("rockGranite", rock.getStack(ItemRock.Subtype.GRANITE));

        dough = new ItemRegisteredFood("dough", 5, 0.6f, true);
        GameRegistry.register(dough);

        round_bread = new ItemRegisteredFood("round_bread", 8, 0.6f, true);
        GameRegistry.register(round_bread);

        hatchet = new ItemRegisteredAxe("hatchet", TOOL_FLINT, 8.0F, -3.1F).setCreativeTab(CreativeTabs.TOOLS);
        GameRegistry.register(hatchet);
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event)
    {
        scraping = EnchantmentScraping.register();
    }

    private void registerNetwork()
    {
        logger.info("Registering network channel...");

        channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

        int messageNumber = 0;
        channel.registerMessage(MessageScraping.Handler.class, MessageScraping.class, messageNumber++, Side.CLIENT);
        logger.debug("Final message number: " + messageNumber);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        ConfigManager.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));

        if (ConfigManager.instance.enableTorchFire)
        {
            TorchFireEventHandling.register();
        }

        if (ConfigManager.instance.enableScraping)
        {
            ItemBreakingTracker.register();
        }

        registerNetwork();

        proxy.preInit();

        if (Loader.isModLoaded("MineTweaker3"))
        {
            try
            {
                Class.forName("gigaherz.survivalist.integration.MineTweakerPlugin").getMethod("init").invoke(null);
            }
            catch (Exception e)
            {
                throw new ReportedException(new CrashReport("Error initializing minetweaker integration", e));
            }
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        int entityId = 1;

        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

        if (ConfigManager.instance.enableBread)
        {
            if (ConfigManager.instance.removeVanillaBread)
            {
                List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
                for (int i = 0; i < recipes.size(); )
                {
                    boolean removed = false;
                    IRecipe r = recipes.get(i);
                    if (r instanceof ShapedOreRecipe)
                    {
                        ItemStack output = r.getRecipeOutput();
                        if (output != null && output.getItem() == Items.BREAD)
                        {
                            recipes.remove(r);
                            removed = true;
                        }
                    }

                    if (!removed) i++;
                }
            }

            GameRegistry.addShapelessRecipe(new ItemStack(dough), Items.WHEAT, Items.WHEAT, Items.WHEAT, Items.WHEAT);
            GameRegistry.addSmelting(dough, new ItemStack(round_bread), 0);
        }

        if (ConfigManager.instance.enableDryingRack)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rack),
                    "sss",
                    " p ",
                    "p p",
                    's', "stickWood",
                    'p', "plankWood"));

            Dryable.registerStockRecipes();

            if (ConfigManager.instance.enableLeatherTanning)
            {
                if (ConfigManager.instance.enableSaddleCrafting)
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.SADDLE),
                            "ttt",
                            "tst",
                            "i i",
                            't', "materialTannedLeather",
                            's', new ItemStack(Items.STRING),
                            'i', "ingotIron"));
                }

                GameRegistry.addRecipe(new ItemStack(tanned_helmet),
                        "ttt",
                        "t t",
                        't', tanned_leather);

                GameRegistry.addRecipe(new ItemStack(tanned_chestplate),
                        "t t",
                        "ttt",
                        "ttt",
                        't', tanned_leather);

                GameRegistry.addRecipe(new ItemStack(tanned_leggings),
                        "ttt",
                        "t t",
                        "t t",
                        't', tanned_leather);

                GameRegistry.addRecipe(new ItemStack(tanned_boots),
                        "t t",
                        "t t",
                        't', tanned_leather);
            }
        }

        if (ConfigManager.instance.sticksFromLeaves)
            GameRegistry.addRecipe(new ShapelessOreRecipe(Items.STICK, "treeLeaves"));

        if (ConfigManager.instance.sticksFromSaplings)
            GameRegistry.addRecipe(new ShapelessOreRecipe(Items.STICK, "treeSapling"));

        if (ConfigManager.instance.enableNuggetRecipes)
        {
            addIngotToNuggets("ingotIron", "nuggetIron");
            addIngotToNuggets("ingotCopper", "nuggetCopper");
            addIngotToNuggets("ingotTin", "nuggetTin");
            addIngotToNuggets("ingotLead", "nuggetLead");
            addIngotToNuggets("ingotSilver", "nuggetSilver");
        }

        if (ConfigManager.instance.enableChainmailCrafting)
        {
            if (ConfigManager.instance.enableNuggets)
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

            GameRegistry.addRecipe(new ItemStack(Items.CHAINMAIL_HELMET),
                    "ccc",
                    "c c",
                    'c', chainmail);

            GameRegistry.addRecipe(new ItemStack(Items.CHAINMAIL_CHESTPLATE),
                    "c c",
                    "ccc",
                    "ccc",
                    'c', chainmail);

            GameRegistry.addRecipe(new ItemStack(Items.CHAINMAIL_LEGGINGS),
                    "ccc",
                    "c c",
                    "c c",
                    'c', chainmail);

            GameRegistry.addRecipe(new ItemStack(Items.CHAINMAIL_BOOTS),
                    "c c",
                    "c c",
                    'c', chainmail);
        }

        if (ConfigManager.instance.enableRocks)
        {
            EntityRegistry.registerModEntity(EntityRock.class, "ThrownRock", entityId++, this, 80, 3, true);
            logger.debug("Last used id: %i", entityId);

            addSmeltingNugget(rock_ore.getStack(ItemOreRock.Subtype.IRON), "nuggetIron");
            addSmeltingNugget(rock_ore.getStack(ItemOreRock.Subtype.GOLD), "nuggetGold");
            addSmeltingNugget(rock_ore.getStack(ItemOreRock.Subtype.COPPER), "nuggetCopper");
            addSmeltingNugget(rock_ore.getStack(ItemOreRock.Subtype.TIN), "nuggetTin");
            addSmeltingNugget(rock_ore.getStack(ItemOreRock.Subtype.LEAD), "nuggetLead");
            addSmeltingNugget(rock_ore.getStack(ItemOreRock.Subtype.SILVER), "nuggetSilver");

            GameRegistry.addRecipe(new ItemStack(Blocks.COBBLESTONE),
                    "rrr",
                    "rcr",
                    "rrr",
                    'r', rock.getStack(ItemRock.Subtype.NORMAL),
                    'c', Items.CLAY_BALL);

            GameRegistry.addRecipe(new ItemStack(Blocks.STONE, 1, 5),
                    "rrr",
                    "rcr",
                    "rrr",
                    'r', rock.getStack(ItemRock.Subtype.ANDESITE),
                    'c', Items.CLAY_BALL);

            GameRegistry.addRecipe(new ItemStack(Blocks.STONE, 1, 3),
                    "rrr",
                    "rcr",
                    "rrr",
                    'r', rock.getStack(ItemRock.Subtype.DIORITE),
                    'c', Items.CLAY_BALL);

            GameRegistry.addRecipe(new ItemStack(Blocks.STONE, 1, 1),
                    "rrr",
                    "rcr",
                    "rrr",
                    'r', rock.getStack(ItemRock.Subtype.GRANITE),
                    'c', Items.CLAY_BALL);

            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.GRAVEL),
                    "rr",
                    "rr",
                    'r', "rock"));

            GameRegistry.addShapelessRecipe(new ItemStack(rock, 4, 0), Blocks.GRAVEL);
            GameRegistry.addShapelessRecipe(new ItemStack(Items.FLINT), Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL);
        }

        if (ConfigManager.instance.enableHatchet)
        {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(hatchet),
                    "stickWood", "string", Items.FLINT));
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (ConfigManager.instance.removeSticksFromPlanks)
        {
            List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
            for (int i = 0; i < recipes.size(); )
            {
                boolean removed = false;
                IRecipe r = recipes.get(i);
                if (r instanceof ShapedOreRecipe)
                {
                    ItemStack output = r.getRecipeOutput();
                    if (output != null && output.getItem() == Items.STICK)
                    {
                        recipes.remove(r);
                        removed = true;
                    }
                }

                if (!removed) i++;
            }
        }

        if (ConfigManager.instance.enableChopping)
        {
            if (ConfigManager.instance.importPlanksRecipes || ConfigManager.instance.removePlanksRecipes)
            {
                List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
                for (int i = 0; i < recipes.size(); )
                {
                    boolean removed = false;
                    IRecipe r = recipes.get(i);

                    ItemStack output = r.getRecipeOutput();
                    if (output != null && hasOreName(output, "plankWood"))
                    {
                        List<Object> inputs = null;
                        if (r instanceof ShapedRecipes)
                        {
                            ShapedRecipes rcp = (ShapedRecipes) r;
                            inputs = Lists.newArrayList((Object[]) rcp.recipeItems);
                        }
                        else if (r instanceof ShapelessRecipes)
                        {
                            ShapelessRecipes rcp = (ShapelessRecipes) r;
                            inputs = Lists.newArrayList(rcp.recipeItems);
                        }
                        else if (r instanceof ShapedOreRecipe)
                        {
                            ShapedOreRecipe rcp = (ShapedOreRecipe) r;
                            inputs = Lists.newArrayList(rcp.getInput());
                        }
                        else if (r instanceof ShapelessOreRecipe)
                        {
                            ShapelessOreRecipe rcp = (ShapelessOreRecipe) r;
                            inputs = Lists.newArrayList(rcp.getInput());
                        }
                        else
                        {
                            logger.warn("Unknown recipe type with planks output (" + output.getItem().getRegistryName() + "): " + r.getClass().getName());
                        }

                        if (inputs != null)
                        {
                            ItemStack logInput = null;
                            String oreInput = null;

                            for (Object input : inputs)
                            {
                                if (input instanceof ItemStack)
                                {
                                    ItemStack stack = (ItemStack) input;
                                    if (!hasOreName(stack, "logWood") || logInput != null || oreInput != null)
                                    {
                                        logInput = null;
                                        oreInput = null;
                                        break;
                                    }
                                    logInput = stack;
                                }
                                else if (input instanceof String)
                                {
                                    logger.warn("A recipe with planks output uses ore dictionary string as input. This is not supported yet.");

                                    logInput = null;
                                    oreInput = null;
                                    break;

                                    /*
                                    String oreName = (String) input;
                                    if (<verify that the inputs are logs> || logInput != null || oreInput != null)
                                    {
                                        logInput = null;
                                        oreInput = null;
                                        break;
                                    }
                                    oreInput = oreName;*/
                                }
                            }

                            if (logInput != null)
                            {
                                if (ConfigManager.instance.removePlanksRecipes)
                                {
                                    removed = recipes.remove(r);
                                }
                                if (ConfigManager.instance.importPlanksRecipes)
                                {
                                    Choppable.registerRecipe(logInput.copy(), output.copy());
                                }
                            }
                            else if (oreInput != null)
                            {
                                if (ConfigManager.instance.removePlanksRecipes)
                                {
                                    removed = recipes.remove(r);
                                }
                                if (ConfigManager.instance.importPlanksRecipes)
                                {
                                    Choppable.registerRecipe(logInput.copy(), output.copy());
                                }
                            }
                        }
                    }

                    if (!removed) i++;
                }


                GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(chopping_block), "logWood"));
            }

            if (ConfigManager.instance.removeSticksFromPlanks)
            {
                Choppable.registerStockRecipes();
            }
        }
    }

    public static boolean hasOreName(ItemStack stack, String oreName)
    {
        if (stack.getItem() == null)
        {
            logger.warn("Detected ItemStack with null item inside!");
            return false;
        }

        int id = OreDictionary.getOreID(oreName);
        for (int i : OreDictionary.getOreIDs(stack))
        {
            if (i == id) return true;
        }
        return false;
    }

    private static void addIngotToNuggets(String oreIngot, String oreNugget)
    {
        List<ItemStack> matches1 = OreDictionary.getOres(oreIngot);
        if (matches1.size() > 0)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(matches1.get(0), "nnn", "nnn", "nnn", 'n', oreNugget));
        }

        List<ItemStack> matches2 = OreDictionary.getOres(oreNugget);
        if (matches2.size() > 0)
        {
            ItemStack output = matches2.get(0).copy();
            output.stackSize = 9;
            GameRegistry.addRecipe(new ShapelessOreRecipe(output, oreIngot));
        }
    }

    private static void addSmeltingNugget(ItemStack stack, String ore)
    {
        List<ItemStack> matches = OreDictionary.getOres(ore);
        if (matches.size() > 0)
        {
            GameRegistry.addSmelting(stack, matches.get(0), 0.1f);
        }
    }

    private static Map<String, Class<? extends TileEntity>> nameToClassMap = ReflectionHelper.getPrivateValue(TileEntity.class, null, "field_145855_i", "nameToClassMap");

    private static void addAlternativeName(Class<? extends TileEntity> clazz, String altName)
    {
        nameToClassMap.put(altName, clazz);
    }

    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }
}
