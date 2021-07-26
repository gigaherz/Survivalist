package gigaherz.survivalist.util;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class AppendLootTable extends LootModifier
{
    private final ResourceLocation lootTable;

    public AppendLootTable(ILootCondition[] lootConditions, ResourceLocation lootTable)
    {
        super(lootConditions);
        this.lootTable = lootTable;
    }

    boolean reentryPrevention = false;

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
    {
        if (reentryPrevention)
            return generatedLoot;

        reentryPrevention = true;
        LootTable lootTable = context.getLootTable(this.lootTable);
        List<ItemStack> extras = lootTable.generate(context);
        generatedLoot.addAll(extras);
        reentryPrevention = false;

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AppendLootTable>
    {
        @Override
        public AppendLootTable read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
        {
            ResourceLocation lootTable = new ResourceLocation(JSONUtils.getString(object, "add_loot"));
            return new AppendLootTable(ailootcondition, lootTable);
        }

        @Override
        public JsonObject write(AppendLootTable instance)
        {
            JsonObject object = new JsonObject();
            object.addProperty("add_loot", instance.lootTable.toString());
            return object;
        }
    }
}