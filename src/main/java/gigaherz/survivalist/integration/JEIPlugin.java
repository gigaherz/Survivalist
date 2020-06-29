package gigaherz.survivalist.integration;
/*
import gigaherz.survivalist.SurvivalistBlocks;
import gigaherz.survivalist.SurvivalistMod;
import gigaherz.survivalist.api.ChoppingRecipe;
import gigaherz.survivalist.api.DryingRecipe;
import gigaherz.survivalist.chopblock.ChopblockMaterials;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Stream;

@JeiPlugin
public class JEIPlugin implements IModPlugin
{
    private static final ResourceLocation ID = SurvivalistMod.location("jei_plugin");

    @Override
    public ResourceLocation getPluginUid()
    {
        return ID;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        Stream.of(ChopblockMaterials.values()).forEach(v -> {
            registration.addRecipeCatalyst(new ItemStack(v.getPristine().get()), ChoppingCategory.UID);
        });
        registration.addRecipeCatalyst(new ItemStack(SurvivalistBlocks.SAWMILL.get()), ChoppingCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(SurvivalistBlocks.RACK.get()), DryingCategory.UID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(new DryingCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new ChoppingCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        ClientWorld world = Objects.requireNonNull(Minecraft.getInstance().world);
        registration.addRecipes(DryingRecipe.getAllRecipes(world), DryingCategory.UID);
        registration.addRecipes(ChoppingRecipe.getAllRecipes(world), ChoppingCategory.UID);
    }
}*/