package gigaherz.survivalist.sawmill;

import gigaherz.common.BlockRegistered;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.GuiHandler;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSawmill extends BlockRegistered
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockSawmill(String name)
    {
        super(name, Material.ROCK);
        setHardness(3.5F);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setDefaultState(getBlockState().getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(POWERED,false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, POWERED);
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(POWERED) ? 8 : 0) |
                state.getValue(FACING).getIndex();
    }

    @Deprecated
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileSawmill)
        {
            return state.withProperty(POWERED, ((TileSawmill)tileentity).isBurning());
        }

        return state;
    }

    @Deprecated
    @Override
    public int getLightValue(IBlockState state)
    {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        IBlockState other = world.getBlockState(pos);
        if (other.getBlock() != this)
        {
            return other.getLightValue(world, pos);
        }
        return state.getActualState(world, pos).getValue(POWERED) ? 15 : 0;
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("incomplete-switch")
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getActualState(worldIn, pos).getValue(POWERED))
        {
            EnumFacing enumfacing = stateIn.getValue(FACING);
            double x = (double)pos.getX() + 0.5D;
            double y = (double)pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double z = (double)pos.getZ() + 0.5D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;

            if (rand.nextDouble() < 0.1D)
            {
                worldIn.playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            switch (enumfacing)
            {
                case WEST:
                    z += d4;
                    x -= 0.52D;
                    break;
                case EAST:
                    z += d4;
                    x += 0.52D;
                    break;
                case NORTH:
                    x += d4;
                    z -= 0.52D;
                    break;
                case SOUTH:
                    x += d4;
                    z += 0.52D;
            }

            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x , y, z, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
            return true;

        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileSawmill)
            playerIn.openGui(Survivalist.instance, GuiHandler.GUI_SAWMILL, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileSawmill();
    }

    @Deprecated
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        /*if (stack.hasDisplayName())
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileSawmill)
            {
                ((TileSawmill)tileentity).setCustomInventoryName(stack.getDisplayName());
            }
        }*/
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileSawmill)
        {
            dropInventoryItems(worldIn, pos, ((TileSawmill)te).getInventory());
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    private static void dropInventoryItems(World worldIn, BlockPos pos, IItemHandler inventory)
    {
        for (int i = 0; i < inventory.getSlots(); ++i)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (itemstack.getCount() > 0)
            {
                InventoryHelper.spawnItemStack(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), itemstack);
            }
        }
    }

    @Deprecated
    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Deprecated
    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileSawmill)
            return ItemHandlerHelper.calcRedstoneFromInventory(((TileSawmill)te).getInventory());
        return 0;
    }

    @Deprecated
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Deprecated
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }
}
