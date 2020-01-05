package gigaherz.survivalist.rocks;

import com.google.common.collect.Lists;
import gigaherz.survivalist.SurvivalistItems;
import gigaherz.survivalist.SurvivalistMod;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public enum Rocks implements IStringSerializable
{
    STONE("stone", false, SurvivalistItems.STONE_ROCK),
    ANDESITE("andesite", false, SurvivalistItems.ANDESITE_ROCK),
    DIORITE("diorite", false, SurvivalistItems.DIORITE_ROCK),
    GRANITE("granite", false, SurvivalistItems.GRANITE_ROCK),

    IRON("iron", true, SurvivalistItems.IRON_ORE_ROCK, () -> Items.IRON_NUGGET),
    GOLD("gold", true, SurvivalistItems.GOLD_ORE_ROCK, () -> Items.GOLD_NUGGET),
    COPPER("copper", true, SurvivalistItems.COPPER_ORE_ROCK, SurvivalistItems.COPPER_NUGGET),
    TIN("tin", true, SurvivalistItems.TIN_ORE_ROCK, SurvivalistItems.TIN_NUGGET),
    LEAD("lead", true, SurvivalistItems.LEAD_ORE_ROCK, SurvivalistItems.LEAD_NUGGET),
    SILVER("silver", true, SurvivalistItems.SILVER_ORE_ROCK, SurvivalistItems.SILVER_NUGGET),
    ALUMINUM("aluminum", true, SurvivalistItems.ALUMINUM_ORE_ROCK, SurvivalistItems.ALUMINUM_NUGGET);

    private final String name;
    private final boolean isOre;
    private final RegistryObject<? extends Item> item;
    @Nullable
    private final Supplier<? extends Item> smeltsInto;

    Rocks(String name, boolean isOre, RegistryObject<? extends Item> item)
    {
        this.name = name;
        this.isOre = isOre;
        this.item = item;
        this.smeltsInto = null;
    }

    Rocks(String name, boolean isOre, RegistryObject<? extends Item> item, Supplier<? extends Item> smeltsInto)
    {
        this.name = name;
        this.isOre = isOre;
        this.item = item;
        this.smeltsInto = smeltsInto;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public RegistryObject<? extends Item> getItem()
    {
        return item;
    }

    public Optional<Supplier<? extends Item>> getSmeltsInto()
    {
        return Optional.ofNullable(smeltsInto);
    }

    public List<ResourceLocation> getTagLocations()
    {
        if (isOre)
        {
            return Lists.newArrayList(
                    new ResourceLocation("forge", "small_ores/" + name),
                    new ResourceLocation("forge", "weak_ores/" + name),
                    new ResourceLocation("forge", "ore_rocks/" + name),
                    SurvivalistMod.location("ore_rocks/" + name)
            );
        }
        else
        {
            return Lists.newArrayList(
                    new ResourceLocation("forge", "rocks/" + name),
                    SurvivalistMod.location("rocks/" + name)
            );
        }
    }

    public ResourceLocation getSmeltingTag()
    {
        return SurvivalistMod.location("ore_rocks/" + name);
    }
}
