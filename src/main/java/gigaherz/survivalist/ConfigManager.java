package gigaherz.survivalist;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigManager
{
    public static ConfigManager instance;

    private final Configuration config;

    public final boolean sticksFromLeaves;
    public final boolean sticksFromSaplings;
    public final boolean removeSticksFromPlanks;
    public final boolean enableRocks;
    public final boolean replaceStoneDrops;
    public final boolean replaceIronOreDrops;
    public final boolean replaceGoldOreDrops;
    public final boolean replaceModOreDrops;
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
    public boolean importPlanksRecipes;
    public boolean removePlanksRecipes;
    public final float choppingDegradeChance;

    public static void loadConfig(Configuration configuration)
    {
        instance = new ConfigManager(configuration);
    }

    public ConfigManager(Configuration configuration)
    {
        config = configuration;

        config.addCustomCategoryComment("Sticks", "Settings for stick crafting");
        Property p_sticksFromLeaves = config.get("Sticks", "CraftSticksFromLeaves", true);
        Property p_sticksFromSaplings = config.get("Sticks", "CraftSticksFromSaplings", true);
        Property p_removeSticksFromPlanks = config.get("Sticks", "RemoveSticksFromPlanksRecipes", true);

        config.addCustomCategoryComment("Rocks", "Settings for rock and ore rock drops");
        Property p_enableRocks = config.get("Rocks", "Enables", true);
        Property p_replaceStoneDrops = config.get("Rocks", "ReplaceStoneDrops", true);
        Property p_replaceIronOreDrops = config.get("Rocks", "ReplaceIronOreDrops", true);
        Property p_replaceGoldOreDrops = config.get("Rocks", "ReplaceGoldOreDrops", true);
        Property p_replaceModOreDrops = config.get("Rocks", "ReplaceModOreDrops", true);

        config.addCustomCategoryComment("Scraping", "Settings for the Scraping feature and enchant");
        Property p_enableScraping = config.get("Scraping", "Enable", true);
        Property p_enableToolScraping = config.get("Scraping", "EnableToolScraping", true);
        Property p_enableArmorScraping = config.get("Scraping", "EnableArmorScraping", true);

        config.addCustomCategoryComment("DryingRack", "Settings for the drying rack block");
        Property p_enableDryingRack = config.get("DryingRack", "Enable", true);
        Property p_enableMeatRotting = config.get("DryingRack", "EnableMeatRotting", true);
        Property p_enableRottenDrying = config.get("DryingRack", "EnableRottenDrying", true);
        Property p_enableJerky = config.get("DryingRack", "EnableJerky", true);
        Property p_enableMeatDrying = config.get("DryingRack", "EnableMeatDrying", true);
        Property p_enableLeatherTanning = config.get("DryingRack", "EnableLeatherTanning", true);
        Property p_enableSaddleCrafting = config.get("DryingRack", "EnableSaddleCrafting", true);

        config.addCustomCategoryComment("Nuggets", "Settings for enabling custom nuggets");
        Property p_enableNuggets = config.get("Nuggets", "Enable", true);
        Property p_enableNuggetRecipes = config.get("Nuggets", "EnableNuggetRecipes", true);
        p_enableNuggetRecipes.setComment("Independent of Nuggets being enabled, allows adding recipes also when different mods don't play together.");

        config.addCustomCategoryComment("Chainmail", "Settings for the chainmail crafting");
        Property p_enableChainmailCrafting = config.get("Chainmail", "EnableChainmailCrafting", true);

        config.addCustomCategoryComment("TorchFire", "Settings for the torch setting fire to entities");
        Property p_enableTorchFire = config.get("TorchFire", "Enable", true);

        config.addCustomCategoryComment("Bread", "Settings for the dough/bread replacements");
        Property p_enableBread = config.get("Bread", "Enable", true);
        Property p_removeVanillaBread = config.get("Bread", "RemoveVanillaBread", true);

        config.addCustomCategoryComment("Chopping", "Settings for the chopping block");
        Property p_enableChopping = config.get("Chopping", "Enable", true);
        Property p_importPlanksRecipes = config.get("Chopping", "ImportPlanksRecipes", true);
        Property p_removePlanksRecipes = config.get("Chopping", "RemovePlanksRecipes", true);
        Property p_choppingDegradeChance = config.get("Chopping", "DegradeChance", 0.06);
        p_choppingDegradeChance.setComment("The average number of uses before degrading to the next phase will be 1/DegradeChance. Default is 16.67 average uses.");

        // Backward compatibility
        Property p_replacePlanksRecipes = config.get("Chopping", "ReplacePlanksRecipes", true);

        config.load();

        sticksFromLeaves = p_sticksFromLeaves.getBoolean();
        sticksFromSaplings = p_sticksFromSaplings.getBoolean();
        removeSticksFromPlanks = p_removeSticksFromPlanks.getBoolean();
        enableRocks = p_enableRocks.getBoolean();
        replaceStoneDrops = p_replaceStoneDrops.getBoolean();
        replaceIronOreDrops = p_replaceIronOreDrops.getBoolean();
        replaceGoldOreDrops = p_replaceGoldOreDrops.getBoolean();
        replaceModOreDrops = p_replaceModOreDrops.getBoolean();
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

        if(p_replacePlanksRecipes.wasRead() && !p_importPlanksRecipes.wasRead() && !p_removePlanksRecipes.wasRead())
        {
            removePlanksRecipes = importPlanksRecipes = p_replacePlanksRecipes.getBoolean();
            config.getCategory("Chopping").remove("ReplacePlanksRecipes");
        }

        boolean anyDefault = !p_enableDryingRack.wasRead();
        anyDefault = anyDefault || !p_sticksFromSaplings.wasRead();
        anyDefault = anyDefault || !p_removeSticksFromPlanks.wasRead();
        anyDefault = anyDefault || !p_enableRocks.wasRead();
        anyDefault = anyDefault || !p_replaceStoneDrops.wasRead();
        anyDefault = anyDefault || !p_replaceIronOreDrops.wasRead();
        anyDefault = anyDefault || !p_replaceGoldOreDrops.wasRead();
        anyDefault = anyDefault || !p_replaceModOreDrops.wasRead();
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

        if (anyDefault)
            config.save();
    }

    public void save()
    {
        config.save();
    }
}
