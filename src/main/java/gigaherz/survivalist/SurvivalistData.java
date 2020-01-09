package gigaherz.survivalist;

import com.mojang.datafixers.util.Pair;
import gigaherz.survivalist.chopblock.ChopblockMaterials;
import gigaherz.survivalist.rocks.Nuggets;
import gigaherz.survivalist.rocks.Rocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SurvivalistData
{
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient())
        {
            //gen.addProvider(new Lang(gen));
            //gen.addProvider(new ItemModels(gen, event.getExistingFileHelper()));
            //gen.addProvider(new BlockStates(gen, event.getExistingFileHelper()));
        }
        if (event.includeServer())
        {
            gen.addProvider(new ItemTags(gen));
            gen.addProvider(new BlockTags(gen));
            gen.addProvider(new Recipes(gen));
        }
    }

    public static Tag<Item> makeItemTag(String id)
    {
        return makeItemTag(new ResourceLocation(id));
    }

    public static Tag<Item> makeItemTag(ResourceLocation id)
    {
        return new net.minecraft.tags.ItemTags.Wrapper(id);
    }

    private static class Recipes extends RecipeProvider implements IDataProvider, IConditionBuilder
    {
        public Recipes(DataGenerator gen)
        {
            super(gen);
        }

        @Override
        protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
        {
            Arrays.stream(Rocks.values())
                    .forEach(rock -> rock.getSmeltsInto().ifPresent(result -> {
                        Tag<Item> tag = makeItemTag(rock.getSmeltingTag());
                        ResourceLocation itemId = rock.getItem().getId();
                        ResourceLocation recipeId = new ResourceLocation(itemId.getNamespace(), "smelting/" + itemId.getPath());
                        CookingRecipeBuilder
                                .smeltingRecipe(Ingredient.fromTag(tag), result.get(), 0.2f, 50)
                                .addCriterion("has_rock", hasItem(tag))
                                .build(consumer, recipeId);
                    }));

            Tag<Item> leatherTag = makeItemTag("survivalist:tanned_leather");
            ConditionalRecipe.builder()
                    .addCondition(new ConfigurationCondition("drying_rack", "EnableSaddleCrafting"))
                    .addRecipe(
                            ShapedRecipeBuilder.shapedRecipe(Items.SADDLE, 1)
                                    .patternLine("ttt")
                                    .patternLine("tst")
                                    .patternLine("i i")
                                    .key('t', leatherTag)
                                    .key('s', Items.STRING)
                                    .key('i', Items.IRON_INGOT)
                                    .setGroup("")
                                    .addCriterion("has_leather", hasItem(leatherTag)) // dummy, is only required to pass the validation
                                    ::build
                    )
                    .setAdvancement(SurvivalistMod.location("craft_saddle"),
                            ConditionalAdvancement.builder()
                                    .addCondition(new ConfigurationCondition("drying_rack", "EnableSaddleCrafting"))
                                    .addAdvancement(
                                            Advancement.Builder.builder()
                                                    .withParentId(new ResourceLocation("minecraft", "recipes/root"))
                                                    .withRewards(AdvancementRewards.Builder.recipe(SurvivalistMod.location("saddle")))
                                                    .withCriterion("has_leather", hasItem(leatherTag))
                                                    .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(SurvivalistMod.location("saddle")))
                                    )
                    )
                    .build(consumer, SurvivalistMod.location("saddle"));
        }
    }

    private static class ItemTags extends ItemTagsProvider implements IDataProvider
    {
        public ItemTags(DataGenerator gen)
        {
            super(gen);
        }

        @Override
        protected void registerTags()
        {
            Arrays.stream(Rocks.values())
                    .flatMap(rock -> rock.getTagLocations().stream().map(tag -> Pair.of(rock.getItem().get(), tag)))
                    .forEach((pair) -> this.getBuilder(makeItemTag(pair.getSecond())).add(pair.getFirst()));

            Arrays.stream(Nuggets.values())
                    .flatMap(rock -> rock.getTagLocations().stream().map(tag -> Pair.of(rock.getItem().get(), tag)))
                    .forEach((pair) -> this.getBuilder(makeItemTag(pair.getSecond())).add(pair.getFirst()));

            this.getBuilder(makeItemTag(SurvivalistMod.location("chopping_blocks")))
                    .add(Arrays.stream(ChopblockMaterials.values())
                            .flatMap(block -> Stream.of(block.getPristine(), block.getChipped(), block.getDamaged()).map(reg -> reg.get().asItem()))
                            .toArray(Item[]::new));
        }
    }

    private static class BlockTags extends BlockTagsProvider implements IDataProvider
    {
        public BlockTags(DataGenerator gen)
        {
            super(gen);
        }

        @Override
        protected void registerTags()
        {
            this.getBuilder(new net.minecraft.tags.BlockTags.Wrapper(SurvivalistMod.location("chopping_blocks")))
                    .add(Arrays.stream(ChopblockMaterials.values())
                            .flatMap(block -> Stream.of(block.getPristine(), block.getChipped(), block.getDamaged()).map(Supplier::get))
                            .toArray(Block[]::new));
        }
    }
}
