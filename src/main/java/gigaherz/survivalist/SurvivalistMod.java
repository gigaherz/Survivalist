package gigaherz.survivalist;

import gigaherz.survivalist.api.ChoppingRecipe;
import gigaherz.survivalist.api.DryingRecipe;
import gigaherz.survivalist.misc.FibersEventHandling;
import gigaherz.survivalist.misc.StringEventHandling;
import gigaherz.survivalist.rack.DryingRackBakedModel;
import gigaherz.survivalist.rack.DryingRackContainer;
import gigaherz.survivalist.rack.DryingRackScreen;
import gigaherz.survivalist.rocks.RockEntity;
import gigaherz.survivalist.rocks.RocksEventHandling;
import gigaherz.survivalist.sawmill.gui.SawmillContainer;
import gigaherz.survivalist.sawmill.gui.SawmillScreen;
import gigaherz.survivalist.scraping.ItemBreakingTracker;
import gigaherz.survivalist.scraping.ScrapingEnchantment;
import gigaherz.survivalist.scraping.ScrapingMessage;
import gigaherz.survivalist.slime.SlimeMerger;
import gigaherz.survivalist.torchfire.TorchFireEventHandling;
import gigaherz.survivalist.util.MiniReg;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(SurvivalistMod.MODID)
public class SurvivalistMod
{
    public static final String MODID = "survivalist";

    public static SurvivalistMod instance;

    public static Logger logger = LogManager.getLogger(MODID);

    static final MiniReg HELPER = new MiniReg(SurvivalistMod.MODID);

    public static RegistryObject<SoundEvent> SOUND_SHLOP = HELPER.soundEvent("shlop", () -> new SoundEvent(SurvivalistMod.location("mob.slime.merge"))).defer();

    public static RegistryObject<ScrapingEnchantment> SCRAPING = HELPER.enchantment("scraping", ScrapingEnchantment::new).defer();

    public static RegistryObject<EntityType<RockEntity>> THROWN_ROCK = HELPER.<RockEntity>entityType("thrown_rock", RockEntity::new, EntityClassification.MISC)
            .size(.5f, .5f)
            .immuneToFire()
            .setTrackingRange(80)
            .setUpdateInterval(3)
            .setShouldReceiveVelocityUpdates(true)
            .setCustomClientFactory((packet,world) -> new RockEntity(world))
            .defer();

    public static final String CHANNEL = "main";
    private static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, CHANNEL))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public SurvivalistMod()
    {
        instance = this;

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        HELPER.subscribeEvents(modEventBus);
        SurvivalistBlocks.HELPER.subscribeEvents(modEventBus);
        SurvivalistItems.HELPER.subscribeEvents(modEventBus);
        SurvivalistTileEntityTypes.HELPER.subscribeEvents(modEventBus);

        modEventBus.addGenericListener(ContainerType.class, this::registerContainers);
        modEventBus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::gatherData);

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigManager.SERVER_SPEC);
    }

    public void gatherData(GatherDataEvent event)
    {
        SurvivalistData.gatherData(event);
    }

    private void registerContainers(RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().registerAll(
                withName(new ContainerType<>(DryingRackContainer::new), "rack"),
                withName(new ContainerType<>(SawmillContainer::new), "sawmill")
        );
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        CraftingHelper.register(ConfigurationCondition.Serializer.INSTANCE);
        CraftingHelper.register(ConfigToggledIngredientSerializer.NAME, ConfigToggledIngredientSerializer.INSTANCE);

        event.getRegistry().registerAll(
                new DryingRecipe.Serializer().setRegistryName("drying"),
                new ChoppingRecipe.Serializer().setRegistryName("chopping")
        );
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
        SurvivalistRecipeBookCategories.instance();

        TorchFireEventHandling.register();

        ItemBreakingTracker.register();

        RocksEventHandling.register();

        FibersEventHandling.register();

        StringEventHandling.register();

        SlimeMerger.register();

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
        channel.registerMessage(messageNumber++, ScrapingMessage.class, ScrapingMessage::encode, ScrapingMessage::new, ScrapingMessage::handle);
        logger.debug("Final message number: " + messageNumber);

    }

    public void clientSetup(FMLClientSetupEvent event)
    {
        ScreenManager.registerFactory(DryingRackContainer.TYPE, DryingRackScreen::new);
        ScreenManager.registerFactory(SawmillContainer.TYPE, SawmillScreen::new);

        ModelLoaderRegistry.registerLoader(location("rack"), DryingRackBakedModel.ModelLoader.INSTANCE);

        //RenderTypeLookup.setRenderLayer(SurvivalistBlocks.RACK.get(), (layer) -> layer == RenderType.getSolid() || layer == RenderType.getCutout());
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

    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }
}
