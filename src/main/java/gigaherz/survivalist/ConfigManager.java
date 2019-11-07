package gigaherz.survivalist;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.InMemoryFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.Int2DoubleArrayMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigManager
{
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
        public final ForgeConfigSpec.BooleanValue removeSticksFromPlanks;
        public final ForgeConfigSpec.BooleanValue enableRocks;
        public final ForgeConfigSpec.BooleanValue replaceStoneDrops;
        public final ForgeConfigSpec.BooleanValue replaceIronOreDrops;
        public final ForgeConfigSpec.BooleanValue replaceGoldOreDrops;
        public final ForgeConfigSpec.BooleanValue replaceModOreDrops;
        public final ForgeConfigSpec.BooleanValue replacePoorOreDrops;
        public final ForgeConfigSpec.BooleanValue cobbleRequiresClay;
        public final ForgeConfigSpec.BooleanValue enableScraping;
        public final ForgeConfigSpec.BooleanValue scrapingIsTreasure;
        public final ForgeConfigSpec.BooleanValue enableToolScraping;
        public final ForgeConfigSpec.BooleanValue enableArmorScraping;
        public final ForgeConfigSpec.BooleanValue enableMeatRotting;
        public final ForgeConfigSpec.BooleanValue enableRottenDrying;
        public final ForgeConfigSpec.BooleanValue enableJerky;
        public final ForgeConfigSpec.BooleanValue enableMeatDrying;
        public final ForgeConfigSpec.BooleanValue enableLeatherTanning;
        public final ForgeConfigSpec.BooleanValue enableSaddleCrafting;
        public final ForgeConfigSpec.BooleanValue enableTorchFire;
        public final ForgeConfigSpec.BooleanValue enableBread;
        public final ForgeConfigSpec.BooleanValue removeVanillaBread;
        public final ForgeConfigSpec.BooleanValue importPlanksRecipes;
        public final ForgeConfigSpec.BooleanValue removePlanksRecipes;
        public final ForgeConfigSpec.DoubleValue choppingDegradeChance;
        public final ForgeConfigSpec.DoubleValue choppingExhaustion;
        public final ForgeConfigSpec.DoubleValue choppingWithEmptyHand;
        public final ForgeConfigSpec.BooleanValue dropFibersFromGrass;
        public final ForgeConfigSpec.BooleanValue dropStringFromSheep;
        public final ForgeConfigSpec.BooleanValue enableStringCrafting;
        public final ForgeConfigSpec.BooleanValue mergeSlimes;
        public final ForgeConfigSpec.ConfigValue<Config> axeLevels;

        ServerConfig(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Settings for stick crafting").push("sticks");
            removeSticksFromPlanks = builder.define("RemoveSticksFromPlanksRecipes", true);
            builder.pop();
            builder.comment("Settings for rock and ore rock drops").push("rocks");
            enableRocks = builder.define("Enable", true);
            replaceStoneDrops = builder.define("ReplaceStoneDrops", true);
            replaceIronOreDrops = builder.define("ReplaceIronOreDrops", true);
            replaceGoldOreDrops = builder.define("ReplaceGoldOreDrops", true);
            replaceModOreDrops = builder.define("ReplaceModOreDrops", true);
            replacePoorOreDrops = builder.define("ReplacePoorOreDrops", true);
            cobbleRequiresClay = builder.define("CobbleRequiresClay", true);
            builder.pop();
            builder.comment("Settings for the Scraping feature and enchant").push("scraping");
            enableScraping = builder.define("Enable", false);
            scrapingIsTreasure = builder.define("IsTreasureEnchantment", false);
            enableToolScraping = builder.define("EnableToolScraping", true);
            enableArmorScraping = builder.define("EnableArmorScraping", true);
            builder.pop();
            builder.comment("Settings for the drying rack block").push("drying_rack");
            enableMeatRotting = builder.define("EnableMeatRotting", true);
            enableRottenDrying = builder.define("EnableRottenDrying", true);
            enableJerky = builder.define("EnableJerky", true);
            enableMeatDrying = builder.define("EnableMeatDrying", true);
            enableLeatherTanning = builder.define("EnableLeatherTanning", true);
            enableSaddleCrafting = builder.define("EnableSaddleCrafting", true);
            builder.pop();
            builder.comment("Settings for the torch setting fire to entities").push("torch_fire");
            enableTorchFire = builder.define("Enable", true);
            builder.pop();

            builder.comment("Settings for the dough/bread replacements").push("bread");
            enableBread = builder.define("Enable", true);
            removeVanillaBread = builder.define("RemoveVanillaBread", true);
            builder.pop();

            builder.comment("Settings for the chopping block").push("chopping");
            importPlanksRecipes = builder.define("ImportPlanksRecipes", true);
            removePlanksRecipes = builder.define("RemovePlanksRecipes", true);
            choppingDegradeChance = builder
                    .comment("The average number of uses before degrading to the next phase will be 1/DegradeChance. Default is 16.67 average uses.")
                    .defineInRange("DegradeChance", 0.06, 0, Double.MAX_VALUE);
            choppingExhaustion = builder.defineInRange("Exhaustion", 0.0025,0, Double.MAX_VALUE);
            choppingWithEmptyHand = builder.defineInRange("EmptyHandFactor", 0.4,0, Double.MAX_VALUE);
            builder.pop();

            builder.comment("Settings for the fibre collection").push("fibres");
            dropFibersFromGrass = builder.define("DropFibresFromGrass", true);
            dropStringFromSheep = builder.define("DropStringFromSheep", true);
            enableStringCrafting = builder.define("EnableStringCrafting", true);
            builder.pop();

            builder.comment("Settings for slime merging").push("slimes");
            mergeSlimes = builder
                    .comment("If enabled, slimes will have new AI rules to feel attracted to other slimes, and if 4 slimes of the same size are nearby they will merge into a slime of higher size.")
                    .define("Merge", true);
            builder.pop();


            axeLevels = builder
                    .comment("If enabled, slimes will have new AI rules to feel attracted to other slimes, and if 4 slimes of the same size are nearby they will merge into a slime of higher size.")
                    .define(Arrays.asList("axe_levels"), () -> Config.of(InMemoryFormat.defaultInstance()), x -> true, Config.class);

        /*
        configuration.addCustomCategoryComment("AxeMultipliers",
                "Allows customizing the multiplier for each axe level. By default this is 'baseOutput * (1+axeLevel)'.\n" +
                        "To customize an axeLevel, add a line like 'D:AxeLevel1=2.0' or 'D:AxeLevel5=3.0' without the quotes.\n" +
                        "Levels that are not defined will continue using their default value."
        );
        axeMultipliers = configuration.getCategory("AxeMultipliers");

         */
        }

        private class AxeMap
        {
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

    public static boolean getConfigBoolean(String categoryName, String keyName)
    {
        ForgeConfigSpec.BooleanValue value = SERVER_SPEC.getValues().get(Arrays.asList(categoryName, keyName));
        return value.get();
    }

    @Mod.EventBusSubscriber(modid=Survivalist.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
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

            for(Config.Entry e : axeLevels.entrySet())
            {
                Matcher m = AXE_LEVEL_ENTRY_PATTERN.matcher(e.getKey());
                if (m.matches())
                {
                    String numberPart = m.group("level");
                    int levelNumber = Integer.parseInt(numberPart);
                    axeLevelMap.put(levelNumber, e.getIntOrElse(1+levelNumber));
                }
            }
        }
    }
}
