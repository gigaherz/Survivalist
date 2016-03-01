package gigaherz.survivalist;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigManager
{
    public static ConfigManager instance;

    private final Configuration config;

    public final Property idScraping;

    public final boolean sticksFromLeaves;
    public final boolean sticksFromSaplings;
    public final boolean removeSticksFromPlanks;
    public final boolean enableRocks;
    public final boolean replaceStoneDrops;
    public final boolean replaceIronOreDrops;
    public final boolean replaceGoldOreDrops;
    public final boolean enableScraping;
    public final boolean enableToolScraping;
    public final boolean enableArmorScraping;
    public final boolean enableDryingRack;
    public final boolean enableMeatRotting;
    public final boolean enableJerky;
    public final boolean enableRottenDrying;
    public final boolean enableMeatDrying;
    public final boolean enableLeatherTanning;
    public final boolean enableIronNugget;
    public final boolean enableChainmailCrafting;

    public static void loadConfig(Configuration configuration)
    {
        instance = new ConfigManager(configuration);
    }

    public ConfigManager(Configuration configuration)
    {
        config = configuration;

        config.addCustomCategoryComment("Ids", "Internal IDs, DO NOT CHANGE");
        idScraping = config.get("Ids", "EnchantmentScraping", -1);

        config.addCustomCategoryComment("Sticks", "Settings for stick crafting");
        Property p_sticksFromLeaves = config.get("Sticks", "CraftSticksFromLeaves", true);
        Property p_sticksFromSaplings = config.get("Sticks", "CraftSticksFromSaplings", true);
        Property p_removeSticksFromPlanks = config.get("Sticks", "RemoveSticksFromPlanksRecipes", true);

        config.addCustomCategoryComment("Rocks", "Settings for rock and ore rock drops");
        Property p_enableRocks = config.get("Rocks", "Enables", true);
        Property p_replaceStoneDrops = config.get("Rocks", "ReplaceStoneDrops", true);
        Property p_replaceIronOreDrops = config.get("Rocks", "ReplaceIronOreDrops", true);
        Property p_replaceGoldOreDrops = config.get("Rocks", "ReplaceGoldOreDrops", true);

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

        config.addCustomCategoryComment("Chainmail", "Settings for the chainmail crafting");
        Property p_enableIronNugget = config.get("Chainmail", "EnableIronNugget", true);
        Property p_enableChainmailCrafting = config.get("Chainmail", "EnableChainmailCrafting", true);

        config.load();

        sticksFromLeaves = p_sticksFromLeaves.getBoolean();
        sticksFromSaplings = p_sticksFromSaplings.getBoolean();
        removeSticksFromPlanks = p_removeSticksFromPlanks.getBoolean();
        enableRocks = p_enableRocks.getBoolean();
        replaceStoneDrops = p_replaceStoneDrops.getBoolean();
        replaceIronOreDrops = p_replaceIronOreDrops.getBoolean();
        replaceGoldOreDrops = p_replaceGoldOreDrops.getBoolean();
        enableScraping = p_enableScraping.getBoolean();
        enableToolScraping = p_enableToolScraping.getBoolean();
        enableArmorScraping = p_enableArmorScraping.getBoolean();
        enableDryingRack = p_enableDryingRack.getBoolean();
        enableJerky = p_enableJerky.getBoolean();
        enableMeatRotting = p_enableMeatRotting.getBoolean();
        enableRottenDrying = p_enableRottenDrying.getBoolean();
        enableMeatDrying = p_enableMeatDrying.getBoolean();
        enableLeatherTanning = p_enableLeatherTanning.getBoolean();
        enableIronNugget = p_enableIronNugget.getBoolean();
        enableChainmailCrafting = p_enableChainmailCrafting.getBoolean();

        boolean anyDefault = !p_enableDryingRack.wasRead();
        anyDefault = anyDefault || !p_sticksFromSaplings.wasRead();
        anyDefault = anyDefault || !p_removeSticksFromPlanks.wasRead();
        anyDefault = anyDefault || !p_enableRocks.wasRead();
        anyDefault = anyDefault || !p_replaceStoneDrops.wasRead();
        anyDefault = anyDefault || !p_replaceIronOreDrops.wasRead();
        anyDefault = anyDefault || !p_replaceGoldOreDrops.wasRead();
        anyDefault = anyDefault || !p_enableScraping.wasRead();
        anyDefault = anyDefault || !p_enableToolScraping.wasRead();
        anyDefault = anyDefault || !p_enableArmorScraping.wasRead();
        anyDefault = anyDefault || !p_enableDryingRack.wasRead();
        anyDefault = anyDefault || !p_enableMeatRotting.wasRead();
        anyDefault = anyDefault || !p_enableJerky.wasRead();
        anyDefault = anyDefault || !p_enableRottenDrying.wasRead();
        anyDefault = anyDefault || !p_enableMeatDrying.wasRead();
        anyDefault = anyDefault || !p_enableLeatherTanning.wasRead();
        anyDefault = anyDefault || !p_enableIronNugget.wasRead();
        anyDefault = anyDefault || !p_enableChainmailCrafting.wasRead();

        if (anyDefault)
            config.save();
    }

    public void save()
    {
        config.save();
    }
}
