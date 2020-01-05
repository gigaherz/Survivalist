package gigaherz.survivalist.chopblock;

import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.SurvivalistMod;
import gigaherz.survivalist.api.ChoppingRecipe;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SurvivalistMod.MODID)
public class ChoppingBlock extends Block
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 8, 16);

    private final Supplier<BlockState> breaksInto;

    public ChoppingBlock(@Nullable Supplier<BlockState> breaksInto, Properties properties)
    {
        super(properties);
        this.breaksInto = breaksInto != null ? breaksInto : (() -> Blocks.AIR.getDefaultState());
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
        return new ChoppingBlockTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public ActionResultType func_225533_a_(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult)
    {
        ItemStack heldItem = player.getHeldItem(hand);

        if (worldIn.isRemote)
        {
            return (heldItem.getCount() <= 0) || ChoppingRecipe.getRecipe(worldIn, heldItem).isPresent() ?
                    ActionResultType.SUCCESS : ActionResultType.PASS;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (!(tileEntity instanceof ChoppingBlockTileEntity) || player.func_225608_bj_())
            return ActionResultType.PASS;

        ChoppingBlockTileEntity chopper = (ChoppingBlockTileEntity) tileEntity;

        if (heldItem.getCount() <= 0)
        {
            ItemStack extracted = chopper.getSlotInventory().extractItem(0, 1, false);
            if (extracted.getCount() > 0)
            {
                ItemHandlerHelper.giveItemToPlayer(player, extracted);
                return ActionResultType.SUCCESS;
            }

            return ActionResultType.PASS;
        }

        if (ChoppingRecipe.getRecipe(worldIn, heldItem)
                .isPresent())
        {
            ItemStack remaining = chopper.getSlotInventory().insertItem(0, heldItem, false);
            if (!player.isCreative())
            {
                if (remaining.getCount() > 0)
                {
                    player.setHeldItem(hand, remaining);
                }
                else
                {
                    player.setHeldItem(hand, ItemStack.EMPTY);
                }
            }
            return remaining.getCount() < heldItem.getCount() ?
                    ActionResultType.SUCCESS : ActionResultType.PASS;
        }

        return ActionResultType.PASS;
    }

    @SubscribeEvent
    public static void interactEvent(PlayerInteractEvent.LeftClickBlock event)
    {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if(block instanceof ChoppingBlock)
        {
            if (((ChoppingBlock)block).interceptClick(world, pos, state, player))
                event.setCanceled(true);
        }
    }

    private boolean interceptClick(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (!(tileentity instanceof ChoppingBlockTileEntity))
            return false;

        ChoppingBlockTileEntity chopper = (ChoppingBlockTileEntity) tileentity;
        if (chopper.getSlotInventory().getStackInSlot(0).getCount() <= 0)
            return false;

        if (worldIn.isRemote)
            return true;

        ItemStack heldItem = playerIn.getHeldItem(Hand.MAIN_HAND);

        int harvestLevel = heldItem.getItem().getHarvestLevel(heldItem, ToolType.AXE, playerIn, null);
        if (chopper.chop(playerIn, harvestLevel, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItem)))
        {
            if (worldIn.rand.nextFloat() < ConfigManager.SERVER.choppingDegradeChance.get())
            {
                worldIn.setBlockState(pos, breaksInto.get());
            }

            if (ConfigManager.SERVER.choppingExhaustion.get() > 0)
                playerIn.addExhaustion(ConfigManager.SERVER.choppingExhaustion.get().floatValue());

            if (heldItem.getCount() > 0 && !playerIn.abilities.isCreativeMode)
            {
                heldItem.damageItem(1, playerIn, (stack) -> {
                    stack.sendBreakAnimation(Hand.MAIN_HAND);
                });
            }
        }

        return true;
    }

    /*@Override
    public void breakBlock(World worldIn, BlockPos pos, BlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileChopping)
        {
            dropInventoryItems(worldIn, pos, ((TileChopping) tileentity).getSlotInventory());
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

     */

    public static void dropInventoryItems(World worldIn, BlockPos pos, IItemHandler inventory)
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
