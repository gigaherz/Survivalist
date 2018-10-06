package gigaherz.survivalist.state;

import net.minecraft.item.Item;

public abstract class ItemStateful extends Item implements StatefulItem
{
    private IItemStateManager stateManager;

    public ItemStateful()
    {
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

    public abstract IItemStateManager createStateManager();
}

