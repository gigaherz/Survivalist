package gigaherz.survivalist.fibers;

import com.google.gson.JsonObject;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.SurvivalistItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AddFibersModifier implements IGlobalLootModifier
{
    private final ILootCondition[] lootConditions;

    public AddFibersModifier(ILootCondition[] lootConditions)
    {
        this.lootConditions = lootConditions;
    }

    @Nonnull
    @Override
    public List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context)
    {
        if (!Arrays.stream(lootConditions).allMatch(c -> c.test(context)))
            return generatedLoot;

        Random rnd = context.getRandom();

        if (rnd.nextFloat() < 0.12f)
            generatedLoot.add(new ItemStack(SurvivalistItems.PLANT_FIBRES.get()));

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AddFibersModifier>
    {
        @Override
        public AddFibersModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
        {
            return new AddFibersModifier(ailootcondition);
        }
    }
}
