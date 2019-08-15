package gigaherz.survivalist;

import com.google.common.collect.Lists;
import gigaherz.survivalist.api.Choppable;
import gigaherz.survivalist.api.Dryable;
import gigaherz.survivalist.chopblock.BlockChopping;
import gigaherz.survivalist.chopblock.TileChopping;
import gigaherz.survivalist.misc.DummyRecipe;
import gigaherz.survivalist.armor.ItemTannedArmor;
import gigaherz.survivalist.misc.FibersEventHandling;
import gigaherz.survivalist.misc.OreDictionaryHelper;
import gigaherz.survivalist.misc.StringEventHandling;
import gigaherz.survivalist.network.UpdateFields;
import gigaherz.survivalist.rack.BlockRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.rocks.*;
import gigaherz.survivalist.sawmill.BlockSawmill;
import gigaherz.survivalist.sawmill.TileSawmill;
import gigaherz.survivalist.scraping.EnchantmentScraping;
import gigaherz.survivalist.scraping.ItemBreakingTracker;
import gigaherz.survivalist.scraping.MessageScraping;
import gigaherz.survivalist.slime.SlimeMerger;
import gigaherz.survivalist.torchfire.TorchFireEventHandling;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
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
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
@Mod(modid = Survivalist.MODID, version = Survivalist.VERSION,
        acceptedMinecraftVersions = "[1.12.0,1.13.0)",
        dependencies = "after:forestry")
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

    @GameRegistry.ObjectHolder(MODID + ":scraping")
    public static EnchantmentScraping scraping;

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    private static <T> T sneakyNull() {
        return null;
    }

    @GameRegistry.ObjectHolder(MODID)
    public static class Items
    {
        public static final Item tanned_leather = sneakyNull();
        public static final Item chainmail = sneakyNull();
        public static final Item jerky = sneakyNull();
        public static final ItemNugget nugget = sneakyNull();
        public static final ItemRock rock = sneakyNull();
        public static final ItemOreRock rock_ore = sneakyNull();
        public static final Item dough = sneakyNull();
        public static final Item round_bread = sneakyNull();
        public static final Item hatchet = sneakyNull();
        public static final Item pick = sneakyNull();
        public static final Item spade = sneakyNull();
        public static final Item plant_fibres = sneakyNull();
        public static final Item tanned_helmet = sneakyNull();
        public static final Item tanned_chestplate = sneakyNull();
        public static final Item tanned_leggings = sneakyNull();
        public static final Item tanned_boots = sneakyNull();
    }

    @GameRegistry.ObjectHolder(MODID + ":rack")
    public static Block rack;
    @GameRegistry.ObjectHolder(MODID + ":chopping_block")
    public static Block chopping_block;
    @GameRegistry.ObjectHolder(MODID + ":chopping_block2")
    public static Block chopping_block2;
    @GameRegistry.ObjectHolder(MODID + ":sawmill")
    public static Block sawmill;

    @GameRegistry.ObjectHolder(MODID + ":shlop")
    public static SoundEvent shlop;

    public static final ItemArmor.ArmorMaterial TANNED_LEATHER =
            EnumHelper.addArmorMaterial("tanned_leather", MODID + ":tanned_leather", 12,
                    new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1);

    public static final ItemTool.ToolMaterial TOOL_FLINT =
            EnumHelper.addToolMaterial("flint", 1, 150, 5.0F, 1.5F, 5);

    public static SimpleNetworkWrapper channel;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                withName(new BlockRack(), "rack"),
                withName(new BlockChopping.OldLog(), "chopping_block"),
                withName(new BlockChopping.NewLog(),"chopping_block2", "chopping_block"),
                withName(new BlockSawmill(),"sawmill")
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                // ItemBlocks
                forBlock(rack),
                forBlock(chopping_block),
                forBlock(chopping_block2),
                forBlock(sawmill),

                // Items
                withName(new Item(),"chainmail").setCreativeTab(CreativeTabs.MATERIALS),
                withName(new Item(), "tanned_leather").setCreativeTab(CreativeTabs.MATERIALS),
                withName(new ItemTannedArmor(TANNED_LEATHER, 0, EntityEquipmentSlot.HEAD), "tanned_helmet"),
                withName(new ItemTannedArmor(TANNED_LEATHER, 0, EntityEquipmentSlot.CHEST), "tanned_chestplate"),
                withName(new ItemTannedArmor(TANNED_LEATHER, 0, EntityEquipmentSlot.LEGS), "tanned_leggings"),
                withName(new ItemTannedArmor(TANNED_LEATHER, 0, EntityEquipmentSlot.FEET), "tanned_boots"),
                withName(new ItemFood(4, 1, true), "jerky"),
                withName(new ItemNugget(), "nugget"),
                withName(new ItemRock(), "rock"),
                withName(new ItemOreRock(), "rock_ore"),
                withName(new ItemSurvivalistBread(5, false), "dough"),
                withName(new ItemSurvivalistBread(8, false), "round_bread"),
                withName(new ItemAxe(TOOL_FLINT, 8.0F, -3.1F){}.setCreativeTab(CreativeTabs.TOOLS), "hatchet"),
                withName(new ItemPickaxe(TOOL_FLINT){}.setCreativeTab(CreativeTabs.TOOLS), "pick"),
                withName(new ItemSpade(TOOL_FLINT).setCreativeTab(CreativeTabs.TOOLS), "spade"),
                withName(new Item(),"plant_fibres").setCreativeTab(CreativeTabs.MATERIALS)
        );

        GameRegistry.registerTileEntity(TileRack.class, rack.getRegistryName());
        GameRegistry.registerTileEntity(TileChopping.class, chopping_block.getRegistryName());
        GameRegistry.registerTileEntity(TileSawmill.class, sawmill.getRegistryName());
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
    {
        event.getRegistry().registerAll(
                new SoundEvent(location("mob.slime.merge")).setRegistryName(location("shlop"))
        );
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event)
    {
        event.getRegistry().registerAll(
                withName(new EnchantmentScraping(), "scraping")
        );
    }

    private static void registerOredictNames()
    {
        OreDictionary.registerOre("materialLeather", Items.tanned_leather);
        OreDictionary.registerOre("materialTannedLeather", Items.tanned_leather);
        OreDictionary.registerOre("materialHardenedLeather", Items.tanned_leather);

        OreDictionary.registerOre("nuggetIron", Items.nugget.getStack(OreMaterial.IRON));
        OreDictionary.registerOre("nuggetCopper", Items.nugget.getStack(OreMaterial.COPPER));
        OreDictionary.registerOre("nuggetTin", Items.nugget.getStack(OreMaterial.TIN));
        OreDictionary.registerOre("nuggetLead", Items.nugget.getStack(OreMaterial.LEAD));
        OreDictionary.registerOre("nuggetSilver", Items.nugget.getStack(OreMaterial.SILVER));
        OreDictionary.registerOre("nuggetAluminum", Items.nugget.getStack(OreMaterial.ALUMINUM));
        OreDictionary.registerOre("nuggetAluminium", Items.nugget.getStack(OreMaterial.ALUMINUM));

        OreDictionary.registerOre("rockOreIron", Items.rock_ore.getStack(OreMaterial.IRON));
        OreDictionary.registerOre("rockOreGold", Items.rock_ore.getStack(OreMaterial.GOLD));
        OreDictionary.registerOre("rockOreCopper", Items.rock_ore.getStack(OreMaterial.COPPER));
        OreDictionary.registerOre("rockOreTin", Items.rock_ore.getStack(OreMaterial.TIN));
        OreDictionary.registerOre("rockOreLead", Items.rock_ore.getStack(OreMaterial.LEAD));
        OreDictionary.registerOre("rockOreSilver", Items.rock_ore.getStack(OreMaterial.SILVER));
        OreDictionary.registerOre("rockOreAluminum", Items.rock_ore.getStack(OreMaterial.ALUMINUM));
        OreDictionary.registerOre("rockOreAluminium", Items.rock_ore.getStack(OreMaterial.ALUMINUM));

        OreDictionary.registerOre("oreNuggetIron", Items.rock_ore.getStack(OreMaterial.IRON));
        OreDictionary.registerOre("oreNuggetGold", Items.rock_ore.getStack(OreMaterial.GOLD));
        OreDictionary.registerOre("oreNuggetCopper", Items.rock_ore.getStack(OreMaterial.COPPER));
        OreDictionary.registerOre("oreNuggetTin", Items.rock_ore.getStack(OreMaterial.TIN));
        OreDictionary.registerOre("oreNuggetLead", Items.rock_ore.getStack(OreMaterial.LEAD));
        OreDictionary.registerOre("oreNuggetSilver", Items.rock_ore.getStack(OreMaterial.SILVER));
        OreDictionary.registerOre("oreNuggetAluminium", Items.rock_ore.getStack(OreMaterial.ALUMINUM));
        OreDictionary.registerOre("oreNuggetAluminium", Items.rock_ore.getStack(OreMaterial.ALUMINUM));

        OreDictionary.registerOre("rock", Items.rock.getStack(RockMaterial.NORMAL));
        OreDictionary.registerOre("rock", Items.rock.getStack(RockMaterial.ANDESITE));
        OreDictionary.registerOre("rock", Items.rock.getStack(RockMaterial.DIORITE));
        OreDictionary.registerOre("rock", Items.rock.getStack(RockMaterial.GRANITE));
        OreDictionary.registerOre("rockAndesite", Items.rock.getStack(RockMaterial.ANDESITE));
        OreDictionary.registerOre("rockDiorite", Items.rock.getStack(RockMaterial.DIORITE));
        OreDictionary.registerOre("rockGranite", Items.rock.getStack(RockMaterial.GRANITE));
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        registerOredictNames();

        Dryable.registerStockRecipes();
        Choppable.registerStockRecipes();

        replaceVanillaRecipes();
    }

    private void registerNetwork()
    {
        logger.info("Registering network channel...");

        channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

        int messageNumber = 0;
        channel.registerMessage(MessageScraping.Handler.class, MessageScraping.class, messageNumber++, Side.CLIENT);
        channel.registerMessage(UpdateFields.Handler.class, UpdateFields.class, messageNumber++, Side.CLIENT);
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

        if (ConfigManager.instance.mergeSlimes)
        {
            SlimeMerger.register();
        }

        registerNetwork();

        proxy.preInit();

        if (Loader.isModLoaded("crafttweaker"))
        {
            try
            {
                Class.forName("gigaherz.survivalist.integration.CraftTweakerPlugin").getMethod("init").invoke(null);
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
        EntityRegistry.registerModEntity(location("thrown_rock"), EntityRock.class, "ThrownRock", entityId++, this, 80, 3, true);
        logger.debug("Last used id: %i", entityId);

        TOOL_FLINT.setRepairItem(new ItemStack(net.minecraft.init.Items.FLINT));

        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.IRON), "nuggetIron");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.GOLD), "nuggetGold");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.COPPER), "nuggetCopper");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.TIN), "nuggetTin");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.LEAD), "nuggetLead");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.SILVER), "nuggetSilver");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.ALUMINUM), "nuggetAluminum");

        GameRegistry.addSmelting(Items.dough, new ItemStack(Items.round_bread), 0);
    }

    private static void addSmeltingNugget(ItemStack stack, String ore)
    {
        List<ItemStack> matches = OreDictionary.getOres(ore);
        if (matches.size() > 0)
        {
            GameRegistry.addSmelting(stack, matches.get(0), 0.1f);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ConfigManager.instance.parseChoppingAxes();
    }

    @SubscribeEvent
    public void missingMapping(@Nonnull final RegistryEvent.MissingMappings<Block> event)
    {
        event.getAllMappings().forEach((mapping) -> {
            if (mapping.key.getNamespace().equals(MODID))
            {
                final Block newBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MODID, mapping.key.getPath()));
                if (newBlock != null)
                {
                    mapping.remap(newBlock);
                }
            }
        });
    }

    private static void replaceVanillaRecipes()
    {
        ForgeRegistry<IRecipe> recipeRegistry = (ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES;
        ArrayList<IRecipe> recipes = Lists.newArrayList(recipeRegistry.getValuesCollection());

        logger.warn("#################################");
        logger.warn("## Removing vanilla recipes if enabled in settings, 'Dangerous alternative' messages are expected and not a bug...");

        if (ConfigManager.instance.enableBread)
        {
            if (ConfigManager.instance.removeVanillaBread)
            {
                for (IRecipe r : recipes)
                {
                    ItemStack output = r.getRecipeOutput();
                    if (output.getItem() == net.minecraft.init.Items.BREAD)
                    {
                        recipeRegistry.remove(r.getRegistryName());
                        recipeRegistry.register(DummyRecipe.from(r));
                    }
                }
            }
        }

        if (ConfigManager.instance.removeSticksFromPlanks)
        {
            for (IRecipe r : recipes)
            {
                ItemStack output = r.getRecipeOutput();
                int ore = OreDictionary.getOreID("plankWood");
                if (output.getItem() == net.minecraft.init.Items.STICK)
                {
                    boolean isPlanksInput = r.getIngredients().stream().allMatch(ingredient -> Arrays.stream(ingredient.getMatchingStacks()).anyMatch(input -> ArrayUtils.contains(OreDictionary.getOreIDs(input), ore)));
                    if (isPlanksInput)
                    {
                        recipeRegistry.remove(r.getRegistryName());
                        recipeRegistry.register(DummyRecipe.from(r));
                    }
                }
            }
        }

        if (ConfigManager.instance.importPlanksRecipes || ConfigManager.instance.removePlanksRecipes)
        {
            for (IRecipe r : recipes)
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
                        if (ConfigManager.instance.removePlanksRecipes)
                        {
                            recipeRegistry.remove(r.getRegistryName());
                            recipeRegistry.register(DummyRecipe.from(r));
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
            }
        }

        logger.warn("## Vanilla recipes removed.");
    }

    private static Item withName(Item item, String name)
    {
        return item.setRegistryName(name).setTranslationKey(MODID + "." + name);
    }

    private static Block withName(Block block, String name)
    {
        return block.setRegistryName(name).setTranslationKey(MODID + "." + name);
    }

    private static Block withName(Block block, String name, String translationBaseName)
    {
        return block.setRegistryName(name).setTranslationKey(MODID + "." + translationBaseName);
    }

    private static Enchantment withName(Enchantment block, String name)
    {
        return block.setRegistryName(name).setName(MODID + "." + name);
    }

    private static Item forBlock(Block block)
    {
        return new ItemBlock(block).setRegistryName(block.getRegistryName());
    }


    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }

    private static class ItemSurvivalistBread extends ItemFood
    {

        public ItemSurvivalistBread(int amount, boolean isWolfFood)
        {
            super(amount, isWolfFood);
        }

        @Override
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
        {
            if (ConfigManager.instance.enableBread)
            {
                super.getSubItems(tab, subItems);
            }
        }
    }
}
