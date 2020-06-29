package gigaherz.survivalist.rack;

import gigaherz.survivalist.SurvivalistTileEntityTypes;
import gigaherz.survivalist.api.DryingRecipe;
import gigaherz.survivalist.api.ItemHandlerWrapper;
import gigaherz.survivalist.util.IntArrayWrapper;
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
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class DryingRackTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider
{
    public static final RegistryObject<TileEntityType<DryingRackTileEntity>> TYPE = SurvivalistTileEntityTypes.DRYING_RACK_TILE_ENTITY_TYPE;

    public static final ModelProperty<DryingRackItemsStateData> CONTAINED_ITEMS_DATA = new ModelProperty<>();

    private final ModelDataMap data = new ModelDataMap.Builder().withProperty(CONTAINED_ITEMS_DATA).build();

    private int[] dryTimeRemaining = new int[4];
    private final IIntArray dryTimeArray = new IntArrayWrapper(dryTimeRemaining);

    private final ItemStackHandler items = new ItemStackHandler(4)
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
            DryingRackTileEntity.this.markDirty();

            BlockState state = DryingRackTileEntity.this.getBlockState();
            world.notifyBlockUpdate(pos, state, state, 3);

            requestModelDataUpdate();
        }
    };
    private final LazyOptional<IItemHandler> itemsProvider = LazyOptional.of(() -> items);

    private final NonNullList<ItemStack> oldItems = NonNullList.withSize(4, ItemStack.EMPTY);
    private final ItemHandlerWrapper[] dryingSlots = {
            new ItemHandlerWrapper(new RangedWrapper(items, 0, 1), () -> new Vec3d(this.pos).add(0.5, 0.5, 0.5), 64),
            new ItemHandlerWrapper(new RangedWrapper(items, 1, 2), () -> new Vec3d(this.pos).add(0.5, 0.5, 0.5), 64),
            new ItemHandlerWrapper(new RangedWrapper(items, 2, 3), () -> new Vec3d(this.pos).add(0.5, 0.5, 0.5), 64),
            new ItemHandlerWrapper(new RangedWrapper(items, 3, 4), () -> new Vec3d(this.pos).add(0.5, 0.5, 0.5), 64),
    };

    public DryingRackTileEntity()
    {
        super(TYPE.get());
    }

    @Nonnull
    @Override
    public IModelData getModelData()
    {
        data.setData(CONTAINED_ITEMS_DATA, new DryingRackItemsStateData(getItems()));
        return data;
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT compound = super.getUpdateTag();
        compound.put("Items", items.serializeNBT());
        return compound;
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag)
    {
        items.deserializeNBT(tag.getCompound("Items"));
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet)
    {
        handleUpdateTag(packet.getNbtCompound());
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
                    Optional<DryingRecipe> recipe = DryingRecipe.getRecipe(world, dryingSlots[i]);
                    if (recipe.isPresent())
                    {
                        if (--dryTimeRemaining[i] <= 0)
                        {
                            ItemStack result = recipe.get().getCraftingResult(dryingSlots[i]);
                            items.setStackInSlot(i, result);
                        }
                    }
                    else
                    {
                        dryTimeRemaining[i] = 0;
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
        dryTimeRemaining = Arrays.copyOf(compound.getIntArray("RemainingTime"), 4);
    }

    public IItemHandler inventory()
    {
        return items;
    }

    public IIntArray progress()
    {
        return dryTimeArray;
    }

    public ItemStack[] getItems()
    {
        return new ItemStack[]{
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
        return new DryingRackContainer(windowId, this, playerInventory);
    }
}
