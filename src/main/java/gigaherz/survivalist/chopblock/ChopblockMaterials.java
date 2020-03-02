package gigaherz.survivalist.chopblock;

import gigaherz.survivalist.SurvivalistBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public enum ChopblockMaterials implements IStringSerializable
{
    OAK("oak", SurvivalistBlocks.OAK_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_OAK_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_OAK_CHOPPING_BLOCK, "oak_logs"),
    BIRCH("birch", SurvivalistBlocks.BIRCH_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_BIRCH_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_BIRCH_CHOPPING_BLOCK, "birch_logs"),
    SPRUCE("spruce", SurvivalistBlocks.SPRUCE_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_SPRUCE_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_SPRUCE_CHOPPING_BLOCK, "spruce_logs"),
    JUNGLE("jungle", SurvivalistBlocks.JUNGLE_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_JUNGLE_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_JUNGLE_CHOPPING_BLOCK, "jungle_logs"),
    DARK_OAK("dark_oak", SurvivalistBlocks.DARK_OAK_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_DARK_OAK_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_DARK_OAK_CHOPPING_BLOCK, "dark_oak_logs"),
    ACACIA("acacia", SurvivalistBlocks.ACACIA_CHOPPING_BLOCK, SurvivalistBlocks.CHIPPED_ACACIA_CHOPPING_BLOCK, SurvivalistBlocks.DAMAGED_ACACIA_CHOPPING_BLOCK, "acacia_logs");

    private final String name;
    private final RegistryObject<ChoppingBlock> pristine;
    private final RegistryObject<ChoppingBlock> chipped;
    private final RegistryObject<ChoppingBlock> damaged;
    private final ResourceLocation madeFrom;

    ChopblockMaterials(String name, RegistryObject<ChoppingBlock> pristine, RegistryObject<ChoppingBlock> chipped, RegistryObject<ChoppingBlock> damaged, String madeFrom)
    {
        this.name = name;
        this.pristine = pristine;
        this.chipped = chipped;
        this.damaged = damaged;
        this.madeFrom = new ResourceLocation(madeFrom);
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

    public ResourceLocation getMadeFrom()
    {
        return madeFrom;
    }
}
