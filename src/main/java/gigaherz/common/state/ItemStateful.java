package gigaherz.common.state;

import gigaherz.common.ItemRegistered;

public abstract class ItemStateful extends ItemRegistered
{
    private IItemStateManager stateManager;

    public ItemStateful(String name)
    {
        super(name);
        stateManager = createStateManager();
    }

    public IItemStateManager getStateManager()
    {
        return stateManager;
    }

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
