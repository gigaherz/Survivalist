package gigaherz.survivalist;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigurationCondition implements ICondition
{
    public static final ResourceLocation NAME = SurvivalistMod.location("configuration");

    private final String categoryName;
    private final String keyName;

    public ConfigurationCondition(String categoryName, String keyName)
    {
        this.categoryName = categoryName;
        this.keyName = keyName;
    }

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test()
    {
        return ConfigManager.getConfigBoolean(categoryName, keyName);
    }

    public static class Serializer implements IConditionSerializer<ConfigurationCondition>
    {
        public static Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ConfigurationCondition value)
        {
            json.add("category", new JsonPrimitive(value.categoryName));
            json.add("key", new JsonPrimitive(value.keyName));
        }

        @Override
        public ConfigurationCondition read(JsonObject json)
        {
            String categoryName = JSONUtils.getString(json, "category");
            String keyName = JSONUtils.getString(json, "key");

            return new ConfigurationCondition(categoryName, keyName);
        }

        @Override
        public ResourceLocation getID()
        {
            return NAME;
        }
    }
}
