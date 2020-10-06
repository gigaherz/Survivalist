package gigaherz.survivalist;

import gigaherz.survivalist.chopblock.ChoppingBlockTileEntity;
import gigaherz.survivalist.rack.DryingRackTileEntity;
import gigaherz.survivalist.sawmill.SawmillTileEntity;
import gigaherz.survivalist.util.RegSitter;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

public class SurvivalistTileEntityTypes
{
    static final RegSitter HELPER = new RegSitter(SurvivalistMod.MODID);

    public static final RegistryObject<TileEntityType<DryingRackTileEntity>> DRYING_RACK_TILE_ENTITY_TYPE = HELPER.tileEntity("rack");

    public static final RegistryObject<TileEntityType<SawmillTileEntity>> SAWMILL_RACK_TILE_ENTITY_TYPE = HELPER.tileEntity("sawmill");

    public static final RegistryObject<TileEntityType<ChoppingBlockTileEntity>> CHOPPING_BLOCK_TILE_ENTITY_TYPE = HELPER.tileEntity("chopping_block", ChoppingBlockTileEntity::new,
            SurvivalistBlocks.OAK_CHOPPING_BLOCK,
            SurvivalistBlocks.BIRCH_CHOPPING_BLOCK,
            SurvivalistBlocks.SPRUCE_CHOPPING_BLOCK,
            SurvivalistBlocks.JUNGLE_CHOPPING_BLOCK,
            SurvivalistBlocks.DARK_OAK_CHOPPING_BLOCK,
            SurvivalistBlocks.ACACIA_CHOPPING_BLOCK,
            SurvivalistBlocks.CHIPPED_OAK_CHOPPING_BLOCK,
            SurvivalistBlocks.CHIPPED_BIRCH_CHOPPING_BLOCK,
            SurvivalistBlocks.CHIPPED_SPRUCE_CHOPPING_BLOCK,
            SurvivalistBlocks.CHIPPED_JUNGLE_CHOPPING_BLOCK,
            SurvivalistBlocks.CHIPPED_DARK_OAK_CHOPPING_BLOCK,
            SurvivalistBlocks.CHIPPED_ACACIA_CHOPPING_BLOCK,
            SurvivalistBlocks.DAMAGED_OAK_CHOPPING_BLOCK,
            SurvivalistBlocks.DAMAGED_BIRCH_CHOPPING_BLOCK,
            SurvivalistBlocks.DAMAGED_SPRUCE_CHOPPING_BLOCK,
            SurvivalistBlocks.DAMAGED_JUNGLE_CHOPPING_BLOCK,
            SurvivalistBlocks.DAMAGED_DARK_OAK_CHOPPING_BLOCK,
            SurvivalistBlocks.DAMAGED_ACACIA_CHOPPING_BLOCK).defer();
}