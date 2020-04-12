package gigaherz.survivalist;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.sun.xml.internal.ws.util.StreamUtils;
import gigaherz.survivalist.api.ChoppingRecipe;
import gigaherz.survivalist.api.DryingRecipe;
import gigaherz.survivalist.fibers.AddFibersModifier;
import gigaherz.survivalist.misc.BlockTagCondition;
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
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
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
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.packs.DelegatableResourcePack;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
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

        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);

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
        LootConditionManager.registerCondition(new BlockTagCondition.Serializer());
        event.getRegistry().register(
                new AddFibersModifier.Serializer().setRegistryName(location("plant_fibers"))
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

    public void serverStarting(FMLServerAboutToStartEvent event)
    {
        event.getServer().getResourcePacks().addPackFinder(new IPackFinder()
        {
            @Override
            public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> map, ResourcePackInfo.IFactory<T> infoFactory)
            {
                map.computeIfAbsent("survivalist_vanilla_replacements", (id) ->
                        ResourcePackInfo.createResourcePack(id, false, () -> new SurvivalistVanillaReplacements(id), infoFactory, ResourcePackInfo.Priority.TOP));
            }
        });
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


    private static class SurvivalistVanillaReplacements extends DelegatableResourcePack
    {
        private final String id;
        private final ModFile modFile;

        public SurvivalistVanillaReplacements(String id)
        {
            super(new File(id));
            this.id = id;
            this.modFile = ModList.get().getModFileById(MODID).getFile();
        }

        @Override
        public String getName()
        {
            return id;
        }

        @Override
        public InputStream getInputStream(String name) throws IOException
        {
            final Path path = modFile.getLocator().findPath(modFile, "vanilla_replacements", name);
            return Files.newInputStream(path, StandardOpenOption.READ);
        }

        @Override
        public boolean resourceExists(String name)
        {
            return Files.exists(modFile.getLocator().findPath(modFile, "vanilla_replacements", name));
        }

        @Override
        public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepth, Predicate<String> filter)
        {
            try
            {
                Path root = modFile.getLocator().findPath(modFile, "vanilla_replacements", type.getDirectoryName()).toAbsolutePath();
                Path inputPath = root.getFileSystem().getPath(pathIn);

                List<ResourceLocation> resourceLocationList = Files.walk(root).
                        map(path -> root.relativize(path.toAbsolutePath())).
                        filter(path -> path.getNameCount() > 1 && path.getNameCount() - 1 <= maxDepth). // Make sure the depth is within bounds, ignoring domain
                        filter(path -> !path.toString().endsWith(".mcmeta")). // Ignore .mcmeta files
                        filter(path -> path.subpath(1, path.getNameCount()).startsWith(inputPath)). // Make sure the target path is inside this one (again ignoring domain)
                        filter(path -> filter.test(path.getFileName().toString())). // Test the file name against the predicate
                        // Finally we need to form the RL, so use the first name as the domain, and the rest as the path
                        // It is VERY IMPORTANT that we do not rely on Path.toString as this is inconsistent between operating systems
                        // Join the path names ourselves to force forward slashes
                                map(path -> new ResourceLocation(path.getName(0).toString(), Joiner.on('/').join(path.subpath(1, Math.min(maxDepth, path.getNameCount()))))).
                                collect(Collectors.toList());
                return resourceLocationList;
            }
            catch (IOException e)
            {
                return Collections.emptyList();
            }
        }

        @Override
        public Set<String> getResourceNamespaces(ResourcePackType type)
        {
            try
            {
                Path root = modFile.getLocator().findPath(modFile, "vanilla_replacements", type.getDirectoryName()).toAbsolutePath();
                return Files.walk(root, 1)
                        .map(path -> root.relativize(path.toAbsolutePath()))
                        .filter(path -> path.getNameCount() > 0) // skip the root entry
                        .map(p -> p.toString().replaceAll("/$", "")) // remove the trailing slash, if present
                        .filter(s -> !s.isEmpty()) //filter empty strings, otherwise empty strings default to minecraft in ResourceLocations
                        .collect(Collectors.toSet());
            }
            catch (IOException e)
            {
                return Collections.emptySet();
            }
        }

        public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException
        {
            if (location.getPath().startsWith("lang/"))
            {
                return super.getResourceStream(ResourcePackType.CLIENT_RESOURCES, location);
            }
            else
            {
                return super.getResourceStream(type, location);
            }
        }

        public boolean resourceExists(ResourcePackType type, ResourceLocation location)
        {
            if (location.getPath().startsWith("lang/"))
            {
                return super.resourceExists(ResourcePackType.CLIENT_RESOURCES, location);
            }
            else
            {
                return super.resourceExists(type, location);
            }
        }

        @Override
        public void close()
        {

        }
    }
}
