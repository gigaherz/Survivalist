package gigaherz.common.state.client;

import com.google.common.collect.Maps;
import gigaherz.common.state.IItemState;
import gigaherz.common.state.IItemStateManager;
import gigaherz.common.state.ItemStateful;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Map;

public class ItemStateMapper implements ItemMeshDefinition
{
    public final Map<ItemStateful, ItemStateMapper> STATE_MAPPERS = Maps.newHashMap();

    final ItemStateful item;

    public ItemStateMapper(ItemStateful item)
    {
        this.item = item;
    }

    public void registerAllModelsExplicitly()
    {
        IItemStateManager manager = item.getStateManager();
        for (IItemState state : manager.getStateTable())
        {
            ModelLoader.setCustomModelResourceLocation(item, state.getMetadata(), getModelLocation(state));
        }
    }

    private ModelResourceLocation getModelLocation(IItemState state)
    {
        return new ModelResourceLocation(item.getRegistryName(), state.toString());
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        if (stack.getItem() != item)
            throw new IllegalArgumentException("The stack's item is not the expected item!");

        IItemState state = IItemStateManager.lookup(stack);
        if (state == null)
            state = item.getDefaultState();

        return getModelLocation(state);
    }
}
