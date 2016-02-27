package gigaherz.survivalist.client;

import gigaherz.survivalist.ISidedProxy;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;

public class ClientProxy implements ISidedProxy
{
    public void preInit()
    {
        OBJLoader.instance.addDomain(Survivalist.MODID);
        registerModels();
    }

    // ----------------------------------------------------------- Item/Block Models
    public void registerModels()
    {
        registerItemModel(Survivalist.iron_nugget, "iron_nugget");
        registerItemModel(Survivalist.chainmail, "chainmail");
        registerItemModel(Survivalist.tanned_leather, "tanned_leather");
        registerItemModel(Survivalist.jerky, "jerky");

        registerItemModel(Survivalist.tanned_helmet, 0, "tanned_armor", "part=helmet");
        registerItemModel(Survivalist.tanned_chestplate, 0, "tanned_armor", "part=chestplate");
        registerItemModel(Survivalist.tanned_leggings, 0, "tanned_armor", "part=leggings");
        registerItemModel(Survivalist.tanned_boots, 0, "tanned_armor", "part=boots");

        registerItemModel(Survivalist.rock_normal, "rock", "rock=normal");
        registerItemModel(Survivalist.rock_andesite, "rock", "rock=andesite");
        registerItemModel(Survivalist.rock_diorite, "rock", "rock=diorite");
        registerItemModel(Survivalist.rock_granite, "rock", "rock=granite");

        registerItemModel(Survivalist.iron_ore_rock, "rock_ore", "ore=iron");
        registerItemModel(Survivalist.gold_ore_rock, "rock_ore", "ore=gold");

        registerBlockModelAsItem(Survivalist.rack, "rack");
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
