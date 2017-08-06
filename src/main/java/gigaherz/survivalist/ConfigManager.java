package gigaherz.survivalist;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

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
    public final List<Pair<ItemStack, Integer>> customChoppingAxes = Lists.newArrayList();
    private final ConfigCategory customAxes;

    public static void loadConfig(Configuration configuration)
    {
        instance = new ConfigManager(configuration);
    }

    public final Configuration config;

    public ConfigManager(Configuration configuration)
    {
        this.config = configuration;

        configuration.addCustomCategoryComment("Sticks", "Settings for stick crafting");
        Property p_removeSticksFromPlanks = configuration.get("Sticks", "RemoveSticksFromPlanksRecipes", true);

        configuration.addCustomCategoryComment("Rocks", "Settings for rock and ore rock drops");
        Property p_enableRocks = configuration.get("Rocks", "Enable", true);
        Property p_replaceStoneDrops = configuration.get("Rocks", "ReplaceStoneDrops", true);
        Property p_replaceIronOreDrops = configuration.get("Rocks", "ReplaceIronOreDrops", true);
        Property p_replaceGoldOreDrops = configuration.get("Rocks", "ReplaceGoldOreDrops", true);
        Property p_replaceModOreDrops = configuration.get("Rocks", "ReplaceModOreDrops", true);
        Property p_replacePoorOreDrops = configuration.get("Rocks", "ReplacePoorOreDrops", true);
        Property p_cobbleRequiresClay = configuration.get("Rocks", "CobbleRequiresClay", true);

        configuration.addCustomCategoryComment("Scraping", "Settings for the Scraping feature and enchant");
        Property p_enableScraping = configuration.get("Scraping", "Enable", true);
        Property p_enableToolScraping = configuration.get("Scraping", "EnableToolScraping", true);
        Property p_enableArmorScraping = configuration.get("Scraping", "EnableArmorScraping", true);

        configuration.addCustomCategoryComment("DryingRack", "Settings for the drying rack block");
        Property p_enableMeatRotting = configuration.get("DryingRack", "EnableMeatRotting", true);
        Property p_enableRottenDrying = configuration.get("DryingRack", "EnableRottenDrying", true);
        Property p_enableJerky = configuration.get("DryingRack", "EnableJerky", true);
        Property p_enableMeatDrying = configuration.get("DryingRack", "EnableMeatDrying", true);
        Property p_enableLeatherTanning = configuration.get("DryingRack", "EnableLeatherTanning", true);
        Property p_enableSaddleCrafting = configuration.get("DryingRack", "EnableSaddleCrafting", true);

        configuration.addCustomCategoryComment("TorchFire", "Settings for the torch setting fire to entities");
        Property p_enableTorchFire = configuration.get("TorchFire", "Enable", true);

        configuration.addCustomCategoryComment("Bread", "Settings for the dough/bread replacements");
        Property p_enableBread = configuration.get("Bread", "Enable", true);
        Property p_removeVanillaBread = configuration.get("Bread", "RemoveVanillaBread", true);

        configuration.addCustomCategoryComment("Chopping", "Settings for the chopping block");
        Property p_importPlanksRecipes = configuration.get("Chopping", "ImportPlanksRecipes", true);
        Property p_removePlanksRecipes = configuration.get("Chopping", "RemovePlanksRecipes", true);
        Property p_choppingDegradeChance = configuration.get("Chopping", "DegradeChance", 0.06);
        Property p_choppingExhaustion = configuration.get("Chopping", "Exhaustion", 0.0025);
        p_choppingDegradeChance.setComment("The average number of uses before degrading to the next phase will be 1/DegradeChance. Default is 16.67 average uses.");

        configuration.addCustomCategoryComment("Fibres", "Settings for the fibre collection");
        Property p_dropfibersFromGrass = configuration.get("Fibres", "DropFibresFromGrass", true);
        Property p_dropStringsFromSheep = configuration.get("Fibres", "DropStringFromSheep", true);
        Property p_enableStringCrafting = configuration.get("Fibres", "EnableStringCrafting", true);

        boolean hasList = configuration.hasCategory("CustomAxes");
        configuration.addCustomCategoryComment("CustomAxes", "Custom Chopping Block axe values for when mods have axes that don't declare themselves to be axes.");
        customAxes = configuration.getCategory("CustomAxes");

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

        boolean anyDefault = !p_removeSticksFromPlanks.wasRead();
        anyDefault = anyDefault || !p_enableRocks.wasRead();
        anyDefault = anyDefault || !p_replaceStoneDrops.wasRead();
        anyDefault = anyDefault || !p_replaceIronOreDrops.wasRead();
        anyDefault = anyDefault || !p_replaceGoldOreDrops.wasRead();
        anyDefault = anyDefault || !p_replaceModOreDrops.wasRead();
        anyDefault = anyDefault || !p_replacePoorOreDrops.wasRead();
        anyDefault = anyDefault || !p_cobbleRequiresClay.wasRead();
        anyDefault = anyDefault || !p_enableScraping.wasRead();
        anyDefault = anyDefault || !p_enableToolScraping.wasRead();
        anyDefault = anyDefault || !p_enableArmorScraping.wasRead();
        anyDefault = anyDefault || !p_enableMeatRotting.wasRead();
        anyDefault = anyDefault || !p_enableJerky.wasRead();
        anyDefault = anyDefault || !p_enableRottenDrying.wasRead();
        anyDefault = anyDefault || !p_enableMeatDrying.wasRead();
        anyDefault = anyDefault || !p_enableLeatherTanning.wasRead();
        anyDefault = anyDefault || !p_enableSaddleCrafting.wasRead();
        anyDefault = anyDefault || !p_enableTorchFire.wasRead();
        anyDefault = anyDefault || !p_enableBread.wasRead();
        anyDefault = anyDefault || !p_removeVanillaBread.wasRead();
        anyDefault = anyDefault || !p_importPlanksRecipes.wasRead();
        anyDefault = anyDefault || !p_removePlanksRecipes.wasRead();
        anyDefault = anyDefault || !p_choppingDegradeChance.wasRead();
        anyDefault = anyDefault || !p_choppingExhaustion.wasRead();
        anyDefault = anyDefault || !p_enableStringCrafting.wasRead();
        anyDefault = anyDefault || !p_dropfibersFromGrass.wasRead();
        anyDefault = anyDefault || !p_dropStringsFromSheep.wasRead();
        anyDefault = anyDefault || !hasList;

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
}
