package gigaherz.survivalist;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class ConfigurationCondition implements IConditionFactory
{
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json)
    {
        JsonPrimitive categoryName = json.getAsJsonPrimitive("category");
        JsonPrimitive keyName = json.getAsJsonPrimitive("key");

        ConfigCategory category = ConfigManager.instance.config.getCategory(categoryName.getAsString());
        Property property = category != null ? category.get(keyName.getAsString()) : null;

        if (property == null)
        {
            Survivalist.logger.error("Property not found! {} / {}", categoryName.getAsString(), keyName.getAsString());
            return () -> false;
        }

        return property::getBoolean;
    }
}
