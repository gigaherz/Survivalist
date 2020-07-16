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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

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
    public void read(BlockState state, CompoundNBT compound)
    {
        super.read(state, compound);
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
    public void handleUpdateTag(BlockState state, CompoundNBT tag)
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
        handleUpdateTag(getBlockState(), pkt.getNbtCompound());
    }

    public ActionResult<ItemStack> chop(PlayerEntity player, int axeLevel, int fortune)
    {
        ActionResultType completed = ActionResultType.PASS;
        ItemStack containedItem = slotInventory.getStackInSlot(0).copy();
        if (containedItem.getCount() > 0)
        {
            ChoppingContext ctx = new ChoppingContext(slotInventory, player, axeLevel, fortune, RANDOM);

            Optional<ChoppingRecipe> foundRecipe = ChoppingRecipe.getRecipe(world, ctx);

            completed = foundRecipe.map(recipe -> {

                ActionResultType completed2 = ActionResultType.PASS;

                breakingProgress += recipe.getHitProgress(axeLevel);
                if (breakingProgress >= 200)
                {
                    if (!world.isRemote)
                    {
                        ItemStack out = recipe.getCraftingResult(ctx);

                        if (out.getCount() > 0)
                        {
                            //ItemHandlerHelper.giveItemToPlayer(player, out);
                            ItemHandlerHelper.giveItemToPlayer(player, out);

                            completed2 = ActionResultType.SUCCESS;
                        }
                        else
                        {
                            completed2 = ActionResultType.FAIL;
                        }
                    }
                    world.playSound(player, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    slotInventory.setStackInSlot(0, ItemStack.EMPTY);
                    breakingProgress = 0;
                }

                BlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);

                return completed2;
            }).orElse(ActionResultType.PASS);
        }
        return new ActionResult<>(completed, containedItem);
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

            Block.spawnAsEntity(worldIn, new BlockPos(x, y, z), stack);
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
