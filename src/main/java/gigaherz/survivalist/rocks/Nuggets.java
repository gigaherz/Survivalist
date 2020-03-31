package gigaherz.survivalist.rocks;

import com.google.common.collect.Lists;
import gigaherz.survivalist.SurvivalistItems;
import gigaherz.survivalist.SurvivalistMod;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Supplier;

public enum Nuggets implements IStringSerializable
{
    COPPER("copper", SurvivalistItems.COPPER_NUGGET),
    TIN("tin", SurvivalistItems.TIN_NUGGET),
    LEAD("lead", SurvivalistItems.LEAD_NUGGET),
    SILVER("silver", SurvivalistItems.SILVER_NUGGET),
    ALUMINUM("aluminum", SurvivalistItems.ALUMINUM_NUGGET);

    private final String name;
    private final Supplier<? extends Item> item;

    Nuggets(String name, Supplier<? extends Item> item)
    {
        this.name = name;
        this.item = item;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public Supplier<? extends Item> getItem()
    {
        return item;
    }

    public List<ResourceLocation> getTagLocations()
    {
        return Lists.newArrayList(
                new ResourceLocation("forge", "nuggets/" + name),
                SurvivalistMod.location("nuggets/" + name)
        );
    }
}
