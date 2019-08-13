package gigaherz.survivalist.sawmill;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSawmill extends Block
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public BlockSawmill(Properties properties)
    {
        super(properties);
        setDefaultState(getStateContainer().getBaseState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, POWERED);
    }

    @Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos)
    {
        return state.get(POWERED) ? 15 : 0;
    }

    @Deprecated
    @Override
    public int getLightValue(BlockState state)
    {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.get(POWERED))
        {
            Direction enumfacing = stateIn.get(FACING);
            double x = (double) pos.getX() + 0.5D;
            double y = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double z = (double) pos.getZ() + 0.5D;
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

            worldIn.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
            worldIn.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (worldIn.isRemote)
            return true;

        TileEntity te = worldIn.getTileEntity(pos);

        // TODO
        //if (te instanceof TileSawmill)
        //    player.openGui(Survivalist.instance, GuiHandler.GUI_SAWMILL, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new TileSawmill();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileSawmill)
        {
            dropInventoryItems(worldIn, pos, ((TileSawmill) te).getInventory());
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
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
    public boolean hasComparatorInputOverride(BlockState state)
    {
        return true;
    }

    @Deprecated
    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileSawmill)
            return ItemHandlerHelper.calcRedstoneFromInventory(((TileSawmill) te).getInventory());
        return 0;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.with(FACING, mirrorIn.toRotation(state.get(FACING)).rotate(state.get(FACING)));
    }
}
