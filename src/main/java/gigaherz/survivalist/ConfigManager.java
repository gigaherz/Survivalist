package gigaherz.survivalist;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigManager
{
    public static ConfigManager instance;

    private final Configuration config;
    public final Property scrapingProp;

    public static void loadConfig(Configuration configuration)
    {
        instance = new ConfigManager(configuration);
    }

    public ConfigManager(Configuration configuration)
    {
        config = configuration;
        scrapingProp = config.get("Ids", "EnchantmentScraping", 0);
        config.load();
    }
}
