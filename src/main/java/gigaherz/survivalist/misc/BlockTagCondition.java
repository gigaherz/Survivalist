package gigaherz.survivalist.misc;

import com.google.gson.JsonDeserializationContext;
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

public class BlockTagCondition implements ILootCondition
{
    final Tag<Block> blockTag;

    public BlockTagCondition(Tag<Block> blockTag)
    {
        this.blockTag = blockTag;
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        BlockState state = lootContext.get(LootParameters.BLOCK_STATE);
        if (state == null)
            return false;
        return blockTag.contains(state.getBlock());
    }

    public static class Serializer extends AbstractSerializer<BlockTagCondition>
    {
        public Serializer()
        {
            super(SurvivalistMod.location("block_tag"), BlockTagCondition.class);
        }

        @Override
        public void serialize(JsonObject json, BlockTagCondition value, JsonSerializationContext context)
        {
            json.addProperty("tag", value.blockTag.getId().toString());
        }

        @Override
        public BlockTagCondition deserialize(JsonObject json, JsonDeserializationContext context)
        {
            ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
            return new BlockTagCondition(new BlockTags.Wrapper(tagName));
        }
    }
}
