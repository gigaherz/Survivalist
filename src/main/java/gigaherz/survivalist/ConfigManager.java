package gigaherz.survivalist;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.InMemoryFormat;
import it.unimi.dsi.fastutil.ints.Int2DoubleArrayMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigManager
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig
    {
        public final ForgeConfigSpec.BooleanValue enableScraping;
        public final ForgeConfigSpec.BooleanValue scrapingIsTreasure;
        public final ForgeConfigSpec.BooleanValue enableToolScraping;
        public final ForgeConfigSpec.BooleanValue enableArmorScraping;
        public final ForgeConfigSpec.BooleanValue enableTorchFire;
        public final ForgeConfigSpec.DoubleValue choppingDegradeChance;
        public final ForgeConfigSpec.DoubleValue choppingExhaustion;
        public final ForgeConfigSpec.DoubleValue choppingWithEmptyHand;
        public final ForgeConfigSpec.BooleanValue mergeSlimes;
        public final ForgeConfigSpec.ConfigValue<Config> axeLevels;

        ServerConfig(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Settings for the Scraping feature and enchant").push("scraping");
            enableScraping = builder.define("Enable", false);
            scrapingIsTreasure = builder.define("IsTreasureEnchantment", false);
            enableToolScraping = builder.define("EnableToolScraping", true);
            enableArmorScraping = builder.define("EnableArmorScraping", true);
            builder.pop();
            builder.comment("Settings for the torch setting fire to entities").push("torch_fire");
            enableTorchFire = builder.define("Enable", true);
            builder.pop();
            builder.comment("Settings for the chopping block").push("chopping");
            choppingDegradeChance = builder
                    .comment("The average number of uses before degrading to the next phase will be 1/DegradeChance. Default is 16.67 average uses.")
                    .defineInRange("DegradeChance", 0.06, 0, Double.MAX_VALUE);
            choppingExhaustion = builder.defineInRange("Exhaustion", 0.0025, 0, Double.MAX_VALUE);
            choppingWithEmptyHand = builder.defineInRange("EmptyHandFactor", 0.4, 0, Double.MAX_VALUE);
            builder.pop();

            builder.comment("Settings for slime merging").push("slimes");
            mergeSlimes = builder
                    .comment("If enabled, slimes will have new AI rules to feel attracted to other slimes, and if 4 slimes of the same size are nearby they will merge into a slime of higher size.")
                    .define("Merge", true);
            builder.pop();
            axeLevels = builder
                    .comment("Specify any items that should be allowed as chopping tools, and their axe-equivalent level.")
                    .define(Arrays.asList("axe_levels"), () -> Config.of(InMemoryFormat.defaultInstance()), x -> true, Config.class);
        }
    }

    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static
    {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig
    {
        public final ForgeConfigSpec.BooleanValue removeSticksFromPlanks;
        public final ForgeConfigSpec.BooleanValue enableRocks;
        public final ForgeConfigSpec.BooleanValue replaceStoneDrops;
        public final ForgeConfigSpec.BooleanValue replaceIronOreDrops;
        public final ForgeConfigSpec.BooleanValue replaceGoldOreDrops;
        public final ForgeConfigSpec.BooleanValue replaceCopperOreDrops;
        public final ForgeConfigSpec.BooleanValue replaceLeadOreDrops;
        public final ForgeConfigSpec.BooleanValue replaceSilverOreDrops;
        public final ForgeConfigSpec.BooleanValue replaceAluminumOreDrops;
        public final ForgeConfigSpec.BooleanValue replaceTinOreDrops;
        public final ForgeConfigSpec.BooleanValue replacePoorOreDrops;
        public final ForgeConfigSpec.BooleanValue cobbleRequiresClay;
        public final ForgeConfigSpec.BooleanValue enableMeatRotting;
        public final ForgeConfigSpec.BooleanValue enableRottenDrying;
        public final ForgeConfigSpec.BooleanValue enableJerky;
        public final ForgeConfigSpec.BooleanValue enableMeatDrying;
        public final ForgeConfigSpec.BooleanValue enableLeatherTanning;
        public final ForgeConfigSpec.BooleanValue enableSaddleCrafting;
        public final ForgeConfigSpec.BooleanValue enableBread;
        public final ForgeConfigSpec.BooleanValue removeVanillaBread;
        public final ForgeConfigSpec.BooleanValue replacePlanksRecipes;
        public final ForgeConfigSpec.BooleanValue dropStringFromSheep;
        public final ForgeConfigSpec.BooleanValue enableStringCrafting;

        CommonConfig(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Settings for stick crafting").push("sticks");
            removeSticksFromPlanks = builder.define("RemoveSticksFromPlanksRecipes", true);
            builder.pop();
            builder.comment("Settings for rock and ore rock drops").push("rocks");
            enableRocks = builder.define("Enable", true);
            replaceStoneDrops = builder.define("ReplaceStoneDrops", true);
            replaceIronOreDrops = builder.define("ReplaceIronOreDrops", true);
            replaceGoldOreDrops = builder.define("ReplaceGoldOreDrops", true);
            replaceCopperOreDrops = builder.define("ReplaceCopperOreDrops", true);
            replaceLeadOreDrops = builder.define("ReplaceLeadOreDrops", true);
            replaceSilverOreDrops = builder.define("ReplaceSilverOreDrops", true);
            replaceAluminumOreDrops = builder.define("ReplaceAluminumOreDrops", true);
            replaceTinOreDrops = builder.define("ReplaceTinOreDrops", true);
            replacePoorOreDrops = builder.define("ReplacePoorOreDrops", true);
            builder.pop();
            builder.comment("Settings for recipes").push("recipes");
            cobbleRequiresClay = builder.define("CobbleRequiresClay", true);
            builder.pop();
            builder.comment("Settings for the drying rack block").push("drying_rack");
            enableMeatRotting = builder.define("EnableMeatRotting", true);
            enableRottenDrying = builder.define("EnableRottenDrying", true);
            enableJerky = builder.define("EnableJerky", true);
            enableMeatDrying = builder.define("EnableMeatDrying", true);
            enableLeatherTanning = builder.define("EnableLeatherTanning", true);
            enableSaddleCrafting = builder.define("EnableSaddleCrafting", true);
            builder.pop();

            builder.comment("Settings for the dough/bread replacements").push("bread");
            enableBread = builder.define("Enable", true);
            removeVanillaBread = builder.define("RemoveVanillaBread", true);
            builder.pop();

            builder.comment("Settings for the chopping block").push("chopping");
            replacePlanksRecipes = builder
                    .comment("If enabled, the vanilla planks recipes will be disabled, using the log to craft choppingblock instead.",
                             "If disabled, the chopping block uses an alternate recipe, instead of using a single log as an input.")
                    .define("ReplacePlanksRecipes", true);
            builder.pop();

            builder.comment("Settings for the fibre collection").push("fibres");
            dropStringFromSheep = builder.define("DropStringFromSheep", true);
            enableStringCrafting = builder.define("EnableStringCrafting", true);
            builder.pop();
        }
    }

    public static final Int2DoubleMap axeLevelMap = new Int2DoubleArrayMap();

    public static double getAxeLevelMultiplier(int axeLevel)
    {
        if (axeLevelMap.containsKey(axeLevel))
            return axeLevelMap.get(axeLevel);

        double value = 1 + axeLevel;

        axeLevelMap.put(axeLevel, value);

        return value;
    }

    private static final Set<String> warns = new HashSet<>();
    public static boolean getConfigBoolean(String spec, String... path)
    {
        ForgeConfigSpec spec1 = spec.equals("common") ? COMMON_SPEC : SERVER_SPEC;
        ForgeConfigSpec.BooleanValue value = spec1.getValues().get(Arrays.asList(path));
        if (value == null)
        {
            String pathJoined = String.join("/", path);
            if (!warns.contains(pathJoined))
            {
                LOGGER.warn("Config path not found: " + pathJoined + ". This message will only show once per path.");
                warns.add(pathJoined);
            }
            return false;
        }
        return value.get();
    }

    @Mod.EventBusSubscriber(modid = SurvivalistMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Events
    {
        private static final Pattern AXE_LEVEL_ENTRY_PATTERN = Pattern.compile("^AxeLevel(?<level>[0-9]+)$");

        @SubscribeEvent
        public static void modConfig(ModConfig.ModConfigEvent event)
        {
            ModConfig config = event.getConfig();
            if (config.getSpec() != SERVER_SPEC)
                return;

            Config axeLevels = SERVER.axeLevels.get();

            for (Config.Entry e : axeLevels.entrySet())
            {
                Matcher m = AXE_LEVEL_ENTRY_PATTERN.matcher(e.getKey());
                if (m.matches())
                {
                    String numberPart = m.group("level");
                    int levelNumber = Integer.parseInt(numberPart);
                    axeLevelMap.put(levelNumber, e.getIntOrElse(1 + levelNumber));
                }
            }
        }
    }
}