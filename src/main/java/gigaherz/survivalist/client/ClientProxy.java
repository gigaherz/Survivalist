package gigaherz.survivalist.client;

import gigaherz.survivalist.ISidedProxy;
import gigaherz.survivalist.Survivalist;
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

        RenderingRegistry.registerEntityRenderingHandler(EntityRock.class, new IRenderFactory<EntityRock>()
        {
            @Override
            public Render<? super EntityRock> createRenderFor(RenderManager manager)
            {
                return new RenderSnowball<EntityRock>(manager, Survivalist.rock, Minecraft.getMinecraft().getRenderItem());
            }
        });

        ClientRegistry.bindTileEntitySpecialRenderer(TileRack.class, new RenderRack());
    }

    // ----------------------------------------------------------- Item/Block Models
    public void registerModels()
    {
        if (Survivalist.iron_nugget != null)
            registerItemModel(Survivalist.iron_nugget, "iron_nugget");

        if (Survivalist.chainmail != null)
            registerItemModel(Survivalist.chainmail, "chainmail");
        if (Survivalist.tanned_leather != null)
            registerItemModel(Survivalist.tanned_leather, "tanned_leather");
        if (Survivalist.jerky != null)
            registerItemModel(Survivalist.jerky, "jerky");

        if (Survivalist.tanned_helmet != null)
            registerItemModel(Survivalist.tanned_helmet, 0, "tanned_armor", "part=helmet");
        if (Survivalist.tanned_chestplate != null)
            registerItemModel(Survivalist.tanned_chestplate, 0, "tanned_armor", "part=chestplate");
        if (Survivalist.tanned_leggings != null)
            registerItemModel(Survivalist.tanned_leggings, 0, "tanned_armor", "part=leggings");
        if (Survivalist.tanned_boots != null)
            registerItemModel(Survivalist.tanned_boots, 0, "tanned_armor", "part=boots");

        if (Survivalist.rock_normal != null)
            registerItemModel(Survivalist.rock_normal, "rock", "rock=normal");
        if (Survivalist.rock_andesite != null)
            registerItemModel(Survivalist.rock_andesite, "rock", "rock=andesite");
        if (Survivalist.rock_diorite != null)
            registerItemModel(Survivalist.rock_diorite, "rock", "rock=diorite");
        if (Survivalist.rock_granite != null)
            registerItemModel(Survivalist.rock_granite, "rock", "rock=granite");

        if (Survivalist.iron_ore_rock != null)
            registerItemModel(Survivalist.iron_ore_rock, "rock_ore", "ore=iron");
        if (Survivalist.gold_ore_rock != null)
            registerItemModel(Survivalist.gold_ore_rock, "rock_ore", "ore=gold");

        if (Survivalist.rack != null)
            registerBlockModelAsItem(Survivalist.rack, "rack");

        if (Survivalist.dough != null)
            registerItemModel(Survivalist.dough, "dough");

        if (Survivalist.round_bread != null)
            registerItemModel(Survivalist.round_bread, "round_bread");
    }

    public void registerBlockModelAsItem(final Block block, final String blockName)
    {
        registerBlockModelAsItem(block, 0, blockName);
    }

    public void registerBlockModelAsItem(final Block block, final String blockName, final String variantName)
    {
        registerBlockModelAsItem(block, 0, blockName, variantName);
    }

    public void registerBlockModelAsItem(final Block block, int meta, final String blockName)
    {
        registerBlockModelAsItem(block, meta, blockName, "inventory");
    }

    public void registerBlockModelAsItem(final Block block, int meta, final String blockName, final String variantName)
    {
        registerItemModel(Item.getItemFromBlock(block), meta, blockName, variantName);
    }

    public void registerItemModel(final ItemStack stack, final String itemName, final String variantName)
    {
        registerItemModel(stack.getItem(), stack.getMetadata(), itemName, variantName);
    }

    public void registerItemModel(final Item item, final String itemName)
    {
        registerItemModel(item, 0, itemName, "inventory");
    }

    public void registerItemModel(final Item item, int meta, final String itemName, final String variantName)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Survivalist.MODID + ":" + itemName, variantName));
    }
}
