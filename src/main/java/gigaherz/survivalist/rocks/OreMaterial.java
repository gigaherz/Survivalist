package gigaherz.survivalist.rocks;

import net.minecraft.util.IStringSerializable;

import java.util.Arrays;

public enum OreMaterial implements IStringSerializable
{
    IRON("iron", ".iron_rock", ".iron_nugget", true),
    GOLD("gold", ".gold_rock", ".gold_nugget", false),
    COPPER("copper", ".copper_rock", ".copper_nugget", true),
    TIN("tin", ".tin_rock", ".tin_nugget", true),
    LEAD("lead", ".lead_rock", ".lead_nugget", true),
    SILVER("silver", ".silver_rock", ".silver_nugget", true);

    final String name;
    final String oreSuffix;
    final String nuggetSuffix;
    private final boolean needsNugget;

    OreMaterial(String name, String oreSuffix, String nuggetSuffix, boolean needsNugget)
    {
        this.name = name;
        this.oreSuffix = oreSuffix;
        this.nuggetSuffix = nuggetSuffix;
        this.needsNugget = needsNugget;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public String getUnlocalizedOreSuffix()
    {
        return oreSuffix;
    }

    public String getUnlocalizedNuggetSuffix()
    {
        return nuggetSuffix;
    }

    public boolean needsNugget()
    {
        return needsNugget;
    }

    public static OreMaterial[] NUGGETS = Arrays.stream(values()).filter((m) -> m.needsNugget).toArray(OreMaterial[]::new);

}
