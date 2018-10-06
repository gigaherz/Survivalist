package gigaherz.survivalist.state;

public interface StatefulItem
{
    IItemStateManager getStateManager();

    IItemState getDefaultState();
}
