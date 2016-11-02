package gigaherz.survivalist.chaining;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerComboTracker
{
    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new PlayerComboTracker());
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent ev)
    {
    }
}
