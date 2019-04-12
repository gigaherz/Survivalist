package gigaherz.survivalist.client;

import com.google.common.collect.Maps;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.chopblock.BlockChopping;
import gigaherz.survivalist.chopblock.RenderChoppingBlock;
import gigaherz.survivalist.chopblock.TileChopping;
import gigaherz.survivalist.rack.RackBakedModel;
import gigaherz.survivalist.rack.RenderRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.rocks.EntityRock;
import gigaherz.survivalist.state.client.ItemStateMapper;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Survivalist.MODID)
public class ClientEvents
{
    // ----------------------------------------------------------- Item/Block Models
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityRock.class,
                manager -> new RenderSnowball<>(manager, Survivalist.rock, Minecraft.getMinecraft().getRenderItem()));

        ClientRegistry.bindTileEntitySpecialRenderer(TileRack.class, new RenderRack());
        ClientRegistry.bindTileEntitySpecialRenderer(TileChopping.class, new RenderChoppingBlock());

        OBJLoader.INSTANCE.addDomain(Survivalist.MODID);

        ModelLoaderRegistry.registerLoader(new RackBakedModel.ModelLoader());

        new ItemStateMapper(Survivalist.nugget).registerAllModelsExplicitly();
        new ItemStateMapper(Survivalist.rock_ore).registerAllModelsExplicitly();
        new ItemStateMapper(Survivalist.rock).registerAllModelsExplicitly();

        ModelLoader.setCustomModelResourceLocation(Survivalist.chainmail, 0,
                new ModelResourceLocation(Survivalist.chainmail.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Survivalist.tanned_leather, 0,
                new ModelResourceLocation(Survivalist.tanned_leather.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Survivalist.jerky, 0,
                new ModelResourceLocation(Survivalist.jerky.getRegistryName(), "inventory"));

        ModelLoader.setCustomModelResourceLocation(Survivalist.tanned_helmet, 0,
                new ModelResourceLocation(Survivalist.location("tanned_armor"), "part=helmet"));
        ModelLoader.setCustomModelResourceLocation(Survivalist.tanned_chestplate, 0,
                new ModelResourceLocation(Survivalist.location("tanned_armor"), "part=chestplate"));
        ModelLoader.setCustomModelResourceLocation(Survivalist.tanned_leggings, 0,
                new ModelResourceLocation(Survivalist.location("tanned_armor"), "part=leggings"));
        ModelLoader.setCustomModelResourceLocation(Survivalist.tanned_boots, 0,
                new ModelResourceLocation(Survivalist.location("tanned_armor"), "part=boots"));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Survivalist.rack), 0,
                new ModelResourceLocation(Survivalist.rack.getRegistryName(), "inventory"));

        ModelLoader.setCustomModelResourceLocation(Survivalist.dough, 0,
                new ModelResourceLocation(Survivalist.dough.getRegistryName(), "inventory"));

        ModelLoader.setCustomModelResourceLocation(Survivalist.round_bread, 0,
                new ModelResourceLocation(Survivalist.round_bread.getRegistryName(), "inventory"));

        ModelLoader.setCustomModelResourceLocation(Survivalist.hatchet, 0,
                new ModelResourceLocation(Survivalist.hatchet.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Survivalist.pick, 0,
                new ModelResourceLocation(Survivalist.pick.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Survivalist.spade, 0,
                new ModelResourceLocation(Survivalist.spade.getRegistryName(), "inventory"));

        NonNullList<ItemStack> stacks1 = NonNullList.create();
        Survivalist.chopping_block.getSubBlocks(CreativeTabs.SEARCH, stacks1);
        for (ItemStack stack : stacks1)
        {
            IBlockState st = Survivalist.chopping_block.getStateFromMeta(stack.getMetadata());
            final Item item = stack.getItem();
            ModelLoader.setCustomModelResourceLocation(item, stack.getMetadata(),
                    new ModelResourceLocation(Survivalist.chopping_block.getRegistryName(), String.format("damage=%s,variant=%s", st.getValue(BlockChopping.DAMAGE), st.getValue(BlockChopping.OldLog.VARIANT))));
        }

        NonNullList<ItemStack> stacks2 = NonNullList.create();
        Survivalist.chopping_block2.getSubBlocks(CreativeTabs.SEARCH, stacks2);
        for (ItemStack stack : stacks2)
        {
            IBlockState st = Survivalist.chopping_block2.getStateFromMeta(stack.getMetadata());
            ModelLoader.setCustomModelResourceLocation(stack.getItem(), stack.getMetadata(),
                    new ModelResourceLocation(Survivalist.chopping_block.getRegistryName(), String.format("damage=%s,variant=%s", st.getValue(BlockChopping.DAMAGE), st.getValue(BlockChopping.NewLog.VARIANT))));
        }

        ModelLoader.setCustomStateMapper(Survivalist.chopping_block2, new StateMapperBase()
        {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                Map<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());

                String s = Survivalist.chopping_block.getRegistryName().toString();

                return new ModelResourceLocation(s, this.getPropertyString(map));
            }
        });

        ModelLoader.setCustomModelResourceLocation(Survivalist.plant_fibres, 0, new ModelResourceLocation(Survivalist.plant_fibres.getRegistryName(), "inventory"));

        Item item = Item.getItemFromBlock(Survivalist.sawmill);
        assert item != null;
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

}
