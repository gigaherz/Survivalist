package gigaherz.survivalist;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2DoubleArrayMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            /*builder.comment("Settings for the drying rack block").push("drying_rack");
            removeSticksFromPlanks = builder.define("RemoveSticksFromPlanksRecipes", true);
            builder.pop();*/
/*

        configuration.addCustomCategoryComment("DryingRack", "Settings for the drying rack block");
        Property p_enableMeatRotting = props.push(configuration.get("DryingRack", "EnableMeatRotting", true));
        Property p_enableRottenDrying = props.push(configuration.get("DryingRack", "EnableRottenDrying", true));
        Property p_enableJerky = props.push(configuration.get("DryingRack", "EnableJerky", true));
        Property p_enableMeatDrying = props.push(configuration.get("DryingRack", "EnableMeatDrying", true));
        Property p_enableLeatherTanning = props.push(configuration.get("DryingRack", "EnableLeatherTanning", true));
        Property p_enableSaddleCrafting = props.push(configuration.get("DryingRack", "EnableSaddleCrafting", true));


 */
        }
    }

    public static final boolean removeSticksFromPlanks = true;
    public static final boolean enableRocks = true;
    public static final boolean replaceStoneDrops = true;
    public static final boolean replaceIronOreDrops = true;
    public static final boolean replaceGoldOreDrops = true;
    public static final boolean replaceModOreDrops = true;
    public static final boolean replacePoorOreDrops = true;
    public static final boolean cobbleRequiresClay = true;
    public static final boolean enableScraping = true;
    public static final boolean scrapingIsTreasure = true;
    public static final boolean enableToolScraping = true;
    public static final boolean enableArmorScraping = true;
    public static final boolean enableMeatRotting = true;
    public static final boolean enableJerky = true;
    public static final boolean enableRottenDrying = true;
    public static final boolean enableMeatDrying = true;
    public static final boolean enableLeatherTanning = true;
    public static final boolean enableTorchFire = true;
    public static final boolean enableBread = true;
    public static final boolean removeVanillaBread = true;
    public static final boolean enableSaddleCrafting = true;
    public static final boolean importPlanksRecipes = true;
    public static final boolean removePlanksRecipes = true;
    public static final float choppingDegradeChance = 0.2f;
    public static final float choppingExhaustion = 0.2f;
    public static final float choppingWithEmptyHand = 0.2f;
    public static final boolean enableStringCrafting = true;
    public static final boolean dropFibersFromGrass = true;
    public static final boolean dropStringFromSheep = true;
    public static final boolean mergeSlimes = true;

    public static final List<Pair<ItemStack, Integer>> customChoppingAxes = Lists.newArrayList();
    public static final Int2DoubleMap axeLevelMap = new Int2DoubleArrayMap();

    public static final Map<String,Integer> customAxes = Maps.newHashMap();

    public ConfigManager()
    {
        /*this.config = configuration;

        AllProperties props = new AllProperties();

        configuration.addCustomCategoryComment("TorchFire", "Settings for the torch setting fire to entities");
        Property p_enableTorchFire = props.push(configuration.get("TorchFire", "Enable", true));

        configuration.addCustomCategoryComment("Bread", "Settings for the dough/bread replacements");
        Property p_enableBread = props.push(configuration.get("Bread", "Enable", true));
        Property p_removeVanillaBread = props.push(configuration.get("Bread", "RemoveVanillaBread", true));

        configuration.addCustomCategoryComment("Chopping", "Settings for the chopping block");
        Property p_importPlanksRecipes = props.push(configuration.get("Chopping", "ImportPlanksRecipes", true));
        Property p_removePlanksRecipes = props.push(configuration.get("Chopping", "RemovePlanksRecipes", true));
        Property p_choppingDegradeChance = props.push(configuration.get("Chopping", "DegradeChance", 0.06));
        Property p_choppingExhaustion = props.push(configuration.get("Chopping", "Exhaustion", 0.0025));
        Property p_choppingWithEmptyHand = props.push(configuration.get("Chopping", "EmptyHandFactor", 0.4));
        p_choppingDegradeChance.setComment("The average number of uses before degrading to the next phase will be 1/DegradeChance. Default is 16.67 average uses.");

        configuration.addCustomCategoryComment("Fibres", "Settings for the fibre collection");
        Property p_dropfibersFromGrass = props.push(configuration.get("Fibres", "DropFibresFromGrass", true));
        Property p_dropStringsFromSheep = props.push(configuration.get("Fibres", "DropStringFromSheep", true));
        Property p_enableStringCrafting = props.push(configuration.get("Fibres", "EnableStringCrafting", true));

        configuration.addCustomCategoryComment("Slimes", "Settings for slime merging.");
        Property p_mergeSlimes = props.push(configuration.get("Slimes", "Merge", true));
        p_mergeSlimes.setComment("If enabled, slimes will have new AI rules to feel attracted to other slimes, and if 4 slimes of the same size are nearby they will merge into a slime of higher size.");

        boolean hasList = configuration.hasCategory("CustomAxes");
        configuration.addCustomCategoryComment("CustomAxes", "Custom Chopping Block axe values for when mods have axes that don't declare themselves to be axes.");
        customAxes = configuration.getCategory("CustomAxes");

        configuration.addCustomCategoryComment("AxeMultipliers",
                "Allows customizing the multiplier for each axe level. By default this is 'baseOutput * (1+axeLevel)'.\n" +
                        "To customize an axeLevel, add a line like 'D:AxeLevel1=2.0' or 'D:AxeLevel5=3.0' without the quotes.\n" +
                        "Levels that are not defined will continue using their default value."
        );
        axeMultipliers = configuration.getCategory("AxeMultipliers");

         */
    }

    private static final Pattern itemRegex = Pattern.compile("^(?<item>[a-zA-Z-0-9_]+:[a-zA-Z-0-9_]+)$");

    public static void parseChoppingAxes()
    {
        for (Map.Entry<String, Integer> entry : customAxes.entrySet())
        {
            String key = entry.getKey();
            int level = entry.getValue();
            if (level < 0)
                continue;

            Matcher matcher = itemRegex.matcher(key);

            if (!matcher.matches())
            {
                Survivalist.logger.warn("Could not parse chopping item " + key);
                continue;
            }

            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(matcher.group("item")));
            if (item == null)
            {
                Survivalist.logger.warn("Could not parse chopping item " + key);
                continue;
            }

            ItemStack stack = new ItemStack(item, 1);

            customChoppingAxes.add(Pair.of(stack, level));
        }
    }

    public static int getAxeLevel(ItemStack stack)
    {
        for (Pair<ItemStack, Integer> customAxe : customChoppingAxes)
        {
            if (ItemStack.areItemsEqualIgnoreDurability(customAxe.getLeft(), stack))
            {
                return customAxe.getRight();
            }
        }
        return -1;
    }

    public static double getAxeLevelMultiplier(int axeLevel)
    {
        if (axeLevelMap.containsKey(axeLevel))
            return axeLevelMap.get(axeLevel);

        double value = 1 + axeLevel;

        /*if (axeMultipliers != null)
        {
            Property p = axeMultipliers.get("AxeLevel" + axeLevel);
            if (p != null && p.wasRead())
            {
                value = p.getDouble();
            }
        }*/

        axeLevelMap.put(axeLevel, value);
        return value;
    }
}
