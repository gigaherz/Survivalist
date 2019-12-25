package gigaherz.survivalist.rack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class DryingRackBlock extends Block
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final VoxelShape shape1;
    private final VoxelShape shape2;
    {
        {
            VoxelShape bar1 = Block.makeCuboidShape(1, 14, 3, 15, 15, 4);
            VoxelShape bar2 = Block.makeCuboidShape(1, 14, 6, 15, 15, 7);
            VoxelShape bar3 = Block.makeCuboidShape(1, 14, 9, 15, 15, 10);
            VoxelShape bar4 = Block.makeCuboidShape(1, 14, 12, 15, 15, 13);
            VoxelShape end1 = Block.makeCuboidShape(0, 13, 1, 1, 16, 15);
            VoxelShape end2 = Block.makeCuboidShape(15, 13, 1, 16, 16, 15);

            VoxelShape side1_1_1 = Block.makeCuboidShape(0, 0, 1, 3, 3, 2);
            VoxelShape side1_1_2 = Block.makeCuboidShape(1, 1, 1, 4, 4, 2);
            VoxelShape side1_1 = VoxelShapes.or(side1_1_1, side1_1_2);
            VoxelShape side1_2_1 = Block.makeCuboidShape(13, 0, 0, 16, 3, 1);
            VoxelShape side1_2_2 = Block.makeCuboidShape(12, 1, 0, 15, 4, 1);
            VoxelShape side1_2 = VoxelShapes.or(side1_2_1, side1_2_2);
            VoxelShape side2_1_1 = Block.makeCuboidShape(0, 0, 15, 3, 3, 16);
            VoxelShape side2_1_2 = Block.makeCuboidShape(1, 1, 15, 4, 4, 16);
            VoxelShape side2_1 = VoxelShapes.or(side2_1_1, side2_1_2);
            VoxelShape side2_2_1 = Block.makeCuboidShape(13, 0, 14, 16, 3, 15);
            VoxelShape side2_2_2 = Block.makeCuboidShape(12, 1, 14, 15, 4, 15);
            VoxelShape side2_2 = VoxelShapes.or(side2_2_1, side2_2_2);
            for (int i = 2; i <= 13; i++)
            {
                VoxelShape side1_1_3 = Block.makeCuboidShape(i, i, 1, i + 3, i + 3, 2);
                side1_1 = VoxelShapes.or(side1_1, side1_1_3);
                VoxelShape side1_2_3 = Block.makeCuboidShape(13 - i, i, 0, 13 - i + 3, i + 3, 1);
                side1_2 = VoxelShapes.or(side1_2, side1_2_3);
                VoxelShape side2_1_3 = Block.makeCuboidShape(i, i, 15, i + 3, i + 3, 16);
                side2_1 = VoxelShapes.or(side2_1, side2_1_3);
                VoxelShape side2_2_3 = Block.makeCuboidShape(13 - i, i, 14, 13 - i + 3, i + 3, 15);
                side2_2 = VoxelShapes.or(side2_2, side2_2_3);
            }
            VoxelShape side1 = VoxelShapes.or(side1_1, side1_2);
            VoxelShape side2 = VoxelShapes.or(side2_1, side2_2);

            shape1 = VoxelShapes.or(
                    bar1, bar2, bar3, bar4,
                    end1, end2,
                    side1, side2
            );
        }

        {
            VoxelShape bar1b = Block.makeCuboidShape(3, 14, 1, 4, 15, 15);
            VoxelShape bar2b = Block.makeCuboidShape(6, 14, 1, 7, 15, 15);
            VoxelShape bar3b = Block.makeCuboidShape(9, 14, 1, 10, 15, 15);
            VoxelShape bar4b = Block.makeCuboidShape(12, 14, 1, 13, 15, 15);
            VoxelShape end1b = Block.makeCuboidShape(1, 13, 0, 15, 16, 1);
            VoxelShape end2b = Block.makeCuboidShape(1, 13, 15, 15, 16, 16);

            VoxelShape side1b_1_1 = Block.makeCuboidShape(0, 0, 0, 1, 3, 3);
            VoxelShape side1b_1_2 = Block.makeCuboidShape(0, 1, 1, 1, 4, 4);
            VoxelShape side1b_1 = VoxelShapes.or(side1b_1_1, side1b_1_2);
            VoxelShape side1b_2_1 = Block.makeCuboidShape(1, 0, 13, 2, 3, 16);
            VoxelShape side1b_2_2 = Block.makeCuboidShape(1, 1, 12, 2, 4, 15);
            VoxelShape side1b_2 = VoxelShapes.or(side1b_2_1, side1b_2_2);
            VoxelShape side2b_1_1 = Block.makeCuboidShape(14, 0, 0, 15, 3, 3);
            VoxelShape side2b_1_2 = Block.makeCuboidShape(14, 1, 1, 15, 4, 4);
            VoxelShape side2b_1 = VoxelShapes.or(side2b_1_1, side2b_1_2);
            VoxelShape side2b_2_1 = Block.makeCuboidShape(15, 0, 13, 16, 3, 16);
            VoxelShape side2b_2_2 = Block.makeCuboidShape(15, 1, 12, 16, 4, 15);
            VoxelShape side2b_2 = VoxelShapes.or(side2b_2_1, side2b_2_2);
            for (int i = 2; i <= 13; i++)
            {
                VoxelShape side1b_1_3 = Block.makeCuboidShape(0, i, i, 1, i + 3, i + 3);
                side1b_1 = VoxelShapes.or(side1b_1, side1b_1_3);
                VoxelShape side1b_2_3 = Block.makeCuboidShape(1, i, 13 - i, 2, i + 3, 13 - i + 3);
                side1b_2 = VoxelShapes.or(side1b_2, side1b_2_3);
                VoxelShape side2b_1_3 = Block.makeCuboidShape(14, i, i, 15, i + 3, i + 3);
                side2b_1 = VoxelShapes.or(side2b_1, side2b_1_3);
                VoxelShape side2b_2_3 = Block.makeCuboidShape(15, i, 13 - i, 16, i + 3, 13 - i + 3);
                side2b_2 = VoxelShapes.or(side2b_2, side2b_2_3);
            }
            VoxelShape side1b = VoxelShapes.or(side1b_1, side1b_2);
            VoxelShape side2b = VoxelShapes.or(side2b_1, side2b_2);

            shape2 = VoxelShapes.or(
                    bar1b, bar2b, bar3b, bar4b,
                    end1b, end2b,
                    side1b, side2b
            );
        }
    }

    public DryingRackBlock(Properties properties)
    {
        super(properties);
        setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return (state.get(FACING).getHorizontalIndex()%2) == 0? shape1:shape2;
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
        return new DryingRackTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public ActionResultType func_225533_a_(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult)
    {
        if (worldIn.isRemote)
            return ActionResultType.SUCCESS;

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof INamedContainerProvider))
            return ActionResultType.FAIL;

        NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)tileEntity);

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (newState.getBlock() != state.getBlock())
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof DryingRackTileEntity)
            {
                dropInventoryItems(worldIn, pos, ((DryingRackTileEntity) tileentity).inventory());
                worldIn.updateComparatorOutputLevel(pos, this);
            }
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
}
