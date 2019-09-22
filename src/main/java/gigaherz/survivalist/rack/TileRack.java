package gigaherz.survivalist.rack;

import gigaherz.survivalist.api.DryingContext;
import gigaherz.survivalist.api.DryingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class TileRack extends TileEntity implements ITickableTileEntity, INamedContainerProvider
{
    @ObjectHolder("survivalist:rack")
    public static TileEntityType<TileRack> TYPE = null;

    public static final ModelProperty<RackItemsStateData> CONTAINED_ITEMS_DATA = new ModelProperty<>();

    private final ModelDataMap data = new ModelDataMap.Builder().withProperty(CONTAINED_ITEMS_DATA).build();

    public int[] dryTimeRemaining = new int[4];

    public final ItemStackHandler items = new ItemStackHandler(4)
    {
        @Override
        protected int getStackLimit(int slot, ItemStack stack)
        {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            TileRack.this.markDirty();

            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);

            requestModelDataUpdate();
        }
    };
    public final LazyOptional<IItemHandler> itemsProvider = LazyOptional.of(() -> items);

    private final NonNullList<ItemStack> oldItems = NonNullList.withSize(4, ItemStack.EMPTY);
    private final DryingContext[] dryingSlots = {
            new DryingContext(new RangedWrapper(items, 0, 1)),
            new DryingContext(new RangedWrapper(items, 1, 2)),
            new DryingContext(new RangedWrapper(items, 2, 3)),
            new DryingContext(new RangedWrapper(items, 3, 4)),
    };

    public TileRack()
    {
        super(TYPE);
    }

    @Nonnull
    @Override
    public IModelData getModelData()
    {
        data.setData(CONTAINED_ITEMS_DATA, new RackItemsStateData(getItems()));
        return data;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet)
    {
        read(packet.getNbtCompound());

        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public void tick()
    {
        if (world.isRemote)
            return;

        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = items.getStackInSlot(i);
            if (ItemStack.areItemStacksEqual(stack, oldItems.get(i)))
            {
                if (dryTimeRemaining[i] > 0)
                {
                    final int index = i;
                    Optional<DryingRecipe> recipe = DryingRecipe.getRecipe(world, dryingSlots[index]);
                    if (recipe.isPresent())
                    {
                        if (--dryTimeRemaining[index] <= 0) {
                            ItemStack result = recipe.get().getCraftingResult(dryingSlots[index]);
                            items.setStackInSlot(index, result);
                        }
                    }
                    else
                    {
                        dryTimeRemaining[index] = 0;
                    }
                }
            }
            else
            {
                oldItems.set(i, stack);
                dryTimeRemaining[i] = DryingRecipe.getDryingTime(world, dryingSlots[i]);
            }
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return itemsProvider.cast();
        return super.getCapability(capability, facing);
    }

    public boolean isUseableByPlayer(PlayerEntity player)
    {
        return world.getTileEntity(pos) == this
                && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound = super.write(compound);
        compound.put("Items", items.serializeNBT());
        compound.putIntArray("RemainingTime", dryTimeRemaining);
        return compound;
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        items.deserializeNBT(compound.getCompound("Items"));
        int[] remaining = compound.getIntArray("RemainingTime");

        dryTimeRemaining = Arrays.copyOf(remaining, 4);
    }

    public IItemHandler inventory()
    {
        return items;
    }

    public ItemStack[] getItems()
    {
        return new ItemStack[] {
                items.getStackInSlot(0),
                items.getStackInSlot(1),
                items.getStackInSlot(2),
                items.getStackInSlot(3)
        };
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("text.survivalist.rack.inventory");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new ContainerRack(windowId, this, playerInventory);
    }
}
