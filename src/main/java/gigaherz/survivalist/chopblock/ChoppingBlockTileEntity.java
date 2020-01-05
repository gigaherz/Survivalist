package gigaherz.survivalist.chopblock;

import gigaherz.survivalist.SurvivalistTileEntityTypes;
import gigaherz.survivalist.api.ChoppingContext;
import gigaherz.survivalist.api.ChoppingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class ChoppingBlockTileEntity extends TileEntity
{
    public static final RegistryObject<TileEntityType<ChoppingBlockTileEntity>> TYPE = SurvivalistTileEntityTypes.CHOPPING_BLOCK_TILE_ENTITY_TYPE;

    private static final Random RANDOM = new Random();

    private final ItemStackHandler slotInventory = new ItemStackHandler(1)
    {
        @Override
        protected int getStackLimit(int slot, ItemStack stack)
        {
            return 1;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            if (!ChoppingRecipe.getRecipe(world, stack)
                    .isPresent())
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            breakingProgress = 0;
            if (world != null)
            {
                BlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
            markDirty();
        }
    };
    private final LazyOptional<IItemHandler> slotInventoryGetter = LazyOptional.of(() -> slotInventory);

    // measured in the number of ticks it will take to return to 0
    private int breakingProgress = 0;

    public ChoppingBlockTileEntity()
    {
        super(TYPE.get());
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return slotInventoryGetter.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(slotInventory, null, compound.get("Inventory"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound = super.write(compound);
        compound.put("Inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(slotInventory, null));
        return compound;
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT compound = new CompoundNBT();
        compound.put("Item", slotInventory.getStackInSlot(0).serializeNBT());
        return compound;
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag)
    {
        slotInventory.setStackInSlot(0, ItemStack.read(tag.getCompound("Item")));
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        handleUpdateTag(pkt.getNbtCompound());
    }

    public boolean chop(PlayerEntity playerIn, int axeLevel, int fortune)
    {
        boolean completed = false;
        if (slotInventory.getStackInSlot(0).getCount() > 0)
        {
            ChoppingContext ctx = new ChoppingContext(slotInventory, playerIn, axeLevel, fortune, RANDOM);

            Optional<ChoppingRecipe> foundRecipe = ChoppingRecipe.getRecipe(world, ctx);

            completed = foundRecipe.map(recipe -> {

                boolean completed2 = false;

                breakingProgress += 25 + recipe.getHitCountMultiplier() * 25 * Math.max(0, axeLevel);
                if (breakingProgress >= 200)
                {
                    if (!world.isRemote)
                    {
                        ItemStack out = recipe.getCraftingResult(ctx);

                        if (out.getCount() > 0)
                        {
                            //ItemHandlerHelper.giveItemToPlayer(playerIn, out);
                            spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, out);
                        }

                        completed2 = true;
                    }
                    world.playSound(playerIn, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    slotInventory.setStackInSlot(0, ItemStack.EMPTY);
                    breakingProgress = 0;
                }

                BlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);

                return completed2;
            }).orElse(false);
        }
        return completed;
    }

    public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack)
    {
        while (stack.getCount() > 0)
        {
            int i = /*RANDOM.nextInt(3) +*/ 1;

            if (i > stack.getCount())
            {
                i = stack.getCount();
            }

            ItemStack copy = stack.copy();
            copy.setCount(i);
            stack.grow(-i);

            Block.spawnAsEntity(worldIn, new BlockPos(x,y,z), stack);
        }
    }

    public ItemStackHandler getSlotInventory()
    {
        return slotInventory;
    }

    public int getBreakStage()
    {
        if (breakingProgress <= 0)
            return -1;
        return breakingProgress * 10 / 200;
    }
}
