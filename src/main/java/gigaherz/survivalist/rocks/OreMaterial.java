package gigaherz.survivalist.rocks;

import net.minecraft.util.IStringSerializable;

import java.util.Arrays;

public enum OreMaterial implements IStringSerializable
{
    IRON("iron", false),
    GOLD("gold", false),
    COPPER("copper", true),
    TIN("tin", true),
    LEAD("lead", true),
    SILVER("silver", true),
    ALUMINUM("aluminum", true);

    final String name;
    private final boolean needsNugget;

    OreMaterial(String name, boolean needsNugget)
    {
        this.name = name;
        this.needsNugget = needsNugget;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public boolean needsNugget()
    {
        return needsNugget;
    }

    public static OreMaterial[] NUGGETS = Arrays.stream(values()).filter((m) -> m.needsNugget).toArray(OreMaterial[]::new);
}
