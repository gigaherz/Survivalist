package gigaherz.survivalist.sawmill;

import gigaherz.survivalist.SurvivalistTileEntityTypes;
import gigaherz.survivalist.api.ChoppingContext;
import gigaherz.survivalist.api.ChoppingRecipe;
import gigaherz.survivalist.sawmill.gui.SawmillContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class SawmillTileEntity extends TileEntity implements ITickableTileEntity, IIntArray, INamedContainerProvider
{
    public static RegistryObject<TileEntityType<SawmillTileEntity>> TYPE = SurvivalistTileEntityTypes.SAWMILL_RACK_TILE_ENTITY_TYPE;

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEMS_CAP;

    public final ItemStackHandler inventory = new ItemStackHandler(3){
        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            markDirty();
            needRefreshRecipe = true;
        }
    };
    private final RangedWrapper top = new RangedWrapper(inventory, 0, 1);
    private final RangedWrapper sides = new RangedWrapper(inventory, 1, 2)
    {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (!AbstractFurnaceTileEntity.isFuel(stack))
                return stack;

            return super.insertItem(slot, stack, simulate);
        }
    };
    private final RangedWrapper bottom = new RangedWrapper(inventory, 2, 3)
    {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            return stack;
        }
    };

    private final LazyOptional<IItemHandler> combined_provider = LazyOptional.of(() -> inventory);
    private final LazyOptional<IItemHandler> top_provider = LazyOptional.of(() -> top);
    private final LazyOptional<IItemHandler> sides_provider = LazyOptional.of(() -> sides);
    private final LazyOptional<IItemHandler> bottom_provider = LazyOptional.of(() -> bottom);

    private static final Random RANDOM = new Random();

    private int remainingBurnTime;
    private int totalBurnTime;
    private int cookTime;
    private int totalCookTime;

    private boolean needRefreshRecipe = true;

    public SawmillTileEntity()
    {
        super(TYPE.get());
    }

    public boolean isBurning()
    {
        return remainingBurnTime > 0;
    }

    public ItemStackHandler getInventory()
    {
        return inventory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == ITEMS_CAP)
        {
            if (facing == Direction.UP) return top_provider.cast();
            if (facing == Direction.DOWN) return bottom_provider.cast();
            if (facing != null) return sides_provider.cast();
            return combined_provider.cast();
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);

        ITEMS_CAP.readNBT(inventory, null, compound.get("Items"));

        remainingBurnTime = compound.getInt("BurnTime");
        cookTime = compound.getInt("CookTime");
        needRefreshRecipe = true;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound = super.write(compound);

        compound.put("Items", ITEMS_CAP.writeNBT(inventory, null));

        compound.putInt("BurnTime", (short) this.remainingBurnTime);
        compound.putInt("CookTime", (short) this.cookTime);

        return compound;
    }

    public int[] getFields()
    {
        return new int[]{remainingBurnTime, totalBurnTime, cookTime, totalCookTime};
    }

    public void setFields(int[] values)
    {
        remainingBurnTime = values[0];
        totalBurnTime = values[1];
        cookTime = values[2];
        totalCookTime = values[3];
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag)
    {
        read(tag);
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

        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        //world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
    }

    public static int getSawmillTime(World world, ItemStack stack)
    {
        return ChoppingRecipe.getRecipe(world, stack).map(recipe -> recipe.getSawingTime()).orElse(0);
    }

    @Override
    public void tick()
    {
        boolean wasBurning = this.isBurning();
        boolean changes = false;

        if (needRefreshRecipe)
        {
            totalBurnTime = ForgeHooks.getBurnTime(inventory.getStackInSlot(1));
            totalCookTime = getSawmillTime(world, inventory.getStackInSlot(0));
            needRefreshRecipe = false;
        }

        if (this.isBurning())
        {
            --this.remainingBurnTime;
        }

        if (!this.world.isRemote)
        {
            ItemStack fuel = this.inventory.getStackInSlot(1);

            if (this.isBurning() || !fuel.isEmpty())
            {
                ChoppingContext ctx = new ChoppingContext(inventory, null, 0, 0, RANDOM);
                changes |= ChoppingRecipe.getRecipe(world, ctx).map(choppingRecipe -> {
                    boolean changes2 = false;
                    if (!this.isBurning() && this.canWork(ctx, choppingRecipe))
                    {
                        this.totalBurnTime = ForgeHooks.getBurnTime(fuel);
                        this.remainingBurnTime = this.totalBurnTime;

                        if (this.isBurning())
                        {
                            changes2 = true;

                            if (!fuel.isEmpty())
                            {
                                Item item = fuel.getItem();
                                fuel.shrink(1);

                                if (fuel.isEmpty())
                                {
                                    ItemStack containerItem = item.getContainerItem(fuel);
                                    this.inventory.setStackInSlot(1, containerItem);
                                }
                            }
                        }
                    }

                    if (this.isBurning() && this.canWork(ctx, choppingRecipe))
                    {
                        ++this.cookTime;

                        if (this.totalCookTime == 0)
                        {
                            this.totalCookTime = choppingRecipe.getSawingTime();
                        }

                        if (this.cookTime >= this.totalCookTime)
                        {
                            this.cookTime = 0;
                            this.totalCookTime = choppingRecipe.getSawingTime();
                            this.processItem(ctx, choppingRecipe);
                            changes2 = true;
                        }
                    }
                    else
                    {
                        this.cookTime = 0;
                    }

                    return changes2;
                }).orElse(false);
            }
            if (!this.isBurning() && this.cookTime > 0)
            {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
            }
        }

        if (wasBurning != this.isBurning())
        {
            changes = true;
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            //world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
        }

        if (changes)
        {
            this.markDirty();
        }
    }

    private boolean canWork(ChoppingContext ctx, ChoppingRecipe choppingRecipe)
    {
        return getResult(ctx, choppingRecipe).getCount() > 0;
    }

    private void processItem(ChoppingContext ctx, ChoppingRecipe recipe)
    {
        ItemStack input = inventory.getStackInSlot(0);

        if (input.isEmpty())
            return;

        ItemStack result = getResult(ctx, recipe);
        if (result.getCount() <= 0)
            return;

        inventory.insertItem(2, result, false);

        input.shrink(1);
    }

    private ItemStack getResult(ChoppingContext ctx, @Nullable ChoppingRecipe choppingRecipe)
    {
        if (choppingRecipe == null)
            return ItemStack.EMPTY;

        ItemStack result = choppingRecipe.getCraftingResult(ctx);

        ItemStack output = inventory.getStackInSlot(2);

        int max = Math.min(inventory.getSlotLimit(2), output.getMaxStackSize());

        int space = max - output.getCount();

        if (space < result.getCount())
            return ItemStack.EMPTY;

        return result;
    }

    public int getCookTime()
    {
        return cookTime;
    }

    public int getTotalCookTime()
    {
        return totalCookTime;
    }

    public int getRemainingBurnTime()
    {
        return remainingBurnTime;
    }

    public int getTotalBurnTime()
    {
        return totalBurnTime;
    }

    @Override
    public int get(int index)
    {
        switch(index)
        {
            case 0: return remainingBurnTime;
            case 1: return totalBurnTime;
            case 2: return cookTime;
            case 3: return totalCookTime;
        }
        return 0;
    }

    @Override
    public void set(int index, int value)
    {
        switch(index)
        {
            case 0: remainingBurnTime=value;
            case 1: totalBurnTime=value;
            case 2: cookTime=value;
            case 3: totalCookTime=value;
        }
    }

    @Override
    public int size()
    {
        return 4;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("text.survivalist.sawmill");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new SawmillContainer(windowId, this, playerInventory);
    }
}
