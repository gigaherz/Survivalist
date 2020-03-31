package gigaherz.survivalist.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.SurvivalistMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class ChoppingRecipe implements IRecipe<ChoppingContext>
{
    @ObjectHolder("survivalist:chopping")
    public static IRecipeSerializer<?> SERIALIZER = null;

    public static final ResourceLocation RECIPE_TYPE_ID = SurvivalistMod.location("chopping");
    public static IRecipeType<ChoppingRecipe> CHOPPING = Registry.register(Registry.RECIPE_TYPE, RECIPE_TYPE_ID, new IRecipeType<ChoppingRecipe>()
    {
        @Override
        public String toString()
        {
            return RECIPE_TYPE_ID.toString();
        }
    });

    public static Optional<ChoppingRecipe> getRecipe(World world, ChoppingContext ctx)
    {
        return world.getRecipeManager().getRecipe(CHOPPING, ctx, world);
    }

    public static Optional<ChoppingRecipe> getRecipe(World world, ItemStack stack)
    {
        return getRecipe(world, new ChoppingContext(new SingletonInventory(stack), null, 0, 0, null));
    }

    public static Collection<ChoppingRecipe> getAllRecipes(World world)
    {
        return world.getRecipeManager().getRecipes(CHOPPING).values().stream().map(r -> (ChoppingRecipe) r).collect(Collectors.toList());
    }

    private final ResourceLocation id;
    private final String group;
    private final Ingredient input;
    private final ItemStack output;
    private final double outputMultiplier;
    private final double hitCountMultiplier;
    private final int maxOutput;
    private final int sawingTime;

    public ChoppingRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output, double outputMultiplier, double hitCountMultiplier, int maxOutput,
                          int sawingTime)
    {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
        this.outputMultiplier = outputMultiplier;
        this.hitCountMultiplier = hitCountMultiplier;
        this.maxOutput = maxOutput;
        this.sawingTime = sawingTime;
    }

    public ItemStack getOutput()
    {
        return output;
    }

    public double getOutputMultiplier()
    {
        return outputMultiplier;
    }

    public double getHitCountMultiplier()
    {
        return hitCountMultiplier;
    }

    public int getSawingTime()
    {
        return sawingTime;
    }

    public int getMaxOutput()
    {
        return maxOutput;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.from(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(ChoppingContext inv, World worldIn)
    {
        return input.test(inv.getStackInSlot(0));
    }

    @Override
    public ItemStack getCraftingResult(ChoppingContext inv)
    {
        return inv.getPlayer() != null
                ? getResults(inv.getStackInSlot(0), inv.getPlayer(), inv.getAxeLevel(), inv.getFortune(), inv.getRandom())
                : getResultsSawmill();
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return true;
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
    public String getGroup()
    {
        return group;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType()
    {
        return CHOPPING;
    }

    private ItemStack getResults(ItemStack input, @Nullable PlayerEntity player, int axeLevel, int fortune, Random random)
    {
        double number = getOutputMultiplier(axeLevel) * (1 + random.nextFloat() * fortune);

        int whole = (int) Math.floor(number);
        double remainder = number - whole;

        if (random.nextFloat() < remainder)
        {
            whole++;
        }

        if (getMaxOutput() > 0)
            whole = Math.min(whole, getMaxOutput());

        if (whole > 0)
        {
            ItemStack out = getOutput().copy();
            out.setCount(whole);
            return out;
        }

        return ItemStack.EMPTY;
    }

    public double getOutputMultiplier(int axeLevel)
    {
        double number = ConfigManager.SERVER.choppingWithEmptyHand.get() * getOutputMultiplier();

        if (axeLevel >= 0)
            number = Math.max(0, getOutputMultiplier() * ConfigManager.getAxeLevelMultiplier(axeLevel));
        return number;
    }

    private ItemStack getResultsSawmill()
    {
        double number = Math.max(0, getOutputMultiplier() * 4);

        int whole = (int) Math.floor(number);

        if (getMaxOutput() > 0)
            whole = Math.min(whole, getMaxOutput());

        if (whole > 0)
        {
            ItemStack out = getOutput().copy();
            out.setCount(whole);
            return out;
        }

        return ItemStack.EMPTY;
    }

    public double getHitProgress(int axeLevel)
    {
        return 25 + getHitCountMultiplier() * 25 * Math.max(0, axeLevel);
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<ChoppingRecipe>
    {
        @Override
        public ChoppingRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            String group = JSONUtils.getString(json, "group", "");
            JsonElement jsonelement = JSONUtils.isJsonArray(json, "ingredient")
                    ? JSONUtils.getJsonArray(json, "ingredient")
                    : JSONUtils.getJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.deserialize(jsonelement);
            String s1 = JSONUtils.getString(json, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            ItemStack itemstack = new ItemStack(Optional.ofNullable(ForgeRegistries.ITEMS.getValue(resourcelocation)).orElseThrow(() -> new IllegalStateException("Item: " + s1 + " does not exist")));
            double outputMultiplier = JSONUtils.getFloat(json, "output_multiplier", 1.0f);
            double hitCountMultiplier = JSONUtils.getFloat(json, "hit_count_multiplier", 1.0f);
            int maxOutput = JSONUtils.getInt(json, "max_output", 0);
            int sawingTime = JSONUtils.getInt(json, "sawing_time", 200);
            return new ChoppingRecipe(recipeId, group, ingredient, itemstack, outputMultiplier, hitCountMultiplier, maxOutput, sawingTime);
        }

        @Override
        public ChoppingRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {

            String group = buffer.readString(32767);
            Ingredient ingredient = Ingredient.read(buffer);
            ItemStack itemstack = buffer.readItemStack();
            double outputMultiplier = buffer.readDouble();
            double hitCountMultiplier = buffer.readDouble();
            int maxOutput = buffer.readVarInt();
            int sawingTime = buffer.readVarInt();
            return new ChoppingRecipe(recipeId, group, ingredient, itemstack, outputMultiplier, hitCountMultiplier, maxOutput, sawingTime);
        }

        @Override
        public void write(PacketBuffer buffer, ChoppingRecipe recipe)
        {
            buffer.writeString(recipe.group);
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeDouble(recipe.outputMultiplier);
            buffer.writeDouble(recipe.hitCountMultiplier);
            buffer.writeVarInt(recipe.maxOutput);
            buffer.writeVarInt(recipe.sawingTime);
        }
    }
}
