package gigaherz.survivalist.fibers;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class AddFibersModifier implements IGlobalLootModifier
{
    private final ILootCondition[] lootConditions;

    private final ResourceLocation lootTable;

    public AddFibersModifier(ILootCondition[] lootConditions, ResourceLocation lootTable)
    {
        this.lootConditions = lootConditions;
        this.lootTable = lootTable;
    }

    boolean reentryPrevention = false;

    @Nonnull
    @Override
    public List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context)
    {
        if (reentryPrevention)
            return generatedLoot;

        if (!Arrays.stream(lootConditions).allMatch(c -> c.test(context)))
            return generatedLoot;

        reentryPrevention = true;
        LootTable lootTable = context.func_227502_a_(this.lootTable);
        List<ItemStack> extras = lootTable.generate(context);
        generatedLoot.addAll(extras);
        reentryPrevention = false;

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AddFibersModifier>
    {
        @Override
        public AddFibersModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
        {
            ResourceLocation lootTable = new ResourceLocation(JSONUtils.getString(object, "add_loot"));
            return new AddFibersModifier(ailootcondition, lootTable);
        }
    }
}
