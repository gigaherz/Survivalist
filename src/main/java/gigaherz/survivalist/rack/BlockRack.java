package gigaherz.survivalist.rack;

import gigaherz.survivalist.GuiHandler;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockRack extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockRack()
    {
        super(Material.wood);
        setUnlocalizedName(Survivalist.MODID + ".rack");
        setCreativeTab(CreativeTabs.tabBlock);
        setStepSound(soundTypeMetal);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        setHardness(1.0F);
        setResistance(1.0F);
        setLightOpacity(0);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileRack();
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FACING, EnumFacing.HORIZONTALS[meta & 3]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).ordinal();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (!(tileEntity instanceof TileRack) || playerIn.isSneaking())
            return false;

        playerIn.openGui(Survivalist.instance, GuiHandler.GUI_RACK, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }
}
