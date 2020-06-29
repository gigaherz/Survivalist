package gigaherz.survivalist.misc;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

public class BlockTagCondition implements ILootCondition
{
    public static LootConditionType BLOCK_TAG_CONDITION;

    final ITag.INamedTag<Block> blockTag;

    public BlockTagCondition(ITag.INamedTag<Block> blockTag)
    {
        this.blockTag = blockTag;
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        BlockState state = lootContext.get(LootParameters.BLOCK_STATE);
        if (state == null)
            return false;
        return blockTag.func_230235_a_(state.getBlock());
    }

    @Override
    public LootConditionType func_230419_b_()
    {
        return BLOCK_TAG_CONDITION;
    }

    public static class Serializer implements ILootSerializer<BlockTagCondition>
    {
        @Override
        public void func_230424_a_(JsonObject json, BlockTagCondition value, JsonSerializationContext context)
        {
            json.addProperty("tag", value.blockTag.func_230234_a_().toString());
        }

        @Override
        public BlockTagCondition func_230423_a_(JsonObject json, JsonDeserializationContext context)
        {
            ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
            return new BlockTagCondition(BlockTags.makeWrapperTag(tagName.toString()));
        }
    }
}
