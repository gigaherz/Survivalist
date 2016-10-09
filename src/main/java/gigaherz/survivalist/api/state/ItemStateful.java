package gigaherz.survivalist.api.state;

import gigaherz.survivalist.base.ItemRegistered;

public abstract class ItemStateful extends ItemRegistered
{
    private ItemStateManager stateManager;

    public ItemStateful(String name)
    {
        super(name);
    }

    public ItemStateManager getStateManager()
    {
        return stateManager;
    }

    public void setStateManager(ItemStateManager manager)
    {
        stateManager = manager;
    }

    public IItemState getDefaultState()
    {
        return stateManager.getDefaultState();
    }

    public void setDefaultState(IItemState defaultState)
    {
        stateManager.setDefaultState(defaultState);
    }
}
