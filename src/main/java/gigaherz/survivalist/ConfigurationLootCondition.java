package gigaherz.survivalist;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.Inverted;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationLootCondition implements ILootCondition
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceLocation NAME = SurvivalistMod.location("configuration");
    public static final LootConditionType TYPE = LootConditionManager.register(NAME.toString(), new ConfigurationLootCondition.Serializer());

    private final String categoryName;
    private final String keyName;

    public static void init()
    {
        LOGGER.debug("ConfigurationLootCondition Init called.");
    }

    public ConfigurationLootCondition(String categoryName, String keyName)
    {
        this.categoryName = categoryName;
        this.keyName = keyName;
    }

    @Override
    public LootConditionType getConditionType()
    {
        return TYPE;
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        return ConfigManager.getConfigBoolean("common", categoryName, keyName);
    }

    public static class Serializer implements ILootSerializer<ConfigurationLootCondition>
    {
        @Override
        public void serialize(JsonObject json, ConfigurationLootCondition value, JsonSerializationContext ctx)
        {
            json.add("category", new JsonPrimitive(value.categoryName));
            json.add("key", new JsonPrimitive(value.keyName));
        }

        @Override
        public ConfigurationLootCondition deserialize(JsonObject json, JsonDeserializationContext ctx)
        {
            String categoryName = JSONUtils.getString(json, "category");
            String keyName = JSONUtils.getString(json, "key");

            return new ConfigurationLootCondition(categoryName, keyName);
        }
    }
}