package gigaherz.survivalist;

import gigaherz.survivalist.network.UpdateFields;

public interface IModProxy
{
    void preInit();

    void handleUpdateField(UpdateFields message);
}
