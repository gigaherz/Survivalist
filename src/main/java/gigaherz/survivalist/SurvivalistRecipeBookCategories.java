package gigaherz.survivalist;

import net.minecraftforge.common.util.NonNullLazy;

public class SurvivalistRecipeBookCategories
{
    private static final NonNullLazy<SurvivalistRecipeBookCategories> INSTANCE = NonNullLazy.of(SurvivalistRecipeBookCategories::new);

    public static SurvivalistRecipeBookCategories instance()
    {
        return INSTANCE.get();
    }

    /*
    public final RecipeBookCategories SAWMILL_SEARCH = RecipeBookCategories.create("SAWMILL_SEARCH", false, new ItemStack(Items.COMPASS));
    public final RecipeBookCategories SAWMILL = RecipeBookCategories.create("SAWMILL", false, new ItemStack(Items.OAK_PLANKS));

    public SurvivalistRecipeBookCategories()
    {
        ForgeHooksClient.setSearchCategoryForCategory(SAWMILL, SAWMILL_SEARCH);

        ForgeHooksClient.setRecipeCategoryMapper(ChoppingRecipe.CHOPPING, (recipe) -> SAWMILL);
    }
     */
}