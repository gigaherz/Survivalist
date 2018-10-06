package gigaherz.survivalist.state;

import gigaherz.survivalist.state.implementation.ItemStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockStateful extends ItemBlock implements StatefulItem
{
    private IItemStateManager stateManager;

    public ItemBlockStateful(Block block)
    {
        super(block);
        setRegistryName(block.getRegistryName());
        stateManager = createStateManager();
    }

    @Override
    public IItemStateManager getStateManager()
    {
        return stateManager;
    }

    @Override
    public IItemState getDefaultState()
    {
        return stateManager.getDefaultState();
    }

    public void setDefaultState(IItemState defaultState)
    {
        stateManager.setDefaultState(defaultState);
    }

    public IItemStateManager createStateManager()
    {
        BlockStateContainer blockStateManager = block.getBlockState();

        return new ItemStateManager(this, blockStateManager.getProperties().stream().toArray(IProperty[]::new));
    }

    @SuppressWarnings("unchecked")
    public ItemStack getStack(IBlockState bState)
    {
        IItemState iState = getDefaultState();
        for (IProperty prop : getStateManager().getProperties())
        {
            iState = iState.withProperty(prop, bState.getValue(prop));
        }
        return iState.getStack();
    }

    @SuppressWarnings("unchecked")
    public IBlockState getBlockState(ItemStack stack)
    {
        IItemState iState = getStateManager().get(stack.getMetadata());
        IBlockState bState = block.getDefaultState();
        if (iState != null)
        {
            for (IProperty prop : getStateManager().getProperties())
            {
                bState = bState.withProperty(prop, iState.getValue(prop));
            }
        }
        return bState;
    }
}
