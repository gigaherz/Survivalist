package gigaherz.survivalist;

import gigaherz.survivalist.chopblock.ChoppingBlock;
import gigaherz.survivalist.rack.DryingRackBlock;
import gigaherz.survivalist.rack.DryingRackTileEntity;
import gigaherz.survivalist.sawmill.SawmillBlock;
import gigaherz.survivalist.sawmill.SawmillTileEntity;
import gigaherz.survivalist.util.RegSitter;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;

public class SurvivalistBlocks
{
    static final RegSitter HELPER = new RegSitter(SurvivalistMod.MODID);

    public static final RegistryObject<DryingRackBlock> RACK = HELPER
            .block("rack", () -> new DryingRackBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(1.0f)))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).withTileEntity(DryingRackTileEntity::new).defer();

    public static final RegistryObject<SawmillBlock> SAWMILL = HELPER
            .block("sawmill", () -> new SawmillBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.5F).sound(SoundType.STONE)))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).withTileEntity(SawmillTileEntity::new).defer();

    public static final RegistryObject<ChoppingBlock> OAK_CHOPPING_BLOCK = HELPER
            .block("oak_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.CHIPPED_OAK_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> CHIPPED_OAK_CHOPPING_BLOCK = HELPER
            .block("chipped_oak_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.DAMAGED_OAK_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> DAMAGED_OAK_CHOPPING_BLOCK = HELPER
            .block("damaged_oak_chopping_block", SurvivalistBlocks::getChoppingBlock)
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> BIRCH_CHOPPING_BLOCK = HELPER
            .block("birch_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.CHIPPED_BIRCH_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> CHIPPED_BIRCH_CHOPPING_BLOCK = HELPER
            .block("chipped_birch_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.DAMAGED_BIRCH_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> DAMAGED_BIRCH_CHOPPING_BLOCK = HELPER
            .block("damaged_birch_chopping_block", SurvivalistBlocks::getChoppingBlock)
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> SPRUCE_CHOPPING_BLOCK = HELPER
            .block("spruce_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.CHIPPED_SPRUCE_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> CHIPPED_SPRUCE_CHOPPING_BLOCK = HELPER
            .block("chipped_spruce_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.DAMAGED_SPRUCE_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> DAMAGED_SPRUCE_CHOPPING_BLOCK = HELPER
            .block("damaged_spruce_chopping_block", SurvivalistBlocks::getChoppingBlock)
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> JUNGLE_CHOPPING_BLOCK = HELPER
            .block("jungle_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.CHIPPED_JUNGLE_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> CHIPPED_JUNGLE_CHOPPING_BLOCK = HELPER
            .block("chipped_jungle_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.DAMAGED_JUNGLE_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> DAMAGED_JUNGLE_CHOPPING_BLOCK = HELPER
            .block("damaged_jungle_chopping_block", SurvivalistBlocks::getChoppingBlock)
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> DARK_OAK_CHOPPING_BLOCK = HELPER
            .block("dark_oak_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.CHIPPED_DARK_OAK_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> CHIPPED_DARK_OAK_CHOPPING_BLOCK = HELPER
            .block("chipped_dark_oak_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.DAMAGED_DARK_OAK_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> DAMAGED_DARK_OAK_CHOPPING_BLOCK = HELPER
            .block("damaged_dark_oak_chopping_block", SurvivalistBlocks::getChoppingBlock)
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> ACACIA_CHOPPING_BLOCK = HELPER
            .block("acacia_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.CHIPPED_ACACIA_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> CHIPPED_ACACIA_CHOPPING_BLOCK = HELPER
            .block("chipped_acacia_chopping_block", () -> getChoppingBlock(SurvivalistBlocks.DAMAGED_ACACIA_CHOPPING_BLOCK))
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    public static final RegistryObject<ChoppingBlock> DAMAGED_ACACIA_CHOPPING_BLOCK = HELPER
            .block("damaged_acacia_chopping_block", SurvivalistBlocks::getChoppingBlock)
            .withItem(new Item.Properties().group(SurvivalistMod.SURVIVALIST_ITEMS)).defer();

    private static ChoppingBlock getChoppingBlock()
    {
        return new ChoppingBlock(null, defaultChopBlockProperties());
    }

    private static ChoppingBlock getChoppingBlock(RegistryObject<ChoppingBlock> breaksInto)
    {
        return new ChoppingBlock(() -> breaksInto.get().getDefaultState(), defaultChopBlockProperties());
    }

    private static Block.Properties defaultChopBlockProperties()
    {
        return Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(5.0f).harvestTool(ToolType.AXE).harvestLevel(0);
    }
}
