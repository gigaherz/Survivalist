package gigaherz.survivalist.rocks;

import net.minecraft.util.IStringSerializable;

public enum RockMaterial implements IStringSerializable
{
    NORMAL("normal", ".rock"),
    ANDESITE("andesite", ".rock_andesite"),
    DIORITE("diorite", ".rock_diorite"),
    GRANITE("granite", ".rock_granite");

    final String name;
    final String unlocalizedSuffix;

    RockMaterial(String name, String unlocalizedSuffix)
    {
        this.name = name;
        this.unlocalizedSuffix = unlocalizedSuffix;
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
}
