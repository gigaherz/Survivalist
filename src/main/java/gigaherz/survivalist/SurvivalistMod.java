package gigaherz.survivalist;

import com.google.common.base.Joiner;
import gigaherz.survivalist.api.ChoppingRecipe;
import gigaherz.survivalist.api.DryingRecipe;
import gigaherz.survivalist.util.AppendLootTable;
import gigaherz.survivalist.util.MatchBlockCondition;
import gigaherz.survivalist.misc.StringEventHandling;
import gigaherz.survivalist.rack.DryingRackBakedModel;
import gigaherz.survivalist.rack.DryingRackContainer;
import gigaherz.survivalist.rack.DryingRackScreen;
import gigaherz.survivalist.rocks.RockEntity;
import gigaherz.survivalist.sawmill.gui.SawmillContainer;
import gigaherz.survivalist.sawmill.gui.SawmillScreen;
import gigaherz.survivalist.scraping.ItemBreakingTracker;
import gigaherz.survivalist.scraping.ScrapingEnchantment;
import gigaherz.survivalist.scraping.ScrapingMessage;
import gigaherz.survivalist.slime.SlimeMerger;
import gigaherz.survivalist.torchfire.TorchFireEventHandling;
import gigaherz.survivalist.util.RegSitter;
import gigaherz.survivalist.util.ReplaceDrops;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.packs.DelegatableResourcePack;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
@Mod(SurvivalistMod.MODID)
public class SurvivalistMod
{
    public static final String MODID = "survivalist";

    public static SurvivalistMod instance;

    public static Logger LOGGER = LogManager.getLogger(MODID);

    public static final ItemGroup SURVIVALIST_ITEMS = new ItemGroup("survivalist_items")
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(SurvivalistItems.PICK.get());
        }
    };

    static final RegSitter HELPER = new RegSitter(SurvivalistMod.MODID);

    public static RegistryObject<SoundEvent> SOUND_SHLOP = HELPER.soundEvent("shlop", () -> new SoundEvent(SurvivalistMod.location("mob.slime.merge"))).defer();

    public static RegistryObject<ScrapingEnchantment> SCRAPING = HELPER.enchantment("scraping", ScrapingEnchantment::new).defer();

    public static RegistryObject<EntityType<RockEntity>> THROWN_ROCK = HELPER.<RockEntity>entityType("thrown_rock", RockEntity::new, EntityClassification.MISC)
            .size(.5f, .5f)
            .immuneToFire()
            .setTrackingRange(80)
            .setUpdateInterval(3)
            .setShouldReceiveVelocityUpdates(true)
            .setCustomClientFactory((packet, world) -> new RockEntity(world))
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
        modEventBus.addGenericListener(GlobalLootModifierSerializer.class, this::lootModifiers);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::gatherData);

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigManager.SERVER_SPEC);
    }

    // This is its own method because I don't want SurvivalistData loaded all the time, I just need it loaded in the case where the event fires.
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

    private void lootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event)
    {
        MatchBlockCondition.BLOCK_TAG_CONDITION = LootConditionManager.register("survivalist:match_block", new MatchBlockCondition.Serializer());
        event.getRegistry().registerAll(
                new AppendLootTable.Serializer().setRegistryName(location("append_loot")),
                new ReplaceDrops.Serializer().setRegistryName(location("replace_drops"))
        );
    }

    public void commonSetup(FMLCommonSetupEvent event)
    {
        SurvivalistRecipeBookCategories.instance();
        TorchFireEventHandling.register();
        ItemBreakingTracker.register();
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

        LOGGER.info("Registering network channel...");

        int messageNumber = 0;
        channel.registerMessage(messageNumber++, ScrapingMessage.class, ScrapingMessage::encode, ScrapingMessage::new, ScrapingMessage::handle);
        LOGGER.debug("Final message number: " + messageNumber);
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
