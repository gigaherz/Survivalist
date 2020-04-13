package gigaherz.survivalist.fibers;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
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
        LootTable lootTable = context.getLootTableManager().getLootTableFromLocation(this.lootTable);
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
    }
}
