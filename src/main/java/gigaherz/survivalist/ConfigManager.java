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

    public final boolean sticksFromLeaves;
    public final boolean sticksFromSaplings;
    public final boolean removeSticksFromPlanks;
    public final boolean enableRocks;
    public final boolean replaceStoneDrops;
    public final boolean replaceIronOreDrops;
    public final boolean replaceGoldOreDrops;
    public final boolean replaceModOreDrops;
    public final boolean cobbleRequiresClay;
    public final boolean enableScraping;
    public final boolean enableToolScraping;
    public final boolean enableArmorScraping;
    public final boolean enableDryingRack;
    public final boolean enableMeatRotting;
    public final boolean enableJerky;
    public final boolean enableRottenDrying;
    public final boolean enableMeatDrying;
    public final boolean enableLeatherTanning;
    public final boolean enableNuggets;
    public final boolean enableNuggetRecipes;
    public final boolean enableChainmailCrafting;
    public final boolean enableTorchFire;
    public final boolean enableBread;
    public final boolean removeVanillaBread;
    public final boolean enableSaddleCrafting;
    public final boolean enableChopping;
    public final boolean importPlanksRecipes;
    public final boolean removePlanksRecipes;
    public final float choppingDegradeChance;
    public final float choppingExhaustion;
    public final boolean enableHatchet;
    public final boolean enablePick;
    public final boolean enableSpade;
    public final boolean enableFibres;
    public final boolean enableStringCrafting;
    public final boolean dropFibersFromGrass;
    public final boolean dropStringFromSheep;
    public final List<Pair<ItemStack, Integer>> customChoppingAxes = Lists.newArrayList();
    private final ConfigCategory customAxes;

    public static void loadConfig(Configuration configuration)
    {
        instance = new ConfigManager(configuration);
    }

    public ConfigManager(Configuration configuration)
    {
        configuration.addCustomCategoryComment("Sticks", "Settings for stick crafting");
        Property p_sticksFromLeaves = configuration.get("Sticks", "CraftSticksFromLeaves", true);
        Property p_sticksFromSaplings = configuration.get("Sticks", "CraftSticksFromSaplings", true);
        Property p_removeSticksFromPlanks = configuration.get("Sticks", "RemoveSticksFromPlanksRecipes", true);

        configuration.addCustomCategoryComment("Rocks", "Settings for rock and ore rock drops");
        Property p_enableRocks = configuration.get("Rocks", "Enables", true);
        Property p_replaceStoneDrops = configuration.get("Rocks", "ReplaceStoneDrops", true);
        Property p_replaceIronOreDrops = configuration.get("Rocks", "ReplaceIronOreDrops", true);
        Property p_replaceGoldOreDrops = configuration.get("Rocks", "ReplaceGoldOreDrops", true);
        Property p_replaceModOreDrops = configuration.get("Rocks", "ReplaceModOreDrops", true);
        Property p_cobbleRequiresClay = configuration.get("Rocks", "CobbleRequiresClay", true);

        configuration.addCustomCategoryComment("Scraping", "Settings for the Scraping feature and enchant");
        Property p_enableScraping = configuration.get("Scraping", "Enable", true);
        Property p_enableToolScraping = configuration.get("Scraping", "EnableToolScraping", true);
        Property p_enableArmorScraping = configuration.get("Scraping", "EnableArmorScraping", true);

        configuration.addCustomCategoryComment("DryingRack", "Settings for the drying rack block");
        Property p_enableDryingRack = configuration.get("DryingRack", "Enable", true);
        Property p_enableMeatRotting = configuration.get("DryingRack", "EnableMeatRotting", true);
        Property p_enableRottenDrying = configuration.get("DryingRack", "EnableRottenDrying", true);
        Property p_enableJerky = configuration.get("DryingRack", "EnableJerky", true);
        Property p_enableMeatDrying = configuration.get("DryingRack", "EnableMeatDrying", true);
        Property p_enableLeatherTanning = configuration.get("DryingRack", "EnableLeatherTanning", true);
        Property p_enableSaddleCrafting = configuration.get("DryingRack", "EnableSaddleCrafting", true);

        configuration.addCustomCategoryComment("Nuggets", "Settings for enabling custom nuggets");
        Property p_enableNuggets = configuration.get("Nuggets", "Enable", true);
        Property p_enableNuggetRecipes = configuration.get("Nuggets", "EnableNuggetRecipes", true);
        p_enableNuggetRecipes.setComment("Independent of Nuggets being enabled, allows adding recipes also when different mods don't play together.");

        configuration.addCustomCategoryComment("Chainmail", "Settings for the chainmail crafting");
        Property p_enableChainmailCrafting = configuration.get("Chainmail", "EnableChainmailCrafting", true);

        configuration.addCustomCategoryComment("TorchFire", "Settings for the torch setting fire to entities");
        Property p_enableTorchFire = configuration.get("TorchFire", "Enable", true);

        configuration.addCustomCategoryComment("Bread", "Settings for the dough/bread replacements");
        Property p_enableBread = configuration.get("Bread", "Enable", true);
        Property p_removeVanillaBread = configuration.get("Bread", "RemoveVanillaBread", true);

        configuration.addCustomCategoryComment("Chopping", "Settings for the chopping block");
        Property p_enableChopping = configuration.get("Chopping", "Enable", true);
        Property p_importPlanksRecipes = configuration.get("Chopping", "ImportPlanksRecipes", true);
        Property p_removePlanksRecipes = configuration.get("Chopping", "RemovePlanksRecipes", true);
        Property p_choppingDegradeChance = configuration.get("Chopping", "DegradeChance", 0.06);
        Property p_choppingExhaustion = configuration.get("Chopping", "DegradeChance", 0.0025);
        Property p_enableStringCrafting = configuration.get("Chopping", "EnableStringCraftingFromWool", true);
        p_choppingDegradeChance.setComment("The average number of uses before degrading to the next phase will be 1/DegradeChance. Default is 16.67 average uses.");

        configuration.addCustomCategoryComment("Tools", "Settings for the tools");
        Property p_enableHatchet = configuration.get("Tools", "EnableHatchet", true);
        Property p_enablePick = configuration.get("Tools", "EnablePick", true);
        Property p_enableSpade = configuration.get("Tools", "EnableSpade", true);

        configuration.addCustomCategoryComment("Fibres", "Settings for the fibre collection");
        Property p_enableFibres = configuration.get("Fibres", "EnableFibres", true);
        Property p_dropfibersFromGrass = configuration.get("Fibres", "DropFibresFromGrass", true);
        Property p_dropStringsFromSheep = configuration.get("Fibres", "DropFibresFromGrass", true);

        boolean hasList = configuration.hasCategory("CustomAxes");
        configuration.addCustomCategoryComment("CustomAxes", "Custom Chopping Block axe values for when mods have axes that don't declare themselves to be axes.");
        customAxes = configuration.getCategory("CustomAxes");

        configuration.load();

        sticksFromLeaves = p_sticksFromLeaves.getBoolean();
        sticksFromSaplings = p_sticksFromSaplings.getBoolean();
        removeSticksFromPlanks = p_removeSticksFromPlanks.getBoolean();
        enableRocks = p_enableRocks.getBoolean();
        replaceStoneDrops = p_replaceStoneDrops.getBoolean();
        replaceIronOreDrops = p_replaceIronOreDrops.getBoolean();
        replaceGoldOreDrops = p_replaceGoldOreDrops.getBoolean();
        replaceModOreDrops = p_replaceModOreDrops.getBoolean();
        cobbleRequiresClay = p_cobbleRequiresClay.getBoolean();
        enableScraping = p_enableScraping.getBoolean();
        enableToolScraping = p_enableToolScraping.getBoolean();
        enableArmorScraping = p_enableArmorScraping.getBoolean();
        enableDryingRack = p_enableDryingRack.getBoolean();
        enableJerky = p_enableJerky.getBoolean();
        enableMeatRotting = p_enableMeatRotting.getBoolean();
        enableRottenDrying = p_enableRottenDrying.getBoolean();
        enableMeatDrying = p_enableMeatDrying.getBoolean();
        enableLeatherTanning = p_enableLeatherTanning.getBoolean();
        enableSaddleCrafting = p_enableSaddleCrafting.getBoolean();
        enableNuggets = p_enableNuggets.getBoolean();
        enableNuggetRecipes = p_enableNuggetRecipes.getBoolean();
        enableChainmailCrafting = p_enableChainmailCrafting.getBoolean();
        enableTorchFire = p_enableTorchFire.getBoolean();
        enableBread = p_enableBread.getBoolean();
        removeVanillaBread = p_removeVanillaBread.getBoolean();
        enableChopping = p_enableChopping.getBoolean();
        importPlanksRecipes = p_importPlanksRecipes.getBoolean();
        removePlanksRecipes = p_removePlanksRecipes.getBoolean();
        choppingDegradeChance = (float) p_choppingDegradeChance.getDouble();
        choppingExhaustion = (float) p_choppingExhaustion.getDouble();
        enableHatchet = p_enableHatchet.getBoolean();
        enablePick = p_enablePick.getBoolean();
        enableSpade = p_enableSpade.getBoolean();
        enableFibres = p_enableFibres.getBoolean();
        enableStringCrafting = p_enableStringCrafting.getBoolean();
        dropFibersFromGrass = p_dropfibersFromGrass.getBoolean();
        dropStringFromSheep = p_dropStringsFromSheep.getBoolean();

        boolean anyDefault = !p_enableDryingRack.wasRead();
        anyDefault = anyDefault || !p_sticksFromSaplings.wasRead();
        anyDefault = anyDefault || !p_removeSticksFromPlanks.wasRead();
        anyDefault = anyDefault || !p_enableRocks.wasRead();
        anyDefault = anyDefault || !p_replaceStoneDrops.wasRead();
        anyDefault = anyDefault || !p_replaceIronOreDrops.wasRead();
        anyDefault = anyDefault || !p_replaceGoldOreDrops.wasRead();
        anyDefault = anyDefault || !p_replaceModOreDrops.wasRead();
        anyDefault = anyDefault || !p_cobbleRequiresClay.wasRead();
        anyDefault = anyDefault || !p_enableScraping.wasRead();
        anyDefault = anyDefault || !p_enableToolScraping.wasRead();
        anyDefault = anyDefault || !p_enableArmorScraping.wasRead();
        anyDefault = anyDefault || !p_enableDryingRack.wasRead();
        anyDefault = anyDefault || !p_enableMeatRotting.wasRead();
        anyDefault = anyDefault || !p_enableJerky.wasRead();
        anyDefault = anyDefault || !p_enableRottenDrying.wasRead();
        anyDefault = anyDefault || !p_enableMeatDrying.wasRead();
        anyDefault = anyDefault || !p_enableLeatherTanning.wasRead();
        anyDefault = anyDefault || !p_enableSaddleCrafting.wasRead();
        anyDefault = anyDefault || !p_enableNuggets.wasRead();
        anyDefault = anyDefault || !p_enableNuggetRecipes.wasRead();
        anyDefault = anyDefault || !p_enableChainmailCrafting.wasRead();
        anyDefault = anyDefault || !p_enableTorchFire.wasRead();
        anyDefault = anyDefault || !p_enableBread.wasRead();
        anyDefault = anyDefault || !p_removeVanillaBread.wasRead();
        anyDefault = anyDefault || !p_enableChopping.wasRead();
        anyDefault = anyDefault || !p_importPlanksRecipes.wasRead();
        anyDefault = anyDefault || !p_removePlanksRecipes.wasRead();
        anyDefault = anyDefault || !p_choppingDegradeChance.wasRead();
        anyDefault = anyDefault || !p_choppingExhaustion.wasRead();
        anyDefault = anyDefault || !p_enableHatchet.wasRead();
        anyDefault = anyDefault || !p_enablePick.wasRead();
        anyDefault = anyDefault || !p_enableSpade.wasRead();
        anyDefault = anyDefault || !p_enableStringCrafting.wasRead();
        anyDefault = anyDefault || !p_dropfibersFromGrass.wasRead();
        anyDefault = anyDefault || !p_enableFibres.wasRead();
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
            if (customAxe.getLeft().getUnlocalizedName().equalsIgnoreCase(stack.getUnlocalizedName())) {
                return customAxe.getRight();
            }
        }
        return -1;
    }
}
