package gigaherz.survivalist.api;

import gigaherz.survivalist.Survivalist;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import java.util.Optional;

public class DryingRecipe implements IRecipe<DryingContext>
{
    @ObjectHolder("survivalist:drying")
    public static IRecipeSerializer<?> SERIALIZER = null;

    public static final ResourceLocation RECIPE_TYPE_ID = Survivalist.location("drying");
    public static final IRecipeType<DryingRecipe> DRYING = Registry.register(Registry.RECIPE_TYPE, RECIPE_TYPE_ID, new IRecipeType<DryingRecipe>()
    {
        @Override
        public String toString()
        {
            return RECIPE_TYPE_ID.toString();
        }
    });

    public static int getDryingTime(World world, DryingContext ctx)
    {
        return world.getRecipeManager().getRecipe(DRYING, ctx, world).map(DryingRecipe::getDryTime).orElse(0);
    }

    public static int getDryingTime(World world, final ItemStack input)
    {
        DryingContext ctx = new DryingContext(new IItemHandlerModifiable()
        {
            @Override
            public void setStackInSlot(int slot, @Nonnull ItemStack stack)
            {

            }

            @Override
            public int getSlots()
            {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot)
            {
                return input;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                return null;
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate)
            {
                return null;
            }

            @Override
            public int getSlotLimit(int slot)
            {
                return 0;
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return false;
            }
        });
        return world.getRecipeManager().getRecipe(DRYING, ctx, world).map(DryingRecipe::getDryTime).orElse(200);
    }

    public static ItemStack getDryingResult(World world, DryingContext ctx)
    {
        return world.getRecipeManager().getRecipe(DRYING, ctx, world).map(r -> r.getCraftingResult(ctx)).orElse(ItemStack.EMPTY);
    }

    public static Optional<DryingRecipe> getRecipe(World world, DryingContext ctx)
    {
        return world.getRecipeManager().getRecipe(DRYING, ctx, world);
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
    public boolean matches(DryingContext inv, World worldIn)
    {
        return input.test(inv.getStackInSlot(0));
    }

    @Override
    public ItemStack getCraftingResult(DryingContext inv)
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
    public ItemStack getIcon() {
        return new ItemStack(Survivalist.Blocks.RACK);
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DryingRecipe>
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
