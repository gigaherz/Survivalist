package gigaherz.survivalist.chopblock;

import gigaherz.common.BlockRegistered;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.api.Choppable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class BlockChopping extends BlockRegistered
{
    public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 2);
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

    private static final String[] subNames = {
            ".pristine_chopping_block", ".used_chopping_block", ".weathered_chopping_block"
    };

    public BlockChopping(String name)
    {
        super(name, Material.WOOD);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setSoundType(SoundType.WOOD);
        setHardness(5.0F);
        setResistance(5.0F);
        setLightOpacity(0);
        setHarvestLevel("axe", 0);
        setDefaultState(this.blockState.getBaseState().withProperty(DAMAGE, 0));
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        if (ConfigManager.instance.enableChopping)
        {
            for (int i = 0; i < subNames.length; i++)
            {
                list.add(new ItemStack(this, 1, i));
            }
        }
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
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, DAMAGE);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(DAMAGE);
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(DAMAGE, (meta & 3) % 3);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(DAMAGE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (!(tileEntity instanceof TileChopping) || playerIn.isSneaking())
            return false;

        TileChopping chopper = (TileChopping) tileEntity;

        ItemStack heldItem = playerIn.getHeldItem(hand);

        if (heldItem == ItemStack.EMPTY)
        {
            ItemStack extracted = chopper.getSlotInventory().extractItem(0, 1, false);
            if (extracted.getCount() > 0)
            {
                ItemHandlerHelper.giveItemToPlayer(playerIn, extracted);
                return true;
            }

            return false;
        }

        if (Choppable.isValidInput(heldItem))
        {
            ItemStack remaining = chopper.getSlotInventory().insertItem(0, heldItem, false);
            if (!playerIn.isCreative())
            {
                if (remaining.getCount() > 0)
                {
                    playerIn.setHeldItem(hand, remaining);
                }
                else
                {
                    playerIn.setHeldItem(hand, ItemStack.EMPTY);
                }
            }
            return remaining.getCount() < heldItem.getCount();
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

            int harvestLevel = getAxeLevel(heldItem, playerIn);
            if (chopper.chop(playerIn, harvestLevel, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItem)))
            {
                if (worldIn.rand.nextFloat() < ConfigManager.instance.choppingDegradeChance)
                {
                    int damage = worldIn.getBlockState(pos).getValue(DAMAGE);
                    if (damage < 2)
                    {
                        worldIn.setBlockState(pos, getDefaultState().withProperty(DAMAGE, damage + 1));
                    }
                    else
                    {
                        worldIn.setBlockToAir(pos);
                    }
                }

                if (ConfigManager.instance.choppingExhaustion > 0)
                    playerIn.addExhaustion(ConfigManager.instance.choppingExhaustion);

                if (heldItem.getCount() > 0 && !playerIn.capabilities.isCreativeMode)
                {
                    heldItem.damageItem(1, playerIn);
                    if (heldItem.getCount() <= 0)
                    {
                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(playerIn, heldItem, EnumHand.MAIN_HAND);
                        playerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
            }
        }

        super.onBlockClicked(worldIn, pos, playerIn);
    }

    private int getAxeLevel(@Nullable ItemStack heldItem, @Nullable EntityPlayer playerIn)
    {
        if (heldItem == null) return -1;

        int level = ConfigManager.instance.getAxeLevel(heldItem);
        if (level >= 0) return level;

        return heldItem.getItem().getHarvestLevel(heldItem, "axe", playerIn, null);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileChopping)
        {
            dropInventoryItems(worldIn, pos, ((TileChopping) tileentity).getSlotInventory());
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

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

    @Override
    public ItemBlock createItemBlock()
    {
        return (ItemBlock) new AsItem(this).setRegistryName(getRegistryName());
    }

    public static class AsItem extends ItemBlock
    {
        public AsItem(Block block)
        {
            super(block);
            setHasSubtypes(true);
        }

        @Override
        public int getMetadata(int damage)
        {
            return damage;
        }

        @Override
        public String getUnlocalizedName(ItemStack stack)
        {
            int meta = stack.getMetadata();

            if (meta > subNames.length)
                return getUnlocalizedName();

            return "tile." + Survivalist.MODID + subNames[meta];
        }
    }
}
