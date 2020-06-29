package gigaherz.survivalist;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import gigaherz.survivalist.chopblock.ChopblockMaterials;
import gigaherz.survivalist.chopblock.ChoppingBlock;
import gigaherz.survivalist.rocks.Nuggets;
import gigaherz.survivalist.rocks.Rocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.loot.*;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
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

            BlockTags blockTags = new BlockTags(gen);
            gen.addProvider(blockTags);
            gen.addProvider(new ItemTags(gen, blockTags));
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
                    .forEach(rock -> {
                        ITag<Item> tag = makeItemTag(rock.getMadeFrom());
                        RegistryObject<ChoppingBlock> result = rock.getPristine();
                        ShapelessRecipeBuilder
                                .shapelessRecipe(result.get())
                                .addIngredient(tag)
                                .addCriterion("has_rock", hasItem(tag))
                                .build(consumer);
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
                                                    .withRewards(AdvancementRewards.Builder.recipe(saddleRecipeId))
                                                    .withRequirementsStrategy(IRequirementsStrategy.OR)
                                                    .withCriterion("has_leather", hasItem(leatherTag))
                                                    .withCriterion("has_the_recipe", RecipeUnlockedTrigger.func_235675_a_(saddleRecipeId))
                                    )
                    )
                    .build(consumer, saddleRecipeId);
        }
    }

    private static class ItemTags extends ItemTagsProvider implements IDataProvider
    {
        public ItemTags(DataGenerator gen, BlockTags blockTags)
        {
            super(gen, blockTags);
        }

        @Override
        protected void registerTags()
        {
            Arrays.stream(Rocks.values())
                    .flatMap(rock -> rock.getTagLocations().stream().map(tag -> Pair.of(rock.getItem().get(), tag)))
                    .forEach((pair) -> this.func_240522_a_(makeItemTag(pair.getSecond())).func_240534_a_(pair.getFirst()));

            Arrays.stream(Nuggets.values())
                    .flatMap(rock -> rock.getTagLocations().stream().map(tag -> Pair.of(rock.getItem().get(), tag)))
                    .forEach((pair) -> this.func_240522_a_(makeItemTag(pair.getSecond())).func_240534_a_(pair.getFirst()));

            this.func_240522_a_(makeItemTag(SurvivalistMod.location("dough")))
                    .func_240534_a_(SurvivalistItems.DOUGH.get());

            this.func_240522_a_(makeItemTag(SurvivalistMod.location("chopping_blocks")))
                    .func_240534_a_(Arrays.stream(ChopblockMaterials.values())
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
            this.func_240522_a_(net.minecraft.tags.BlockTags.makeWrapperTag(SurvivalistMod.location("chopping_blocks").toString()))
                    .func_240534_a_(Arrays.stream(ChopblockMaterials.values())
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
                LootTableManager.func_227508_a_(validationtracker, p_218436_2_, p_218436_3_);
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
                        .filter(b -> b.getRegistryName().getNamespace().equals(SurvivalistMod.MODID))
                        .collect(Collectors.toList());
            }
        }
    }
}
