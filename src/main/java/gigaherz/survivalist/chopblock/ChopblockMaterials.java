package gigaherz.survivalist.chopblock;

import gigaherz.survivalist.SurvivalistBlocks;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.RegistryObject;

public enum ChopblockMaterials implements IStringSerializable
{
    OAK("oak", SurvivalistBlocks.OAK_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_OAK_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_OAK_CHOPPING_BLOCK),
    BIRCH("birch", SurvivalistBlocks.BIRCH_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_BIRCH_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_BIRCH_CHOPPING_BLOCK),
    SPRUCE("spruce", SurvivalistBlocks.SPRUCE_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_SPRUCE_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_SPRUCE_CHOPPING_BLOCK),
    JUNGLE("jungle", SurvivalistBlocks.JUNGLE_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_JUNGLE_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_JUNGLE_CHOPPING_BLOCK),
    DARK_OAK("dark_oak", SurvivalistBlocks.DARK_OAK_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_DARK_OAK_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_DARK_OAK_CHOPPING_BLOCK),
    ACACIA("acacia", SurvivalistBlocks.ACACIA_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_ACACIA_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_ACACIA_CHOPPING_BLOCK);

    private final String name;
    private final RegistryObject<ChoppingBlock> pristine;
    private final RegistryObject<ChoppingBlock> chipped;
    private final RegistryObject<ChoppingBlock> damaged;

    ChopblockMaterials(String name, RegistryObject<ChoppingBlock> pristine, RegistryObject<ChoppingBlock> chipped, RegistryObject<ChoppingBlock> damaged)
    {
        this.name = name;
        this.pristine = pristine;
        this.chipped = chipped;
        this.damaged = damaged;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public RegistryObject<ChoppingBlock> getPristine()
    {
        return pristine;
    }

    public RegistryObject<ChoppingBlock> getChipped()
    {
        return chipped;
    }

    public RegistryObject<ChoppingBlock> getDamaged()
    {
        return damaged;
    }
}
