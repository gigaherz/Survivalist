package gigaherz.survivalist.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class MatchBlockCondition implements ILootCondition
{
    public static LootConditionType BLOCK_TAG_CONDITION;

    @Nullable
    final List<Block> blockList;
    @Nullable
    final ITag.INamedTag<Block> blockTag;

    public MatchBlockCondition(@Nullable List<Block> blockList, @Nullable ITag.INamedTag<Block> blockTag)
    {
        this.blockList = blockList;
        this.blockTag = blockTag;
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        BlockState state = lootContext.get(LootParameters.BLOCK_STATE);
        if (state == null)
            return false;
        if (blockTag != null)
            return blockTag.contains(state.getBlock());
        if (blockList != null)
            return blockList.contains(state.getBlock());
        return false;
    }

    @Override
    public LootConditionType func_230419_b_()
    {
        return BLOCK_TAG_CONDITION;
    }

    public static class Serializer implements ILootSerializer<MatchBlockCondition>
    {
        @Override
        public void serialize(JsonObject json, MatchBlockCondition value, JsonSerializationContext context)
        {
            if (value.blockTag != null)
                json.addProperty("tag", value.blockTag.getName().toString());
        }

        @Override
        public MatchBlockCondition deserialize(JsonObject json, JsonDeserializationContext context)
        {
            if (json.has("tag"))
            {
                ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
                return new MatchBlockCondition(null, BlockTags.createOptional(tagName));
            }
            else if(json.has("blocks"))
            {
                List<Block> blockNames = Lists.newArrayList();
                for(JsonElement e : JSONUtils.getJsonArray(json, "blocks"))
                {
                    ResourceLocation blockName = new ResourceLocation(e.getAsString());
                    blockNames.add(ForgeRegistries.BLOCKS.getValue(blockName));
                }
                return new MatchBlockCondition(blockNames, null);
            }
            else if(json.has("block"))
            {
                ResourceLocation blockName = new ResourceLocation(JSONUtils.getString(json, "block"));
                return new MatchBlockCondition(Collections.singletonList(ForgeRegistries.BLOCKS.getValue(blockName)), null);
            }
            throw new RuntimeException("match_block must have one of 'tag', 'block' or 'blocks' key");
        }
    }
}