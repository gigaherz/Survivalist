package gigaherz.survivalist;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class ConfigurationToggledIngredient implements IIngredientFactory
{
    @Override
    public Ingredient parse(JsonContext context, JsonObject json)
    {
        JsonPrimitive categoryName = json.getAsJsonPrimitive("category");
        JsonPrimitive keyName = json.getAsJsonPrimitive("key");

        ConfigCategory category = ConfigManager.instance.config.getCategory(categoryName.getAsString());
        Property property = category.get(keyName.getAsString());

        if (property.getBoolean())
            return CraftingHelper.getIngredient(json.getAsJsonObject("then"), context);

        return CraftingHelper.getIngredient(json.getAsJsonObject("else"), context);
    }
}
