package gigaherz.survivalist.client;

import com.google.common.collect.Maps;
import gigaherz.common.state.client.ItemStateMapper;
import gigaherz.survivalist.IModProxy;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.chopblock.BlockChopping;
import gigaherz.survivalist.chopblock.RenderChoppingBlock;
import gigaherz.survivalist.chopblock.TileChopping;
import gigaherz.survivalist.network.UpdateFields;
import gigaherz.survivalist.rack.RenderRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.rocks.EntityRock;
import gigaherz.survivalist.sawmill.gui.ContainerSawmill;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

import static gigaherz.common.client.ModelHelpers.registerBlockModelAsItem;
import static gigaherz.common.client.ModelHelpers.registerItemModel;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IModProxy
{
    public void preInit()
    {
    }

    // ----------------------------------------------------------- Item/Block Models
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityRock.class,
                manager -> new RenderSnowball<>(manager, Survivalist.rock, Minecraft.getMinecraft().getRenderItem()));

        ClientRegistry.bindTileEntitySpecialRenderer(TileRack.class, new RenderRack());
        ClientRegistry.bindTileEntitySpecialRenderer(TileChopping.class, new RenderChoppingBlock());

        OBJLoader.INSTANCE.addDomain(Survivalist.MODID);

        new ItemStateMapper(Survivalist.nugget).registerAllModelsExplicitly();
        new ItemStateMapper(Survivalist.rock_ore).registerAllModelsExplicitly();
        new ItemStateMapper(Survivalist.rock).registerAllModelsExplicitly();

        registerItemModel(Survivalist.chainmail);
        registerItemModel(Survivalist.tanned_leather);
        registerItemModel(Survivalist.jerky);

        registerItemModel(Survivalist.tanned_helmet, 0, Survivalist.location("tanned_armor"), "part=helmet");
        registerItemModel(Survivalist.tanned_chestplate, 0, Survivalist.location("tanned_armor"), "part=chestplate");
        registerItemModel(Survivalist.tanned_leggings, 0, Survivalist.location("tanned_armor"), "part=leggings");
        registerItemModel(Survivalist.tanned_boots, 0, Survivalist.location("tanned_armor"), "part=boots");

        registerBlockModelAsItem(Survivalist.rack);

        registerItemModel(Survivalist.dough);

        registerItemModel(Survivalist.round_bread);

        registerItemModel(Survivalist.hatchet);
        registerItemModel(Survivalist.pick);
        registerItemModel(Survivalist.spade);

        NonNullList<ItemStack> stacks1 = NonNullList.create();
        Survivalist.chopping_block.getSubBlocks(CreativeTabs.SEARCH, stacks1);
        for (ItemStack stack : stacks1)
        {
            IBlockState st = Survivalist.chopping_block.getStateFromMeta(stack.getMetadata());
            registerItemModel(stack.getItem(), stack.getMetadata(), String.format("damage=%s,variant=%s", st.getValue(BlockChopping.DAMAGE), st.getValue(BlockChopping.OldLog.VARIANT)));
        }

        NonNullList<ItemStack> stacks2 = NonNullList.create();
        Survivalist.chopping_block2.getSubBlocks(CreativeTabs.SEARCH, stacks2);
        for (ItemStack stack : stacks2)
        {
            IBlockState st = Survivalist.chopping_block2.getStateFromMeta(stack.getMetadata());
            registerItemModel(stack.getItem(), stack.getMetadata(), Survivalist.chopping_block.getRegistryName(), String.format("damage=%s,variant=%s", st.getValue(BlockChopping.DAMAGE), st.getValue(BlockChopping.NewLog.VARIANT)));
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

        registerItemModel(Survivalist.plant_fibres);

        registerBlockModelAsItem(Survivalist.sawmill);
    }

    @Override
    public void handleUpdateField(final UpdateFields message)
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            Minecraft gameController = Minecraft.getMinecraft();

            EntityPlayer entityplayer = gameController.player;

            if (entityplayer.openContainer != null && entityplayer.openContainer.windowId == message.windowId)
            {
                ((ContainerSawmill) entityplayer.openContainer).updateFields(message.fields);
            }
        });
    }

    /*
    private final Field f_curBlockDamageMP = ReflectionHelper.findField(PlayerControllerMP.class, "field_78770_f","curBlockDamageMP");
    private final Field f_leftClickCounter = ReflectionHelper.findField(Minecraft.class, "field_71429_W","leftClickCounter");

    @Override
    public float getCurrentBlockDamageMP()
    {
        try
        {
            return (float)f_curBlockDamageMP.get(Minecraft.getMinecraft().playerController);
        }
        catch (IllegalAccessException e)
        {
            return 0;
        }
    }

    @Override
    public void resetBlockRemoving()
    {
        Minecraft mc = Minecraft.getMinecraft();
        try
        {
            f_leftClickCounter.set(mc, 0);
        }
        catch (IllegalAccessException e)
        {
            // Do Nothing
        }
        mc.playerController.resetBlockRemoving();
    }*/
}
