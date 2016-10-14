package gigaherz.survivalist.client;

import gigaherz.common.ISidedProxy;
import gigaherz.common.state.client.ItemStateMapper;
import gigaherz.survivalist.IModProxy;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.chopblock.RenderChoppingBlock;
import gigaherz.survivalist.chopblock.TileChopping;
import gigaherz.survivalist.rack.RenderRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.rocks.EntityRock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static gigaherz.common.client.ModelHelpers.registerBlockModelAsItem;
import static gigaherz.common.client.ModelHelpers.registerItemModel;
import static gigaherz.survivalist.Survivalist.proxy;

@Mod.EventBusSubscriber
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

        registerBlockModelAsItem(Survivalist.chopping_block, 0, "damage=0");
        registerBlockModelAsItem(Survivalist.chopping_block, 1, "damage=1");
        registerBlockModelAsItem(Survivalist.chopping_block, 2, "damage=2");
    }

}
