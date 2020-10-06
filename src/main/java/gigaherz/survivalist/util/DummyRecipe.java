package gigaherz.survivalist.util;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DummyRecipe implements ICraftingRecipe
{
    final ResourceLocation id;

    public DummyRecipe(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return false;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn)
    {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return null;
    }

    @Override
    public IRecipeType<?> getType()
    {
        return null;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<DummyRecipe>
    {
        @Override
        public DummyRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            return new DummyRecipe(recipeId);
        }

        @Override
        public DummyRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            return new DummyRecipe(recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, DummyRecipe recipe)
        {
            // nothing to write
        }
    }
}