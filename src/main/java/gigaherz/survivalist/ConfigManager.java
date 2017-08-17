package gigaherz.survivalist;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2DoubleArrayMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigManager
{
    public static ConfigManager instance;

    public final boolean removeSticksFromPlanks;
    public final boolean enableRocks;
    public final boolean replaceStoneDrops;
    public final boolean replaceIronOreDrops;
    public final boolean replaceGoldOreDrops;
    public final boolean replaceModOreDrops;
    public final boolean replacePoorOreDrops;
    public final boolean cobbleRequiresClay;
    public final boolean enableScraping;
    public final boolean enableToolScraping;
    public final boolean enableArmorScraping;
    public final boolean enableMeatRotting;
    public final boolean enableJerky;
    public final boolean enableRottenDrying;
    public final boolean enableMeatDrying;
    public final boolean enableLeatherTanning;
    public final boolean enableTorchFire;
    public final boolean enableBread;
    public final boolean removeVanillaBread;
    public final boolean enableSaddleCrafting;
    public final boolean importPlanksRecipes;
    public final boolean removePlanksRecipes;
    public final float choppingDegradeChance;
    public final float choppingExhaustion;
    public final boolean enableStringCrafting;
    public final boolean dropFibersFromGrass;
    public final boolean dropStringFromSheep;
    public final boolean mergeSlimes;
    public final List<Pair<ItemStack, Integer>> customChoppingAxes = Lists.newArrayList();
    private final ConfigCategory customAxes;
    private final ConfigCategory axeMultipliers;
    public final Int2DoubleMap axeLevelMap = new Int2DoubleArrayMap();

    public static void loadConfig(Configuration configuration)
    {
        instance = new ConfigManager(configuration);
    }

    public final Configuration config;

    private static class AllProperties extends ArrayList<Property>
    {
        public Property push(Property p)
        {
            this.add(p);
            return p;
        }
    }

    public ConfigManager(Configuration configuration)
    {
        this.config = configuration;

        AllProperties props = new AllProperties();

        configuration.addCustomCategoryComment("Sticks", "Settings for stick crafting");
        Property p_removeSticksFromPlanks = props.push(configuration.get("Sticks", "RemoveSticksFromPlanksRecipes", true));

        configuration.addCustomCategoryComment("Rocks", "Settings for rock and ore rock drops");
        Property p_enableRocks = props.push(configuration.get("Rocks", "Enable", true));
        Property p_replaceStoneDrops = props.push(configuration.get("Rocks", "ReplaceStoneDrops", true));
        Property p_replaceIronOreDrops = props.push(configuration.get("Rocks", "ReplaceIronOreDrops", true));
        Property p_replaceGoldOreDrops = props.push(configuration.get("Rocks", "ReplaceGoldOreDrops", true));
        Property p_replaceModOreDrops = props.push(configuration.get("Rocks", "ReplaceModOreDrops", true));
        Property p_replacePoorOreDrops = props.push(configuration.get("Rocks", "ReplacePoorOreDrops", true));
        Property p_cobbleRequiresClay = props.push(configuration.get("Rocks", "CobbleRequiresClay", true));

        configuration.addCustomCategoryComment("Scraping", "Settings for the Scraping feature and enchant");
        Property p_enableScraping = props.push(configuration.get("Scraping", "Enable", true));
        Property p_enableToolScraping = props.push(configuration.get("Scraping", "EnableToolScraping", true));
        Property p_enableArmorScraping = props.push(configuration.get("Scraping", "EnableArmorScraping", true));

        configuration.addCustomCategoryComment("DryingRack", "Settings for the drying rack block");
        Property p_enableMeatRotting = props.push(configuration.get("DryingRack", "EnableMeatRotting", true));
        Property p_enableRottenDrying = props.push(configuration.get("DryingRack", "EnableRottenDrying", true));
        Property p_enableJerky = props.push(configuration.get("DryingRack", "EnableJerky", true));
        Property p_enableMeatDrying = props.push(configuration.get("DryingRack", "EnableMeatDrying", true));
        Property p_enableLeatherTanning = props.push(configuration.get("DryingRack", "EnableLeatherTanning", true));
        Property p_enableSaddleCrafting = props.push(configuration.get("DryingRack", "EnableSaddleCrafting", true));

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

        configuration.load();

        removeSticksFromPlanks = p_removeSticksFromPlanks.getBoolean();
        enableRocks = p_enableRocks.getBoolean();
        replaceStoneDrops = p_replaceStoneDrops.getBoolean();
        replaceIronOreDrops = p_replaceIronOreDrops.getBoolean();
        replaceGoldOreDrops = p_replaceGoldOreDrops.getBoolean();
        replaceModOreDrops = p_replaceModOreDrops.getBoolean();
        replacePoorOreDrops = p_replacePoorOreDrops.getBoolean();
        cobbleRequiresClay = p_cobbleRequiresClay.getBoolean();
        enableScraping = p_enableScraping.getBoolean();
        enableToolScraping = p_enableToolScraping.getBoolean();
        enableArmorScraping = p_enableArmorScraping.getBoolean();
        enableJerky = p_enableJerky.getBoolean();
        enableMeatRotting = p_enableMeatRotting.getBoolean();
        enableRottenDrying = p_enableRottenDrying.getBoolean();
        enableMeatDrying = p_enableMeatDrying.getBoolean();
        enableLeatherTanning = p_enableLeatherTanning.getBoolean();
        enableSaddleCrafting = p_enableSaddleCrafting.getBoolean();
        enableTorchFire = p_enableTorchFire.getBoolean();
        enableBread = p_enableBread.getBoolean();
        removeVanillaBread = p_removeVanillaBread.getBoolean();
        importPlanksRecipes = p_importPlanksRecipes.getBoolean();
        removePlanksRecipes = p_removePlanksRecipes.getBoolean();
        choppingDegradeChance = (float) p_choppingDegradeChance.getDouble();
        choppingExhaustion = (float) p_choppingExhaustion.getDouble();
        enableStringCrafting = p_enableStringCrafting.getBoolean();
        dropFibersFromGrass = p_dropfibersFromGrass.getBoolean();
        dropStringFromSheep = p_dropStringsFromSheep.getBoolean();
        mergeSlimes = p_mergeSlimes.getBoolean();

        boolean anyDefault = !props.stream().allMatch(Property::wasRead) || !hasList;

        if (anyDefault)
            configuration.save();
    }

    private final Pattern itemRegex = Pattern.compile("^(?<item>[a-zA-Z-0-9_]+:[a-zA-Z-0-9_]+)(?:@(?<meta>[0-9]+))?$");

    public void parseChoppingAxes()
    {
        for (Map.Entry<String, Property> entry : this.customAxes.entrySet())
        {
            String key = entry.getKey();
            int level = entry.getValue().getInt(-1);
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

            String metaString = matcher.group("meta");
            int meta = Strings.isNullOrEmpty(metaString) ? 0 : Integer.parseInt(metaString);

            ItemStack stack = new ItemStack(item, 1, meta);

            customChoppingAxes.add(Pair.of(stack, level));
        }
    }

    public int getAxeLevel(ItemStack stack)
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

    public double getAxeLevelMultiplier(int axeLevel)
    {
        if (axeLevelMap.containsKey(axeLevel))
            return axeLevelMap.get(axeLevel);

        double value = 1 + axeLevel;

        if (axeMultipliers != null)
        {
            Property p = axeMultipliers.get("AxeLevel" + axeLevel);
            if (p != null && p.wasRead())
            {
                value = p.getDouble();
            }
        }

        axeLevelMap.put(axeLevel, value);
        return value;
    }
}
