package gigaherz.survivalist;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class ConfigToggledIngredientSerializer implements IIngredientSerializer<Ingredient>
{
    public static ResourceLocation NAME = Survivalist.location("config_toggled_ingredient");
    public static ConfigToggledIngredientSerializer INSTANCE = new ConfigToggledIngredientSerializer();

    @Override
    public Ingredient parse(PacketBuffer buffer)
    {
        return Ingredient.EMPTY;
    }

    @Override
    public Ingredient parse(JsonObject json)
    {
        String categoryName = JSONUtils.getString(json, "category");
        String keyName = JSONUtils.getString(json, "key");

        if (ConfigManager.getConfigBoolean(categoryName, keyName))
            return CraftingHelper.getIngredient(json.getAsJsonObject("then"));

        return CraftingHelper.getIngredient(json.getAsJsonObject("else"));
    }

    @Override
    public void write(PacketBuffer buffer, Ingredient ingredient)
    {
        // Not used.
    }
}
