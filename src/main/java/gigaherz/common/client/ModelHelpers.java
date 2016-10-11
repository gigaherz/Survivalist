package gigaherz.common.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ModelHelpers
{
    public static void registerBlockModelAsItem(final Block block)
    {
        registerBlockModelAsItem(block, 0);
    }

    public static void registerBlockModelAsItem(final Block block, int meta)
    {
        registerBlockModelAsItem(block, meta, "inventory");
    }

    public static void registerBlockModelAsItem(final Block block, int meta, final String variantName)
    {
        Item item = Item.getItemFromBlock(block);
        assert item != null;
        registerItemModel(item, meta, variantName);
    }

    public static void registerItemModel(final Item item)
    {
        registerItemModel(item, 0, "inventory");
    }

    public static void registerItemModel(final ItemStack stack)
    {
        registerItemModel(stack.getItem(), stack.getMetadata(), "inventory");
    }

    public static void registerItemModel(final ItemStack stack, final String variantName)
    {
        registerItemModel(stack.getItem(), stack.getMetadata(), variantName);
    }

    public static void registerItemModel(final Item item, int meta, String variantName)
    {
        registerItemModel(item, meta, item.getRegistryName(), variantName);
    }

    public static void registerItemModel(final Item item, int meta, ResourceLocation blockstatesLocation, String variantName)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(blockstatesLocation, variantName));
    }
}
