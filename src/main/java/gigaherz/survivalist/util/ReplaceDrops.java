package gigaherz.survivalist.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class ReplaceDrops extends LootModifier
{
    private final List<Replacement> replacements;

    public ReplaceDrops(ILootCondition[] lootConditions, List<Replacement> replacements)
    {
        super(lootConditions);
        this.replacements = replacements;
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
    {
        Random rnd = context.getRandom();
        List<ItemStack> outputs = Lists.newArrayList();
        for(ItemStack input : generatedLoot)
        {
            boolean replaced = false;
            for(Replacement r : replacements)
            {
                if (r.input.test(input))
                {
                    for(Result out : r.outputs)
                    {
                        ItemStack output = out.stack.copy();
                        int count = out.max > out.min ? rnd.nextInt(out.max-out.min)+out.min : out.min;
                        output.setCount(count);
                        if (output.getCount() > 0)
                        {
                            outputs.add(output);
                        }
                    }
                    replaced = true;
                    break;
                }
            }
            if (!replaced)
                outputs.add(input);
        }
        return outputs;
    }

    public static class Serializer extends GlobalLootModifierSerializer<ReplaceDrops>
    {
        @Override
        public ReplaceDrops read(ResourceLocation location, JsonObject json, ILootCondition[] conditions)
        {
            List<Replacement> replacements = Lists.newArrayList();

            for(JsonElement e : JSONUtils.getJsonArray(json, "replacements"))
            {
                JsonObject repl = e.getAsJsonObject();

                if (!repl.has("from"))
                    throw new JsonSyntaxException("Replacement must have a 'from' ingredient.");
                if (!repl.has("to"))
                    throw new JsonSyntaxException("Replacement must have a 'to' element, either object or array.");

                Ingredient input = CraftingHelper.getIngredient(repl.get("from"));
                List<Result> results = Lists.newArrayList();

                JsonElement to = repl.get("to");
                if (to.isJsonObject())
                {
                    results.add(parseResult(to.getAsJsonObject()));
                }
                else
                {
                    for(JsonElement ee : to.getAsJsonArray())
                    {
                        results.add(parseResult(ee.getAsJsonObject()));
                    }
                }
                replacements.add(new Replacement(input, results));
            }
            return new ReplaceDrops(conditions, replacements);
        }

        private Result parseResult(JsonObject obj)
        {
            int min = 1, max = 1;
            ItemStack output = CraftingHelper.getItemStack(obj, true);
            if (obj.has("quantity"))
            {
                JsonElement q = obj.get("quantity");
                if (q.isJsonObject())
                {
                    JsonObject qq = q.getAsJsonObject();
                    min = JSONUtils.getInt(qq, "min", 0);
                    max = JSONUtils.getInt(qq, "max", min);
                }
                else if(q.isJsonPrimitive())
                {
                    min = max = q.getAsInt();
                }
                else
                {
                    throw new JsonSyntaxException("If 'quantity' isp resent, it must be either an integer or an object");
                }
            }

            return new Result(min, max, output);
        }
    }

    private static class Replacement
    {
        final Ingredient input;
        final List<ReplaceDrops.Result> outputs;

        private Replacement(Ingredient input, List<ReplaceDrops.Result> outputs)
        {
            this.input = input;
            this.outputs = outputs;
        }
    }

    private static class Result
    {
        int min;
        int max;
        ItemStack stack;

        public Result(int min, int max, ItemStack stack)
        {
            this.min=min;
            this.max=max;
            this.stack = stack;
        }
    }
}
