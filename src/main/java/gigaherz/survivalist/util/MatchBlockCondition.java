package gigaherz.survivalist.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import gigaherz.survivalist.SurvivalistMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class MatchBlockCondition implements ILootCondition
{
    @Nullable
    final List<Block> blockList;
    @Nullable
    final Tag<Block> blockTag;

    public MatchBlockCondition(@Nullable List<Block> blockList, @Nullable Tag<Block> blockTag)
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

    public static class Serializer extends ILootCondition.AbstractSerializer<MatchBlockCondition>
    {
        public Serializer()
        {
            super(SurvivalistMod.location("match_block"), MatchBlockCondition.class);
        }

        @Override
        public void serialize(JsonObject json, MatchBlockCondition value, JsonSerializationContext context)
        {
            json.addProperty("tag", value.blockTag.getId().toString());
        }

        @Override
        public MatchBlockCondition deserialize(JsonObject json, JsonDeserializationContext context)
        {
            if (json.has("tag"))
            {
                ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
                return new MatchBlockCondition(null, new BlockTags.Wrapper(tagName));
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
