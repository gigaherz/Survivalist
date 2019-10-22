package gigaherz.survivalist;

import com.google.common.collect.Lists;
import gigaherz.survivalist.api.DryingRecipe;
import gigaherz.survivalist.chopblock.ChoppingBlock;
import gigaherz.survivalist.chopblock.ChoppingBlockTileEntity;
import gigaherz.survivalist.misc.FibersEventHandling;
import gigaherz.survivalist.misc.StringEventHandling;
import gigaherz.survivalist.rack.*;
import gigaherz.survivalist.rocks.RockEntity;
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
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.ToolType;
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
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static <T> T toBeInitializedLater() {
        return null;
    }

    @ObjectHolder(MODID)
    public static class Items
    {
        public static final Item TANNED_LEATHER = toBeInitializedLater();
        public static final Item CHAINMAIL = toBeInitializedLater();
        public static final Item JERKY = toBeInitializedLater();
        public static final Item COPPER_NUGGET = toBeInitializedLater();
        public static final Item TIN_NUGGET = toBeInitializedLater();
        public static final Item LEAD_NUGGET = toBeInitializedLater();
        public static final Item SILVER_NUGGET = toBeInitializedLater();
        public static final Item ALUMINUM_NUGGET = toBeInitializedLater();
        public static final ItemRock STONE_ROCK = toBeInitializedLater();
        public static final ItemRock ANDESITE_ROCK = toBeInitializedLater();
        public static final ItemRock DIORITE_ROCK = toBeInitializedLater();
        public static final ItemRock GRANITE_ROCK = toBeInitializedLater();
        public static final ItemRock IRON_ORE_ROCK = toBeInitializedLater();
        public static final ItemRock GOLD_ORE_ROCK = toBeInitializedLater();
        public static final ItemRock COPPER_ORE_ROCK = toBeInitializedLater();
        public static final ItemRock TIN_ORE_ROCK = toBeInitializedLater();
        public static final ItemRock LEAD_ORE_ROCK = toBeInitializedLater();
        public static final ItemRock SILVER_ORE_ROCK = toBeInitializedLater();
        public static final ItemRock ALUMINUM_ORE_ROCK = toBeInitializedLater();
        public static final Item DOUGH = toBeInitializedLater();
        public static final Item ROUND_BREAD = toBeInitializedLater();
        public static final Item HATCHET = toBeInitializedLater();
        public static final Item PICK = toBeInitializedLater();
        public static final Item SPADE = toBeInitializedLater();
        public static final Item PLANT_FIBRES = toBeInitializedLater();
        public static final Item TANNED_HELMET = toBeInitializedLater();
        public static final Item TANNED_CHESTPLATE = toBeInitializedLater();
        public static final Item TANNED_LEGGINGS = toBeInitializedLater();
        public static final Item TANNED_BOOTS = toBeInitializedLater();
    }

    @ObjectHolder("survivalist")
    public static class Blocks {
        public static final Block RACK = toBeInitializedLater();
        public static final Block OAK_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block BIRCH_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block SPRUCE_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block JUNGLE_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block DARK_OAK_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block ACACIA_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block CHIPPED_OAK_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block CHIPPED_BIRCH_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block CHIPPED_SPRUCE_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block CHIPPED_JUNGLE_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block CHIPPED_DARK_OAK_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block CHIPPED_ACACIA_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block DAMAGED_OAK_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block DAMAGED_BIRCH_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block DAMAGED_SPRUCE_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block DAMAGED_JUNGLE_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block DAMAGED_DARK_OAK_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block DAMAGED_ACACIA_CHOPPING_BLOCK = toBeInitializedLater();
        public static final Block SAWMILL = toBeInitializedLater();
    }

    @ObjectHolder(MODID + ":shlop")
    public static SoundEvent SOUND_SHLOP;

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
        modEventBus.addGenericListener(ContainerType.class, this::registerContainers);
        modEventBus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
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
                withName(new BlockSawmill(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.5F).sound(SoundType.STONE)),"sawmill"),

                withName(new ChoppingBlock((() -> Blocks.CHIPPED_OAK_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "oak_chopping_block"),
                withName(new ChoppingBlock((() -> Blocks.DAMAGED_OAK_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "chipped_oak_chopping_block"),
                withName(new ChoppingBlock(null, Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "damaged_oak_chopping_block"),

                withName(new ChoppingBlock((() -> Blocks.CHIPPED_BIRCH_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "birch_chopping_block"),
                withName(new ChoppingBlock((() -> Blocks.DAMAGED_BIRCH_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "chipped_birch_chopping_block"),
                withName(new ChoppingBlock(null, Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "damaged_birch_chopping_block"),

                withName(new ChoppingBlock((() -> Blocks.CHIPPED_SPRUCE_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "spruce_chopping_block"),
                withName(new ChoppingBlock((() -> Blocks.DAMAGED_SPRUCE_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "chipped_spruce_chopping_block"),
                withName(new ChoppingBlock(null, Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "damaged_spruce_chopping_block"),

                withName(new ChoppingBlock((() -> Blocks.CHIPPED_JUNGLE_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "jungle_chopping_block"),
                withName(new ChoppingBlock((() -> Blocks.DAMAGED_JUNGLE_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "chipped_jungle_chopping_block"),
                withName(new ChoppingBlock(null, Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "damaged_jungle_chopping_block"),

                withName(new ChoppingBlock((() -> Blocks.CHIPPED_DARK_OAK_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "dark_oak_chopping_block"),
                withName(new ChoppingBlock((() -> Blocks.DAMAGED_DARK_OAK_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "chipped_dark_oak_chopping_block"),
                withName(new ChoppingBlock(null, Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "damaged_dark_oak_chopping_block"),

                withName(new ChoppingBlock((() -> Blocks.CHIPPED_ACACIA_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "acacia_chopping_block"),
                withName(new ChoppingBlock((() -> Blocks.DAMAGED_ACACIA_CHOPPING_BLOCK.getDefaultState()), Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "chipped_acacia_chopping_block"),
                withName(new ChoppingBlock(null, Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0)), "damaged_acacia_chopping_block")
        );
    }

    private void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                // ItemBlocks
                forBlock(Blocks.RACK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.SAWMILL, new Item.Properties().group(ItemGroup.DECORATIONS)),

                forBlock(Blocks.OAK_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.CHIPPED_OAK_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.DAMAGED_OAK_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),

                forBlock(Blocks.BIRCH_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.CHIPPED_BIRCH_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.DAMAGED_BIRCH_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),

                forBlock(Blocks.SPRUCE_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.CHIPPED_SPRUCE_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.DAMAGED_SPRUCE_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),

                forBlock(Blocks.JUNGLE_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.CHIPPED_JUNGLE_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.DAMAGED_JUNGLE_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),

                forBlock(Blocks.DARK_OAK_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.CHIPPED_DARK_OAK_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.DAMAGED_DARK_OAK_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),

                forBlock(Blocks.ACACIA_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.CHIPPED_ACACIA_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),
                forBlock(Blocks.DAMAGED_ACACIA_CHOPPING_BLOCK, new Item.Properties().group(ItemGroup.DECORATIONS)),

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

                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "stone_rock"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "andesite_rock"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "diorite_rock"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "granite_rock"),

                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "iron_ore_rock"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "gold_ore_rock"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "copper_ore_rock"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "tin_ore_rock"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "lead_ore_rock"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "silver_ore_rock"),
                withName(new ItemRock(new Item.Properties().group(ItemGroup.MISC)), "aluminum_ore_rock"),

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
                withName(TileEntityType.Builder.create(TileRack::new, Blocks.RACK).build(null), "rack"),
                withName(TileEntityType.Builder.create(ChoppingBlockTileEntity::new,
                        Blocks.OAK_CHOPPING_BLOCK,
                        Blocks.BIRCH_CHOPPING_BLOCK,
                        Blocks.SPRUCE_CHOPPING_BLOCK,
                        Blocks.JUNGLE_CHOPPING_BLOCK,
                        Blocks.DARK_OAK_CHOPPING_BLOCK,
                        Blocks.ACACIA_CHOPPING_BLOCK,
                        Blocks.CHIPPED_OAK_CHOPPING_BLOCK,
                        Blocks.CHIPPED_BIRCH_CHOPPING_BLOCK,
                        Blocks.CHIPPED_SPRUCE_CHOPPING_BLOCK,
                        Blocks.CHIPPED_JUNGLE_CHOPPING_BLOCK,
                        Blocks.CHIPPED_DARK_OAK_CHOPPING_BLOCK,
                        Blocks.CHIPPED_ACACIA_CHOPPING_BLOCK,
                        Blocks.DAMAGED_OAK_CHOPPING_BLOCK,
                        Blocks.DAMAGED_BIRCH_CHOPPING_BLOCK,
                        Blocks.DAMAGED_SPRUCE_CHOPPING_BLOCK,
                        Blocks.DAMAGED_JUNGLE_CHOPPING_BLOCK,
                        Blocks.DAMAGED_DARK_OAK_CHOPPING_BLOCK,
                        Blocks.DAMAGED_ACACIA_CHOPPING_BLOCK).build(null), "chopping_block"),
                withName(TileEntityType.Builder.create(TileSawmill::new, Blocks.SAWMILL).build(null), "sawmill")
        );
    }

    private void registerContainers(RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().registerAll(
                withName(new ContainerType<>(ContainerRack::new), "rack"),
                withName(new ContainerType<>(ContainerSawmill::new), "sawmill")
        );
    }

    private void registerSounds(RegistryEvent.Register<SoundEvent> event)
    {
        event.getRegistry().registerAll(
                withName(new SoundEvent(location("mob.slime.merge")), "shlop")
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
        event.getRegistry().registerAll(
                EntityType.Builder.<RockEntity>create(RockEntity::new, EntityClassification.MISC)
                        .size(.5f, .5f)
                        .immuneToFire()
                        .setTrackingRange(80)
                        .setUpdateInterval(3)
                        .setShouldReceiveVelocityUpdates(true)
                        .setCustomClientFactory((packet,world) -> new RockEntity(RockEntity.TYPE, world))
                    .build("survivalist:thrown_rock").setRegistryName("thrown_rock")
        );
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        event.getRegistry().register(new DryingRecipe.Serializer().setRegistryName("drying"));
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
        OBJLoader.INSTANCE.addDomain(MODID);
        ModelLoaderRegistry.registerLoader(new RackBakedModel.ModelLoader());
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        ConfigManager.parseChoppingAxes();
    }

    private static <R extends T, T extends IForgeRegistryEntry<T>> R withName(R obj, ResourceLocation name)
    {
        obj.setRegistryName(name);
        return obj;
    }

    private static <R extends T, T extends IForgeRegistryEntry<T>> R withName(R obj, String name)
    {
        return withName(obj, new ResourceLocation(MODID, name));
    }

    private static BlockItem forBlock(Block block, Item.Properties properties)
    {
        return withName(new BlockItem(block, properties), block.getRegistryName());
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
