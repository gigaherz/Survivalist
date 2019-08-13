package gigaherz.survivalist.client;

import com.google.common.collect.Maps;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.chopblock.ChoppingBlock;
import gigaherz.survivalist.chopblock.RenderChoppingBlock;
import gigaherz.survivalist.chopblock.ChoppingBlockTileEntity;
import gigaherz.survivalist.rack.RackBakedModel;
import gigaherz.survivalist.rack.RenderRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.rocks.EntityRock;
import gigaherz.survivalist.sawmill.gui.ContainerSawmill;
import gigaherz.survivalist.scraping.MessageScraping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Survivalist.MODID)
public class ClientEvents
{
    // ----------------------------------------------------------- Item/Block Models
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityRock.class,
                manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));

        ClientRegistry.bindTileEntitySpecialRenderer(TileRack.class, new RenderRack());
        ClientRegistry.bindTileEntitySpecialRenderer(ChoppingBlockTileEntity.class, new RenderChoppingBlock());

        OBJLoader.INSTANCE.addDomain(Survivalist.MODID);

        ModelLoaderRegistry.registerLoader(new RackBakedModel.ModelLoader());
    }

    public static void handleScrapingMessage(MessageScraping message)
    {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().player.sendMessage(
                    new TranslationTextComponent("text." + Survivalist.MODID + ".scraping.message1",
                            makeClickable(message.stack.getTextComponent()),
                            new StringTextComponent("" + message.ret.getCount()),
                            makeClickable(message.ret.getTextComponent())));
        });
    }

    private static ITextComponent makeClickable(ITextComponent textComponent)
    {
        //textComponent.getStyle().setClickEvent()
        return textComponent;
    }
}
