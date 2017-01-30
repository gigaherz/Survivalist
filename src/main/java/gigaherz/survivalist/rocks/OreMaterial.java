package gigaherz.survivalist.rocks;

import net.minecraft.util.IStringSerializable;

import java.util.Arrays;

public enum OreMaterial implements IStringSerializable
{
    IRON("iron", ".iron_rock", true),
    GOLD("gold", ".gold_rock", false),
    COPPER("copper", ".copper_rock", true),
    TIN("tin", ".tin_rock", true),
    LEAD("lead", ".lead_rock", true),
    SILVER("silver", ".silver_rock", true);

    final String name;
    final String unlocalizedSuffix;
    private final boolean needsNugget;

    OreMaterial(String name, String unlocalizedSuffix, boolean needsNugget)
    {
        this.name = name;
        this.unlocalizedSuffix = unlocalizedSuffix;
        this.needsNugget = needsNugget;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public String getUnlocalizedSuffix()
    {
        return unlocalizedSuffix;
    }

    public boolean needsNugget()
    {
        return needsNugget;
    }

    public static OreMaterial[] NUGGETS = Arrays.stream(values()).filter((m) -> m.needsNugget).toArray(OreMaterial[]::new);

}
