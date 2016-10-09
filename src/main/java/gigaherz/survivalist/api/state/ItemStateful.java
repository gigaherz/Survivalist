package gigaherz.survivalist.api.state;

import gigaherz.survivalist.base.ItemRegistered;

public abstract class ItemStateful extends ItemRegistered
{
    private ItemStateManager stateData;

    public ItemStateful(String name)
    {
        super(name);
        initializeItemState();
    }

    public ItemStateManager getStateData()
    {
        return stateData;
    }

    public IItemState getDefaultState()
    {
        return stateData.getDefaultState();
    }

    public void setDefaultState(IItemState defaultState)
    {
        stateData.setDefaultState(defaultState);
    }

    public void initializeItemState()
    {
        stateData = createItemState();
    }

    public abstract ItemStateManager createItemState();
}
