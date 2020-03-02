package gigaherz.survivalist;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

public class ConfigToggledIngredientSerializer implements IIngredientSerializer<Ingredient>
{
    public static ResourceLocation NAME = SurvivalistMod.location("config_toggled_ingredient");
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

        return new ConfigToggledIngredient(
                categoryName, keyName,
                CraftingHelper.getIngredient(json.getAsJsonObject("then")),
                CraftingHelper.getIngredient(json.getAsJsonObject("else"))
        );
    }

    @Override
    public void write(PacketBuffer buffer, Ingredient ingredient)
    {
        // Not used.
    }

    public static class ConfigToggledIngredient extends Ingredient
    {
        private final String categoryName;
        private final String keyName;
        private final Ingredient then;
        private final Ingredient other;

        protected ConfigToggledIngredient(String categoryName, String keyName, Ingredient then, Ingredient other)
        {
            super(Stream.empty());
            this.categoryName = categoryName;
            this.keyName = keyName;
            this.then = then;
            this.other = other;
        }
        
        protected boolean getConfigValue()
        {
            return ConfigManager.getConfigBoolean(categoryName, keyName);
        }

        @Override
        public ItemStack[] getMatchingStacks()
        {
            return getConfigValue() ? then.getMatchingStacks() : other.getMatchingStacks();
        }

        @Override
        public boolean test(@Nullable ItemStack stack)
        {
            return getConfigValue() ? then.test(stack) : other.test(stack);
        }

        @Override
        public IntList getValidItemStacksPacked()
        {
            return getConfigValue() ? then.getValidItemStacksPacked() : other.getValidItemStacksPacked();
        }

        @Override
        public boolean hasNoMatchingItems()
        {
            return getConfigValue() ? then.hasNoMatchingItems() : other.hasNoMatchingItems();
        }

        private static Method invalidateMethod = ObfuscationReflectionHelper.findMethod(Ingredient.class, "invalidate");
        @Override
        protected void invalidate()
        {
            try
            {
                invalidateMethod.invoke(then);
                invalidateMethod.invoke(other);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isSimple()
        {
            return getConfigValue() ? then.isSimple() : other.isSimple();
        }

        @Override
        public IIngredientSerializer<? extends Ingredient> getSerializer()
        {
            return INSTANCE;
        }

        @Override
        public JsonElement serialize()
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", NAME.toString());
            obj.addProperty("category", categoryName);
            obj.addProperty("key", keyName);
            obj.add("then", then.serialize());
            obj.add("else", other.serialize());
            return obj;
        }
    }
}
