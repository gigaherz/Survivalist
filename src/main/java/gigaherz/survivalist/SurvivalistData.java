package gigaherz.survivalist;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import gigaherz.survivalist.chopblock.ChopblockMaterials;
import gigaherz.survivalist.chopblock.ChoppingBlock;
import gigaherz.survivalist.rocks.Nuggets;
import gigaherz.survivalist.rocks.Rocks;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.*;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gigaherz.survivalist.SurvivalistMod.MODID;

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

            BlockTags blockTags = new BlockTags(gen, event.getExistingFileHelper());
            gen.addProvider(blockTags);
            gen.addProvider(new ItemTags(gen, blockTags, event.getExistingFileHelper()));
            gen.addProvider(new Recipes(gen));
            gen.addProvider(new LootTables(gen));
        }
    }

    public static ITag.INamedTag<Item> makeItemTag(String id)
    {
        return makeItemTag(new ResourceLocation(id));
    }

    public static ITag.INamedTag<Item> makeItemTag(ResourceLocation id)
    {
        return net.minecraft.tags.ItemTags.makeWrapperTag(id.toString());
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
            Arrays.stream(ChopblockMaterials.values())
                    .forEach(log -> {
                        ITag<Item> tag = makeItemTag(log.getMadeFrom());
                        RegistryObject<ChoppingBlock> result = log.getPristine();
                        ResourceLocation recipeId = result.getId();
                        ConditionalRecipe.builder()
                                .addCondition(not(new ConfigurationCondition("chopping", "ReplacePlanksRecipes")))
                                .addRecipe(
                                        ShapelessRecipeBuilder
                                                .shapelessRecipe(result.get())
                                                .addIngredient(tag)
                                                .addCriterion("has_log", hasItem(tag))
                                                ::build
                                )
                                .addCondition(new ConfigurationCondition("chopping", "ReplacePlanksRecipes"))
                                .addRecipe(
                                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                                .patternLine("ll")
                                                .key('l', tag)
                                                .addCriterion("has_log", hasItem(tag))
                                                ::build
                                )
                                .generateAdvancement()
                                .build(consumer, recipeId);
                    });

            Arrays.stream(Rocks.values())
                    .forEach(rock -> rock.getCraftsInto().ifPresent(result -> {
                        RegistryObject<? extends Item> rockItem = rock.getItem();
                        ResourceLocation itemId = rockItem.getId();
                        if (rock.isOre())
                        {
                            ITag<Item> tag = makeItemTag(rock.getSmeltingTag());
                            CookingRecipeBuilder
                                    .smeltingRecipe(Ingredient.fromTag(tag), result.get(), 0.2f, 50)
                                    .addCriterion("has_rock", hasItem(tag))
                                    .build(consumer, new ResourceLocation(itemId.getNamespace(), "smelting/" + itemId.getPath()));
                            CookingRecipeBuilder
                                    .blastingRecipe(Ingredient.fromTag(tag), result.get(), 0.2f, 25)
                                    .addCriterion("has_rock", hasItem(tag))
                                    .build(consumer, new ResourceLocation(itemId.getNamespace(), "smelting/" + itemId.getPath() + "_from_blasting"));
                        }
                        else
                        {
                            ShapedRecipeBuilder.shapedRecipe(result.get())
                                    .patternLine("rrr")
                                    .patternLine("rcr")
                                    .patternLine("rrr")
                                    .key('r', rockItem.get())
                                    .key('c', new ConfigToggledIngredientSerializer.ConfigToggledIngredient("rocks", "CobbleRequiresClay",
                                            Ingredient.fromItems(Items.CLAY_BALL), Ingredient.fromItems(rockItem.get())))
                                    .addCriterion("has_rock", hasItem(result.get()))
                                    .build(consumer, new ResourceLocation("survivalist", result.get().getRegistryName().getPath() + "_from_rocks"));
                            ;
                        }
                    }));

            ITag<Item> dough = makeItemTag(SurvivalistMod.location("dough"));
            CookingRecipeBuilder
                    .smeltingRecipe(Ingredient.fromTag(dough), SurvivalistItems.ROUND_BREAD.get(), 0.45f, 300)
                    .addCriterion("has_dough", hasItem(dough))
                    .build(consumer, SurvivalistMod.location("cooking/round_bread"));
            CookingRecipeBuilder
                    .cookingRecipe(Ingredient.fromTag(dough), SurvivalistItems.ROUND_BREAD.get(), 0.45f, 150, IRecipeSerializer.SMOKING)
                    .addCriterion("has_dough", hasItem(dough))
                    .build(consumer, SurvivalistMod.location("cooking/round_bread_from_smoking"));
            // no, no roundbread-making in a campfire

            ITag<Item> leatherTag = makeItemTag("survivalist:tanned_leather");
            ResourceLocation saddleRecipeId = SurvivalistMod.location("saddle");
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
                                    .addCriterion("has_leather", hasItem(leatherTag))
                                    ::build
                    )
                    .generateAdvancement()
                    .build(consumer, saddleRecipeId);
        }
    }

    private static class ItemTags extends ItemTagsProvider implements IDataProvider
    {
        public ItemTags(DataGenerator gen, BlockTags blockTags, ExistingFileHelper existingFileHelper)
        {
            super(gen, blockTags, MODID, existingFileHelper);
        }

        @Override
        protected void registerTags()
        {
            Arrays.stream(Rocks.values()).forEach(rock -> {
                rock.getTagLocations().stream().map(tag -> Pair.of(rock.getItem().get(), tag))
                        .forEach((pair) -> this.getOrCreateBuilder(makeItemTag(pair.getSecond())).add(pair.getFirst()));
                if (rock.isOre())
                {

                }
            });

            Arrays.stream(Nuggets.values())
                    .flatMap(rock -> rock.getTagLocations().stream().map(tag -> Pair.of(rock.getItem().get(), tag)))
                    .forEach((pair) -> this.getOrCreateBuilder(makeItemTag(pair.getSecond())).add(pair.getFirst()));

            this.getOrCreateBuilder(makeItemTag(SurvivalistMod.location("dough")))
                    .add(SurvivalistItems.DOUGH.get());

            this.getOrCreateBuilder(makeItemTag(SurvivalistMod.location("chopping_blocks")))
                    .add(Arrays.stream(ChopblockMaterials.values())
                            .flatMap(block -> Stream.of(block.getPristine(), block.getChipped(), block.getDamaged()).map(reg -> reg.get().asItem()))
                            .toArray(Item[]::new));
        }
    }

    private static class BlockTags extends BlockTagsProvider implements IDataProvider
    {
        public BlockTags(DataGenerator gen, ExistingFileHelper existingFileHelper)
        {
            super(gen, MODID, existingFileHelper);
        }

        @Override
        protected void registerTags()
        {
            this.getOrCreateBuilder(net.minecraft.tags.BlockTags.makeWrapperTag(SurvivalistMod.location("chopping_blocks").toString()))
                    .add(Arrays.stream(ChopblockMaterials.values())
                            .flatMap(block -> Stream.of(block.getPristine(), block.getChipped(), block.getDamaged()).map(Supplier::get))
                            .toArray(Block[]::new));
        }
    }

    private static class LootTables extends LootTableProvider implements IDataProvider
    {
        public LootTables(DataGenerator gen)
        {
            super(gen);
        }

        private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> tables = ImmutableList.of(
                Pair.of(BlockTables::new, LootParameterSets.BLOCK)
                //Pair.of(FishingLootTables::new, LootParameterSets.FISHING),
                //Pair.of(ChestLootTables::new, LootParameterSets.CHEST),
                //Pair.of(EntityLootTables::new, LootParameterSets.ENTITY),
                //Pair.of(GiftLootTables::new, LootParameterSets.GIFT)
        );

        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables()
        {
            return tables;
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker)
        {
            map.forEach((p_218436_2_, p_218436_3_) -> {
                LootTableManager.validateLootTable(validationtracker, p_218436_2_, p_218436_3_);
            });
        }

        public static class BlockTables extends BlockLootTables
        {
            @Override
            protected void addTables()
            {
                this.registerDropSelfLootTable(SurvivalistBlocks.RACK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.SAWMILL.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.OAK_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.CHIPPED_OAK_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.DAMAGED_OAK_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.BIRCH_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.CHIPPED_BIRCH_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.DAMAGED_BIRCH_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.SPRUCE_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.CHIPPED_SPRUCE_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.DAMAGED_SPRUCE_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.JUNGLE_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.CHIPPED_JUNGLE_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.DAMAGED_JUNGLE_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.DARK_OAK_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.CHIPPED_DARK_OAK_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.DAMAGED_DARK_OAK_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.ACACIA_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.CHIPPED_ACACIA_CHOPPING_BLOCK.get());
                this.registerDropSelfLootTable(SurvivalistBlocks.DAMAGED_ACACIA_CHOPPING_BLOCK.get());
            }

            @Override
            protected Iterable<Block> getKnownBlocks()
            {
                return ForgeRegistries.BLOCKS.getValues().stream()
                        .filter(b -> b.getRegistryName().getNamespace().equals(MODID))
                        .collect(Collectors.toList());
            }
        }
    }
}