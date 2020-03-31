package gigaherz.survivalist.client;

import gigaherz.survivalist.SurvivalistMod;
import gigaherz.survivalist.chopblock.ChoppingBlockRenderer;
import gigaherz.survivalist.chopblock.ChoppingBlockTileEntity;
import gigaherz.survivalist.rack.DryingRackRenderer;
import gigaherz.survivalist.rack.DryingRackTileEntity;
import gigaherz.survivalist.rocks.RockEntity;
import gigaherz.survivalist.scraping.ScrapingMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SurvivalistMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents
{
    // ----------------------------------------------------------- Item/Block Models
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(RockEntity.TYPE.get(),
                manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));

        ClientRegistry.bindTileEntityRenderer(DryingRackTileEntity.TYPE.get(), DryingRackRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ChoppingBlockTileEntity.TYPE.get(), ChoppingBlockRenderer::new);
    }

    /*@SubscribeEvent
    public static void lateInit(FMLLoadCompleteEvent event)
    {
        Minecraft.getInstance().getRenderManager().register(EntityRock.class,
                new SpriteRenderer<>(Minecraft.getInstance().getRenderManager(), Minecraft.getInstance().getItemRenderer()));
    }*/

    public static void handleScrapingMessage(ScrapingMessage message)
    {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().player.sendMessage(
                    new TranslationTextComponent("text." + SurvivalistMod.MODID + ".scraping.message1",
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
