package gigaherz.survivalist.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.survivalist.SurvivalistBlocks;
import gigaherz.survivalist.SurvivalistMod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Collection;
import java.util.Optional;

public class DryingRecipe implements IRecipe<ItemHandlerWrapper>
{
    @ObjectHolder("survivalist:drying")
    public static IRecipeSerializer<?> SERIALIZER = null;

    public static IRecipeType<DryingRecipe> DRYING = IRecipeType.register(SurvivalistMod.location("drying").toString());

    public static int getDryingTime(World world, ItemHandlerWrapper ctx)
    {
        return world.getRecipeManager().getRecipe(DRYING, ctx, world).map(DryingRecipe::getDryTime).orElse(0);
    }

    public static int getDryingTime(World world, final ItemStack input)
    {
        ItemHandlerWrapper ctx = new ItemHandlerWrapper(new SingletonInventory(input));
        return world.getRecipeManager().getRecipe(DRYING, ctx, world).map(DryingRecipe::getDryTime).orElse(200);
    }

    public static ItemStack getDryingResult(World world, ItemHandlerWrapper ctx)
    {
        return world.getRecipeManager().getRecipe(DRYING, ctx, world).map(r -> r.getCraftingResult(ctx)).orElse(ItemStack.EMPTY);
    }

    public static Optional<DryingRecipe> getRecipe(World world, ItemHandlerWrapper ctx)
    {
        return world.getRecipeManager().getRecipe(DRYING, ctx, world);
    }

    public static Collection<DryingRecipe> getAllRecipes(World world)
    {
        return world.getRecipeManager().getRecipesForType(DRYING);
    }

    private final String group;
    private final ResourceLocation id;
    private final Ingredient input;
    private final int time;
    private final ItemStack output;

    public DryingRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output, int time)
    {
        this.group = group;
        this.id = id;
        this.input = input;
        this.time = time;
        this.output = output;
    }

    @Override
    public String getGroup()
    {
        return group;
    }

    public int getDryTime()
    {
        return time;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.from(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(ItemHandlerWrapper inv, World worldIn)
    {
        return input.test(inv.getStackInSlot(0));
    }

    @Override
    public ItemStack getCraftingResult(ItemHandlerWrapper inv)
    {
        return input.test(inv.getStackInSlot(0)) ? output.copy() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType()
    {
        return DRYING;
    }

    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(SurvivalistBlocks.RACK.get());
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<DryingRecipe>
    {
        @Override
        public DryingRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            String group = JSONUtils.getString(json, "group", "");
            JsonElement jsonelement = JSONUtils.isJsonArray(json, "ingredient")
                    ? JSONUtils.getJsonArray(json, "ingredient")
                    : JSONUtils.getJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.deserialize(jsonelement);
            String s1 = JSONUtils.getString(json, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            ItemStack itemstack = new ItemStack(Optional.ofNullable(ForgeRegistries.ITEMS.getValue(resourcelocation)).orElseThrow(() -> new IllegalStateException("Item: " + s1 + " does not exist")));
            int dryingTime = JSONUtils.getInt(json, "dryingTime", 200);
            return new DryingRecipe(recipeId, group, ingredient, itemstack, dryingTime);
        }

        @Override
        public DryingRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            String group = buffer.readString(32767);
            Ingredient ingredient = Ingredient.read(buffer);
            ItemStack itemstack = buffer.readItemStack();
            int dryingTime = buffer.readVarInt();
            return new DryingRecipe(recipeId, group, ingredient, itemstack, dryingTime);
        }

        @Override
        public void write(PacketBuffer buffer, DryingRecipe recipe)
        {
            buffer.writeString(recipe.group);
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeVarInt(recipe.time);
        }
    }
}