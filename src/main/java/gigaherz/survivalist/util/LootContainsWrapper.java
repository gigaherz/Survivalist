package gigaherz.survivalist.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class LootContainsWrapper extends LootModifier
{
    private final IGlobalLootModifier childModifier;
    private final Ingredient itemMatcher;

    public LootContainsWrapper(ILootCondition[] lootConditions, IGlobalLootModifier childModifier, Ingredient itemMatcher)
    {
        super(lootConditions);
        this.childModifier = childModifier;
        this.itemMatcher = itemMatcher;
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
    {
        if (generatedLoot.stream().noneMatch(itemMatcher))
            return generatedLoot;

        return childModifier.apply(generatedLoot, context);
    }

    public static class Serializer extends GlobalLootModifierSerializer<LootContainsWrapper>
    {
        @Override
        public LootContainsWrapper read(ResourceLocation location, JsonObject object, ILootCondition[] contidions)
        {
            IGlobalLootModifier child = deserializeModifier(location, JSONUtils.getJsonObject(object, "modifier"));
            Ingredient itemMatcher = CraftingHelper.getIngredient(JSONUtils.getJsonObject(object, "matching"));
            return new LootContainsWrapper(contidions, child, itemMatcher);
        }

        @Override
        public JsonObject write(LootContainsWrapper instance)
        {
            JsonObject object = new JsonObject();
            object.add("matching", instance.itemMatcher.serialize());

            //LootModifierManager.getSerializerForName(instance.childModifier)
            //object.add("modifier", instance.childModifier.);
            //return object;

            throw new RuntimeException("Not implemented.");
        }

        private static final Gson GSON_INSTANCE = new GsonBuilder()
                .registerTypeHierarchyAdapter(ILootFunction.class, LootFunctionManager.func_237450_a_())
                .registerTypeHierarchyAdapter(ILootCondition.class, LootConditionManager.func_237474_a_())
                .create();

        private IGlobalLootModifier deserializeModifier(ResourceLocation location, JsonElement element)
        {
            JsonObject object = element.getAsJsonObject();
            ILootCondition[] lootConditions = GSON_INSTANCE.fromJson(object.get("conditions"), ILootCondition[].class);

            // For backward compatibility with the initial implementation, fall back to using the location as the type.
            // TODO: Remove fallback in 1.16
            ResourceLocation serializer = location;
            if (object.has("type"))
            {
                serializer = new ResourceLocation(JSONUtils.getString(object, "type"));
            }

            return ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.getValue(serializer).read(location, object, lootConditions);
        }
    }
}