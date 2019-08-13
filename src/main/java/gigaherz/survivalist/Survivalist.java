package gigaherz.survivalist;

import gigaherz.survivalist.chopblock.ChoppingBlock;
import gigaherz.survivalist.chopblock.ChoppingBlockTileEntity;
import gigaherz.survivalist.misc.FibersEventHandling;
import gigaherz.survivalist.misc.StringEventHandling;
import gigaherz.survivalist.rack.BlockRack;
import gigaherz.survivalist.rack.ContainerRack;
import gigaherz.survivalist.rack.GuiRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.rocks.ItemRock;
import gigaherz.survivalist.rocks.RocksEventHandling;
import gigaherz.survivalist.sawmill.BlockSawmill;
import gigaherz.survivalist.sawmill.TileSawmill;
import gigaherz.survivalist.sawmill.gui.ContainerSawmill;
import gigaherz.survivalist.sawmill.gui.GuiSawmill;
import gigaherz.survivalist.scraping.EnchantmentScraping;
import gigaherz.survivalist.scraping.ItemBreakingTracker;
import gigaherz.survivalist.scraping.MessageScraping;
import gigaherz.survivalist.torchfire.TorchFireEventHandling;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
@Mod(Survivalist.MODID)
public class Survivalist
{
    public static final String MODID = "survivalist";

    public static Survivalist instance;

    public static Logger logger = LogManager.getLogger(MODID);

    @ObjectHolder(MODID + ":scraping")
    public static EnchantmentScraping scraping;

    public static final String CHANNEL = MODID;
    private static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, CHANNEL))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    private static <T> T sneakyNull() {
        return null;
    }

    @ObjectHolder(MODID)
    public static class Items
    {
        public static final Item tanned_leather = sneakyNull();
        public static final Item chainmail = sneakyNull();
        public static final Item jerky = sneakyNull();
        public static final Item copper_nugget = sneakyNull();
        public static final Item tin_nugget = sneakyNull();
        public static final Item lead_nugget = sneakyNull();
        public static final Item silver_nugget = sneakyNull();
        public static final Item aluminum_nugget = sneakyNull();
        public static final ItemRock rock_stone = sneakyNull();
        public static final ItemRock rock_andesite = sneakyNull();
        public static final ItemRock rock_diorite = sneakyNull();
        public static final ItemRock rock_granite = sneakyNull();
        public static final ItemRock rock_iron_ore = sneakyNull();
        public static final ItemRock rock_gold_ore = sneakyNull();
        public static final ItemRock rock_copper_ore = sneakyNull();
        public static final ItemRock rock_tin_ore = sneakyNull();
        public static final ItemRock rock_lead_ore = sneakyNull();
        public static final ItemRock rock_silver_ore = sneakyNull();
        public static final ItemRock rock_aluminum_ore = sneakyNull();
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

    @ObjectHolder("survivalist")
    public static class Blocks {
        public static final Block rack = sneakyNull();
        public static final Block oak_chopping_block = sneakyNull();
        public static final Block birch_chopping_block = sneakyNull();
        public static final Block spruce_chopping_block = sneakyNull();
        public static final Block jungle_chopping_block = sneakyNull();
        public static final Block dark_oak_chopping_block = sneakyNull();
        public static final Block acacia_chopping_block = sneakyNull();
        public static final Block sawmill = sneakyNull();
    }

    @ObjectHolder(MODID + ":shlop")
    public static SoundEvent shlop;

    public static final IArmorMaterial TANNED_LEATHER = new IArmorMaterial()
    {
        private final int[] armors = new int[]{1, 2, 3, 1};
        private final Tag<Item> leather_tag = new ItemTags.Wrapper(new ResourceLocation("survivalist:items/tanned_leather"));
        private final Ingredient leather_tag_ingredient = Ingredient.fromTag(leather_tag);

        @Override
        public int getDurability(EquipmentSlotType slotIn)
        {
            return 12;
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn)
        {
            return 0;
        }

        @Override
        public int getEnchantability()
        {
            return 15;
        }

        @Override
        public SoundEvent getSoundEvent()
        {
            return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairMaterial()
        {
            return leather_tag_ingredient;
        }

        @Override
        public String getName()
        {
            return "tanned_leather";
        }

        @Override
        public float getToughness()
        {
            return 1;
        }
    };

    public static final IItemTier TOOL_FLINT = new IItemTier()
    {
        private final Tag<Item> flint_tag = new ItemTags.Wrapper(new ResourceLocation("forge:items/flint"));
        private final Ingredient flint_tag_ingredient = Ingredient.fromTag(flint_tag);

        @Override
        public int getMaxUses()
        {
            return 150;
        }

        @Override
        public float getEfficiency()
        {
            return 5.0f;
        }

        @Override
        public float getAttackDamage()
        {
            return 1.5f;
        }

        @Override
        public int getHarvestLevel()
        {
            return 1;
        }

        @Override
        public int getEnchantability()
        {
            return 5;
        }

        @Override
        public Ingredient getRepairMaterial()
        {
            return flint_tag_ingredient;
        }
    };

    public static final Food food_jerky = new Food.Builder().hunger(4).saturation(1).meat().build();
    public static final Food food_dough = new Food.Builder().hunger(4).saturation(1).meat().build();
    public static final Food food_bread = new Food.Builder().hunger(8).saturation(3).meat().build();

    //public static SimpleNetworkWrapper channel;

    public Survivalist()
    {
        instance = this;

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addGenericListener(Item.class, this::registerItems);
        modEventBus.addGenericListener(Block.class, this::registerBlocks);
        modEventBus.addGenericListener(TileEntityType.class, this::registerTileEntities);
        modEventBus.addGenericListener(Enchantment.class, this::registerEnchantments);
        modEventBus.addGenericListener(SoundEvent.class, this::registerSounds);
        modEventBus.addGenericListener(EntityType.class, this::registerEntities);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::loadComplete);
        //modEventBus.addListener(this::modConfig);



        //MinecraftForge.EVENT_BUS.addListener(this::anvilChange);

        //modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigData.SERVER_SPEC);
        //modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigData.CLIENT_SPEC);
    }

    private void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                withName(new BlockRack(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(1.0f)), "rack"),
                withName(new ChoppingBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "oak_chopping_block"),
                withName(new ChoppingBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "birch_chopping_block"),
                withName(new ChoppingBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "spruce_chopping_block"),
                withName(new ChoppingBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "jungle_chopping_block"),
                withName(new ChoppingBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "dark_oak_chopping_block"),
                withName(new ChoppingBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "acacia_chopping_block"),
                withName(new BlockSawmill(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.5F).sound(SoundType.STONE)),"sawmill")
        );
    }

    private void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                // ItemBlocks
                forBlock(Blocks.rack, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.oak_chopping_block, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.birch_chopping_block, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.spruce_chopping_block, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.jungle_chopping_block, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.dark_oak_chopping_block, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.acacia_chopping_block, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.sawmill, new Item.Properties().group(ItemGroup.DECORATIONS)),

                // Items
                withName(new Item(new Item.Properties().group(ItemGroup.MATERIALS)),"chainmail"),
                withName(new Item(new Item.Properties().group(ItemGroup.MATERIALS)),"tanned_leather"),

                withName(new Item(new Item.Properties().group(ItemGroup.MATERIALS).food(food_jerky)), "jerky"),
                withName(new Item(new Item.Properties().group(ItemGroup.MATERIALS).food(food_dough)), "dough"),
                withName(new Item(new Item.Properties().group(ItemGroup.MATERIALS).food(food_bread)), "round_bread"),


                withName(new Item(new Item.Properties().group(ItemGroup.MISC)), "copper_nugget"),
                withName(new Item(new Item.Properties().group(ItemGroup.MISC)), "tin_nugget"),
                withName(new Item(new Item.Properties().group(ItemGroup.MISC)), "lead_nugget"),
                withName(new Item(new Item.Properties().group(ItemGroup.MISC)), "silver_nugget"),
                withName(new Item(new Item.Properties().group(ItemGroup.MISC)), "aluminum_nugget"),

                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_stone"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_andesite"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_diorite"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_granite"),

                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_iron_ore"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_gold_ore"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_copper_ore"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_tin_ore"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_lead_ore"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_silver_ore"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "rock_aluminum_ore"),

                withName(new AxeItem(TOOL_FLINT, 8.0F, -3.1F, new Item.Properties().group(ItemGroup.TOOLS)){}, "hatchet"),
                withName(new PickaxeItem(TOOL_FLINT, 4, -2.6F, new Item.Properties().group(ItemGroup.TOOLS)){}, "pick"),
                withName(new ShovelItem(TOOL_FLINT, 3, -2.1F, new Item.Properties().group(ItemGroup.TOOLS)), "spade"),

                withName(new ArmorItem(TANNED_LEATHER, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.TOOLS)), "tanned_helmet"),
                withName(new ArmorItem(TANNED_LEATHER, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.TOOLS)), "tanned_chestplate"),
                withName(new ArmorItem(TANNED_LEATHER, EquipmentSlotType.LEGS, new Item.Properties().group(ItemGroup.TOOLS)), "tanned_leggings"),
                withName(new ArmorItem(TANNED_LEATHER, EquipmentSlotType.FEET, new Item.Properties().group(ItemGroup.TOOLS)), "tanned_boots"),

                withName(new Item(new Item.Properties().group(ItemGroup.MATERIALS)),"plant_fibres")
        );
    }

    private void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(TileRack::new).build(null).setRegistryName("rack"),
                TileEntityType.Builder.create(ChoppingBlockTileEntity::new).build(null).setRegistryName("chopping_block"),
                TileEntityType.Builder.create(TileSawmill::new).build(null).setRegistryName("sawmill")
        );
    }

    private void registerContainers(RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().registerAll(
                new ContainerType<>(ContainerRack::new),
                new ContainerType<>(ContainerSawmill::new)
        );
    }

    private void registerSounds(RegistryEvent.Register<SoundEvent> event)
    {
        event.getRegistry().registerAll(
                new SoundEvent(location("mob.slime.merge")).setRegistryName(location("shlop"))
        );
    }

    private void registerEnchantments(RegistryEvent.Register<Enchantment> event)
    {
        event.getRegistry().registerAll(
                withName(new EnchantmentScraping(), "scraping")
        );
    }

    private void registerEntities(RegistryEvent.Register<EntityType<?>> event)
    {
        //EntityRegistry.registerModEntity(location("thrown_rock"), EntityRock.class, "ThrownRock", entityId++, this, 80, 3, true);
    }

    private static void registerOredictNames()
    {
        /*OreDictionary.registerOre("materialLeather", Items.tanned_leather);
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
        OreDictionary.registerOre("rockGranite", Items.rock.getStack(RockMaterial.GRANITE));*/
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
        //ConfigManager.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));

        if (ConfigManager.enableTorchFire)
        {
            TorchFireEventHandling.register();
        }

        if (ConfigManager.enableScraping)
        {
            ItemBreakingTracker.register();
        }

        if (ConfigManager.enableRocks)
        {
            RocksEventHandling.register();
        }

        if (ConfigManager.dropFibersFromGrass)
        {
            FibersEventHandling.register();
        }

        if (ConfigManager.dropStringFromSheep)
        {
            StringEventHandling.register();
        }

        /*if (ConfigManager.instance.mergeSlimes)
        {
            SlimeMerger.register();
        }*/

        /*if (Loader.isModLoaded("crafttweaker"))
        {
            try
            {
                Class.forName("gigaherz.survivalist.integration.CraftTweakerPlugin").getMethod("init").invoke(null);
            }
            catch (Exception e)
            {
                throw new ReportedException(new CrashReport("Error initializing minetweaker integration", e));
            }
        }*/

        logger.info("Registering network channel...");

        int messageNumber = 0;
        channel.registerMessage(messageNumber++, MessageScraping.class, MessageScraping::encode, MessageScraping::new, MessageScraping::handle);
        logger.debug("Final message number: " + messageNumber);
        /*
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.IRON), "nuggetIron");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.GOLD), "nuggetGold");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.COPPER), "nuggetCopper");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.TIN), "nuggetTin");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.LEAD), "nuggetLead");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.SILVER), "nuggetSilver");
        addSmeltingNugget(Items.rock_ore.getStack(OreMaterial.ALUMINUM), "nuggetAluminum");

        GameRegistry.addSmelting(Items.dough, new ItemStack(Items.round_bread), 0);
         */
    }

    public void clientSetup(FMLClientSetupEvent event)
    {
        ScreenManager.registerFactory(ContainerRack.TYPE, GuiRack::new);
        ScreenManager.registerFactory(ContainerSawmill.TYPE, GuiSawmill::new);
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        ConfigManager.parseChoppingAxes();
    }

    private static Item withName(Item item, String name)
    {
        return item.setRegistryName(name);
    }

    private static Block withName(Block block, String name)
    {
        return block.setRegistryName(name);
    }

    private static Enchantment withName(Enchantment block, String name)
    {
        return block.setRegistryName(name);
    }

    private static Item forBlock(Block block, Item.Properties properties)
    {
        return new BlockItem(block, properties).setRegistryName(block.getRegistryName());
    }

    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }

    private static class ItemSurvivalist extends Item
    {
        private final Supplier<Boolean> isEnabled;

        public ItemSurvivalist(Item.Properties properties, Supplier<Boolean> isEnabled)
        {
            super(properties);
            this.isEnabled = isEnabled;
        }

        @Override
        public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems)
        {
            if (isEnabled.get()) // ConfigManager.instance.enableBread
            {
                super.fillItemGroup(tab, subItems);
            }
        }
    }
}
