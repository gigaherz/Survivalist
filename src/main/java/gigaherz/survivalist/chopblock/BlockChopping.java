package gigaherz.survivalist.chopblock;

import gigaherz.survivalist.base.BlockRegistered;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class BlockChopping extends BlockRegistered
{
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

    public BlockChopping(String name)
    {
        super(name, Material.WOOD);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setSoundType(SoundType.WOOD);
        setHardness(5.0F);
        setResistance(1.0F);
        setLightOpacity(0);
        setHarvestLevel("axe", 0);
    }

    @Deprecated
    @Override
    public boolean isOpaqueCube(IBlockState state)
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
        return new TileChopping();
    }

    @Deprecated
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (!(tileEntity instanceof TileChopping) || playerIn.isSneaking())
            return false;

        TileChopping chopper = (TileChopping) tileEntity;

        if (heldItem == null)
        {
            ItemStack extracted = chopper.getSlotInventory().extractItem(0, 1, false);
            if (extracted != null && extracted.stackSize > 0)
            {
                ItemHandlerHelper.giveItemToPlayer(playerIn, extracted);
                return true;
            }

            return false;
        }

        if (TileChopping.isValidInput(heldItem))
        {
            ItemStack remaining = chopper.getSlotInventory().insertItem(0, heldItem, false);
            if (!playerIn.isCreative())
            {
                if (remaining != null && remaining.stackSize > 0)
                {
                    playerIn.setHeldItem(hand, remaining);
                }
                else
                {
                    playerIn.setHeldItem(hand, null);
                }
            }
            return remaining == null || remaining.stackSize < heldItem.stackSize;
        }

        return false;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileChopping)
        {
            TileChopping chopper = (TileChopping) tileentity;
            ItemStack heldItem = playerIn.getHeldItem(EnumHand.MAIN_HAND);
            assert heldItem != null;

            int harvestLevel = heldItem.getItem().getHarvestLevel(heldItem, "axe");
            if (harvestLevel >= 0)
            {
                chopper.chop(playerIn, harvestLevel, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItem));
            }
        }

        super.onBlockClicked(worldIn, pos, playerIn);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (!(tileentity instanceof TileChopping))
            return;

        TileChopping chopper = (TileChopping) tileentity;

        dropInventoryItems(worldIn, pos, chopper.getSlotInventory());
        worldIn.updateComparatorOutputLevel(pos, this);

        super.breakBlock(worldIn, pos, state);
    }

    public static void dropInventoryItems(World worldIn, BlockPos pos, IItemHandler inventory)
    {
        for (int i = 0; i < inventory.getSlots(); ++i)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (itemstack != null)
            {
                InventoryHelper.spawnItemStack(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), itemstack);
            }
        }
    }
}
