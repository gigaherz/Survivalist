package gigaherz.survivalist;

import gigaherz.common.*;
import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.api.Dryable;
import gigaherz.survivalist.chopblock.BlockChopping;
import gigaherz.survivalist.chopblock.TileChopping;
import gigaherz.survivalist.common.ItemTannedArmor;
import gigaherz.survivalist.misc.FibersEventHandling;
import gigaherz.survivalist.misc.StringEventHandling;
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
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber
@Mod(modid = Survivalist.MODID, version = Survivalist.VERSION, acceptedMinecraftVersions = "[1.12.0,1.13.0)")
public class Survivalist
{
    public static final String MODID = "survivalist";
    public static final String VERSION = "@VERSION@";
    private static final String CHANNEL = "survivalist";

    // The instance of your mod that Forge uses.
    @Mod.Instance(value = Survivalist.MODID)
    public static Survivalist instance;

    @SidedProxy(clientSide = "gigaherz.survivalist.client.ClientProxy", serverSide = "gigaherz.survivalist.server.ServerProxy")
    public static IModProxy proxy;

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
    public static Item pick;
    public static Item spade;
    public static Item plant_fibres;

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
        event.getRegistry().registerAll(
                rack = new BlockRack("rack"),
                chopping_block = new BlockChopping("chopping_block")
        );
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
    }

    public static void registerTileEntities()
    {
        GameRegistry.registerTileEntityWithAlternatives(TileRack.class, rack.getRegistryName().toString(), "tileRack");
        GameRegistry.registerTileEntityWithAlternatives(TileChopping.class, chopping_block.getRegistryName().toString(), "tile_chopping_block");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                // ItemBlocks
                rack.createItemBlock(),
                chopping_block.createItemBlock(),

                // Items
                chainmail = new ItemRegistered("chainmail")
                {
                    @Override
                    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
                    {
                        if (ConfigManager.instance.enableChainmailCrafting)
                        {
                            super.getSubItems(tab, subItems);
                        }
                    }
                }.setCreativeTab(CreativeTabs.MATERIALS),
                tanned_leather = new ItemRegistered("tanned_leather")
                {
                    @Override
                    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
                    {
                        if (ConfigManager.instance.enableLeatherTanning)
                        {
                            super.getSubItems(tab, subItems);
                        }
                    }
                }.setCreativeTab(CreativeTabs.MATERIALS),
                tanned_helmet = new ItemTannedArmor("tanned_helmet", TANNED_LEATHER, 0, EntityEquipmentSlot.HEAD),
                tanned_chestplate = new ItemTannedArmor("tanned_chestplate", Survivalist.TANNED_LEATHER, 0, EntityEquipmentSlot.CHEST),
                tanned_leggings = new ItemTannedArmor("tanned_leggings", TANNED_LEATHER, 0, EntityEquipmentSlot.LEGS),
                tanned_boots = new ItemTannedArmor("tanned_boots", TANNED_LEATHER, 0, EntityEquipmentSlot.FEET),
                jerky = new ItemRegisteredFood("jerky", 4, 1, true)
                {
                    @Override
                    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
                    {
                        if (ConfigManager.instance.enableLeatherTanning)
                        {
                            super.getSubItems(tab, subItems);
                        }
                    }
                },
                nugget = new ItemNugget("nugget"),
                rock = new ItemRock("rock"),
                rock_ore = new ItemOreRock("rock_ore"),
                dough = new ItemRegisteredFood("dough", 5, 0.6f, true)
                {
                    @Override
                    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
                    {
                        if (ConfigManager.instance.enableBread)
                        {
                            super.getSubItems(tab, subItems);
                        }
                    }
                },
                round_bread = new ItemRegisteredFood("round_bread", 8, 0.6f, true)
                {
                    @Override
                    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
                    {
                        if (ConfigManager.instance.enableBread)
                        {
                            super.getSubItems(tab, subItems);
                        }
                    }
                },
                hatchet = new ItemRegisteredAxe("hatchet", TOOL_FLINT, 8.0F, -3.1F)
                {
                    @Override
                    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
                    {
                        if (ConfigManager.instance.enableHatchet)
                        {
                            super.getSubItems(tab, subItems);
                        }
                    }
                }.setCreativeTab(CreativeTabs.TOOLS),
                pick = new ItemRegisteredPick("pick", TOOL_FLINT)
                {
                    @Override
                    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
                    {
                        if (ConfigManager.instance.enablePick)
                        {
                            super.getSubItems(tab, subItems);
                        }
                    }
                }.setCreativeTab(CreativeTabs.TOOLS),
                spade = new ItemRegisteredSpade("spade", TOOL_FLINT)
                {
                    @Override
                    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
                    {
                        if (ConfigManager.instance.enableSpade)
                        {
                            super.getSubItems(tab, subItems);
                        }
                    }
                }.setCreativeTab(CreativeTabs.TOOLS),
                plant_fibres = new ItemRegistered("plant_fibres")
                {
                    @Override
                    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
                    {
                        if (ConfigManager.instance.enableFibres)
                        {
                            super.getSubItems(tab, subItems);
                        }
                    }
                }.setCreativeTab(CreativeTabs.MATERIALS)
        );
    }

    private static void registerOredictNames()
    {
        OreDictionary.registerOre("materialLeather", tanned_leather);
        OreDictionary.registerOre("materialTannedLeather", tanned_leather);
        OreDictionary.registerOre("materialHardenedLeather", tanned_leather);

        OreDictionary.registerOre("nuggetIron", nugget.getStack(OreMaterial.IRON));
        OreDictionary.registerOre("nuggetCopper", nugget.getStack(OreMaterial.COPPER));
        OreDictionary.registerOre("nuggetTin", nugget.getStack(OreMaterial.TIN));
        OreDictionary.registerOre("nuggetLead", nugget.getStack(OreMaterial.LEAD));
        OreDictionary.registerOre("nuggetSilver", nugget.getStack(OreMaterial.SILVER));

        OreDictionary.registerOre("rockOreIron", rock_ore.getStack(OreMaterial.IRON));
        OreDictionary.registerOre("rockOreGold", rock_ore.getStack(OreMaterial.GOLD));
        OreDictionary.registerOre("rockOreCopper", rock_ore.getStack(OreMaterial.COPPER));
        OreDictionary.registerOre("rockOreTin", rock_ore.getStack(OreMaterial.TIN));
        OreDictionary.registerOre("rockOreLead", rock_ore.getStack(OreMaterial.LEAD));
        OreDictionary.registerOre("rockOreSilver", rock_ore.getStack(OreMaterial.SILVER));

        OreDictionary.registerOre("rock", rock.getStack(RockMaterial.NORMAL));
        OreDictionary.registerOre("rock", rock.getStack(RockMaterial.ANDESITE));
        OreDictionary.registerOre("rock", rock.getStack(RockMaterial.DIORITE));
        OreDictionary.registerOre("rock", rock.getStack(RockMaterial.GRANITE));
        OreDictionary.registerOre("rockAndesite", rock.getStack(RockMaterial.ANDESITE));
        OreDictionary.registerOre("rockDiorite", rock.getStack(RockMaterial.DIORITE));
        OreDictionary.registerOre("rockGranite", rock.getStack(RockMaterial.GRANITE));
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

        if (ConfigManager.instance.enableRocks)
        {
            RocksEventHandling.register();
        }

        if (ConfigManager.instance.dropFibersFromGrass)
        {
            FibersEventHandling.register();
        }

        if (ConfigManager.instance.dropStringFromSheep)
        {
            StringEventHandling.register();
        }

        registerTileEntities();

        registerOredictNames();

        registerNetwork();

        proxy.preInit();

        if (Loader.isModLoaded("MineTweaker3") || Loader.isModLoaded("crafttweaker"))
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

        registerNuggetToIngotRecipe("ingotIron", "nuggetIron", false);
        registerNuggetToIngotRecipe("ingotCopper", "nuggetCopper");
        registerNuggetToIngotRecipe("ingotTin", "nuggetTin");
        registerNuggetToIngotRecipe("ingotLead", "nuggetLead");
        registerNuggetToIngotRecipe("ingotSilver", "nuggetSilver");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        int entityId = 1;

        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

        if (ConfigManager.instance.enableRocks)
        {
            EntityRegistry.registerModEntity(location("thrown_rock"), EntityRock.class, "ThrownRock", entityId++, this, 80, 3, true);
            logger.debug("Last used id: %i", entityId);

            addSmeltingNugget(rock_ore.getStack(OreMaterial.IRON), "nuggetIron");
            addSmeltingNugget(rock_ore.getStack(OreMaterial.GOLD), "nuggetGold");
            addSmeltingNugget(rock_ore.getStack(OreMaterial.COPPER), "nuggetCopper");
            addSmeltingNugget(rock_ore.getStack(OreMaterial.TIN), "nuggetTin");
            addSmeltingNugget(rock_ore.getStack(OreMaterial.LEAD), "nuggetLead");
            addSmeltingNugget(rock_ore.getStack(OreMaterial.SILVER), "nuggetSilver");
        }

        if (ConfigManager.instance.enableDryingRack)
        {
            Dryable.registerStockRecipes();
        }

        if (ConfigManager.instance.enableBread)
        {
            GameRegistry.addSmelting(dough, new ItemStack(round_bread), 0);
        }

            /*
        if (ConfigManager.instance.enableRocks)
        {
            {
                GameRegistry.addRecipe(new ItemStack(Blocks.COBBLESTONE),
                        "rrr",
                        "rrr",
                        "rrr",
                        'r', rock.getStack(RockMaterial.NORMAL));

                GameRegistry.addRecipe(new ItemStack(Blocks.STONE, 1, 5),
                        "rrr",
                        "rrr",
                        "rrr",
                        'r', rock.getStack(RockMaterial.ANDESITE));

                GameRegistry.addRecipe(new ItemStack(Blocks.STONE, 1, 3),
                        "rrr",
                        "rrr",
                        "rrr",
                        'r', rock.getStack(RockMaterial.DIORITE));

                GameRegistry.addRecipe(new ItemStack(Blocks.STONE, 1, 1),
                        "rrr",
                        "rrr",
                        "rrr",
                        'r', rock.getStack(RockMaterial.GRANITE));
            }
        }
        */
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ConfigManager.instance.parseChoppingAxes();

        /*
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
                        if (output.getItem() == Items.BREAD)
                        {
                            recipes.remove(r);
                            removed = true;
                        }
                    }

                    if (!removed) i++;
                }
            }
        }

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
                    if (output.getItem() == Items.STICK)
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
                for (IRecipe r : CraftingManager.REGISTRY)
                {
                    //boolean removed = false;

                    ItemStack output = r.getRecipeOutput();
                    if (output.getCount() > 0 && OreDictionaryHelper.hasOreName(output, "plankWood"))
                    {
                        List<Ingredient> inputs = r.getIngredients();
                        Ingredient logInput = null;

                        for (Ingredient input : inputs)
                        {
                            boolean anyWood = false;
                            for (ItemStack stack : input.getMatchingStacks())
                            {
                                if (OreDictionaryHelper.hasOreName(stack, "logWood"))
                                {
                                    anyWood = true;
                                }
                            }

                            if (!anyWood || logInput != null)
                            {
                                logInput = null;
                                break;
                            }
                            logInput = input;
                        }

                        if (logInput != null)
                        {
                            if (ConfigManager.instance.removePlanksRecipes)
                            {
                                removed = recipes.remove(r);
                            }
                            if (ConfigManager.instance.importPlanksRecipes)
                            {
                                for (ItemStack stack : logInput.getMatchingStacks())
                                {
                                    Choppable.registerRecipe(stack.copy(), output.copy());
                                }
                            }
                        }
                    }

                    //if (!removed) i++;
                }
            }
        }

        */

        if (ConfigManager.instance.enableChopping)
        {
            if (ConfigManager.instance.importPlanksRecipes)
            {
                for (IRecipe r : CraftingManager.REGISTRY)
                {
                    ItemStack output = r.getRecipeOutput();
                    if (output.getCount() > 0 && OreDictionaryHelper.hasOreName(output, "plankWood"))
                    {
                        List<Ingredient> inputs = r.getIngredients();
                        Ingredient logInput = null;

                        for (Ingredient input : inputs)
                        {
                            boolean anyWood = false;
                            for (ItemStack stack : input.getMatchingStacks())
                            {
                                if (OreDictionaryHelper.hasOreName(stack, "logWood"))
                                {
                                    anyWood = true;
                                }
                            }

                            if (!anyWood || logInput != null)
                            {
                                logInput = null;
                                break;
                            }
                            logInput = input;
                        }

                        if (logInput != null)
                        {
                            if (ConfigManager.instance.importPlanksRecipes)
                            {
                                for (ItemStack stack : logInput.getMatchingStacks())
                                {
                                    Choppable.registerRecipe(stack.copy(), output.copy());
                                }
                            }
                        }
                    }
                }
            }

            if (ConfigManager.instance.removeSticksFromPlanks)
            {
                Choppable.registerStockRecipes();
            }
        }
    }

    private static void registerNuggetToIngotRecipe(final String oreIngot, final String oreNugget)
    {
        registerNuggetToIngotRecipe(oreIngot, oreNugget, true);
    }

    private static void registerNuggetToIngotRecipe(final String oreIngot, final String oreNugget, boolean addIngotToNugget)
    {
        ForgeRegistries.RECIPES.register(new ShapedOreRecipe(
                location(oreIngot.replace("ingot", "") + "_from_nugget"),
                ItemStack.EMPTY,
                "nnn", "nnn", "nnn", 'n', oreNugget)
        {
            final NonNullList<ItemStack> matches1 = OreDictionary.getOres(oreIngot);

            {
                setRegistryName(this.group);
            }

            private void findOutput()
            {
                if (this.output.isEmpty())
                {
                    if (matches1.size() > 0)
                    {
                        output = matches1.get(0);
                    }
                }
            }

            @Nonnull
            @Override
            public ItemStack getRecipeOutput()
            {
                findOutput();
                return super.getRecipeOutput();
            }

            @Nonnull
            @Override
            public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1)
            {
                findOutput();
                return super.getCraftingResult(var1);
            }
        });

        if (!addIngotToNugget)
            return;

        ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(
                location(oreIngot.replace("ingot", "") + "_to_nugget"),
                ItemStack.EMPTY, oreIngot)
        {
            final NonNullList<ItemStack> matches2 = OreDictionary.getOres(oreNugget);

            {
                setRegistryName(this.group);
            }

            private void findOutput()
            {
                if (this.output.isEmpty())
                {
                    if (matches2.size() > 0)
                    {
                        output = matches2.get(0).copy();
                        output.setCount(9);
                    }
                }
            }

            @Nonnull
            @Override
            public ItemStack getRecipeOutput()
            {
                findOutput();
                return super.getRecipeOutput();
            }

            @Nonnull
            @Override
            public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1)
            {
                findOutput();
                return super.getCraftingResult(var1);
            }
        });
    }

    private static void addSmeltingNugget(ItemStack stack, String ore)
    {
        List<ItemStack> matches = OreDictionary.getOres(ore);
        if (matches.size() > 0)
        {
            GameRegistry.addSmelting(stack, matches.get(0), 0.1f);
        }
    }

    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }
}
