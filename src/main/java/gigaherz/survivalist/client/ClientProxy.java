package gigaherz.survivalist.client;

import gigaherz.survivalist.ISidedProxy;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.api.state.ItemStateMapper;
import gigaherz.survivalist.chopblock.RenderChoppingBlock;
import gigaherz.survivalist.chopblock.TileChopping;
import gigaherz.survivalist.rack.RenderRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.rocks.EntityRock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy implements ISidedProxy
{
    public void preInit()
    {
        OBJLoader.INSTANCE.addDomain(Survivalist.MODID);
        registerModels();

        RenderingRegistry.registerEntityRenderingHandler(EntityRock.class, manager -> {
            return new RenderSnowball<>(manager, Survivalist.rock, Minecraft.getMinecraft().getRenderItem());
        });

        ClientRegistry.bindTileEntitySpecialRenderer(TileRack.class, new RenderRack());
        ClientRegistry.bindTileEntitySpecialRenderer(TileChopping.class, new RenderChoppingBlock());
    }

    // ----------------------------------------------------------- Item/Block Models
    public void registerModels()
    {
        new ItemStateMapper(Survivalist.nugget).registerAllModelsExplicitly();
        new ItemStateMapper(Survivalist.rock_ore).registerAllModelsExplicitly();
        new ItemStateMapper(Survivalist.rock).registerAllModelsExplicitly();

        if (Survivalist.chainmail != null)
            registerItemModel(Survivalist.chainmail);
        if (Survivalist.tanned_leather != null)
            registerItemModel(Survivalist.tanned_leather);
        if (Survivalist.jerky != null)
            registerItemModel(Survivalist.jerky);

        if (Survivalist.tanned_helmet != null)
            registerItemModel(Survivalist.tanned_helmet, 0, Survivalist.location("tanned_armor"), "part=helmet");
        if (Survivalist.tanned_chestplate != null)
            registerItemModel(Survivalist.tanned_chestplate, 0, Survivalist.location("tanned_armor"), "part=chestplate");
        if (Survivalist.tanned_leggings != null)
            registerItemModel(Survivalist.tanned_leggings, 0, Survivalist.location("tanned_armor"), "part=leggings");
        if (Survivalist.tanned_boots != null)
            registerItemModel(Survivalist.tanned_boots, 0, Survivalist.location("tanned_armor"), "part=boots");

        if (Survivalist.rack != null)
            registerBlockModelAsItem(Survivalist.rack);

        if (Survivalist.dough != null)
            registerItemModel(Survivalist.dough);

        if (Survivalist.round_bread != null)
            registerItemModel(Survivalist.round_bread);

        if (Survivalist.hatchet != null)
            registerItemModel(Survivalist.hatchet);

        if (Survivalist.chopping_block != null)
        {
            registerBlockModelAsItem(Survivalist.chopping_block, 0, "damage=0");
            registerBlockModelAsItem(Survivalist.chopping_block, 1, "damage=1");
            registerBlockModelAsItem(Survivalist.chopping_block, 2, "damage=2");
        }
    }

    public void registerBlockModelAsItem(final Block block)
    {
        registerBlockModelAsItem(block, 0);
    }

    public void registerBlockModelAsItem(final Block block, int meta)
    {
        registerBlockModelAsItem(block, meta, "inventory");
    }

    public void registerBlockModelAsItem(final Block block, int meta, final String variantName)
    {
        Item item = Item.getItemFromBlock(block);
        assert item != null;
        registerItemModel(item, meta, variantName);
    }

    public void registerItemModel(final Item item)
    {
        registerItemModel(item, 0, "inventory");
    }

    public void registerItemModel(final ItemStack stack)
    {
        registerItemModel(stack.getItem(), stack.getMetadata(), "inventory");
    }

    public void registerItemModel(final ItemStack stack, final String variantName)
    {
        registerItemModel(stack.getItem(), stack.getMetadata(), variantName);
    }

    public void registerItemModel(final Item item, int meta, String variantName)
    {
        registerItemModel(item, meta, item.getRegistryName(), variantName);
    }

    public void registerItemModel(final Item item, int meta, ResourceLocation blockstatesLocation, String variantName)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(blockstatesLocation, variantName));
    }
}
