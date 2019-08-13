package gigaherz.survivalist.rocks;

import net.minecraft.util.IStringSerializable;

public enum RockMaterial implements IStringSerializable
{
    NORMAL("normal"),
    ANDESITE("andesite"),
    DIORITE("diorite"),
    GRANITE("granite");

    final String name;

    RockMaterial(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
