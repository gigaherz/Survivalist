package gigaherz.survivalist.mending;

import gigaherz.survivalist.Survivalist;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MendingTracker
{
    public static final MendingTracker instance = new MendingTracker();

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(instance);
    }

    @SubscribeEvent
    public void onPlayerExp(PlayerPickupXpEvent event)
    {
        EntityPlayer player = event.entityPlayer;

        ItemStack held = player.getHeldItem();

        if (mendItem(held, event))
            return;

        for (ItemStack stack : player.getInventory())
        {
            if (mendItem(stack, event))
                return;
        }
    }

    private boolean mendItem(ItemStack stack, PlayerPickupXpEvent event)
    {
        if(stack == null)
            return false;

        int level = EnchantmentHelper.getEnchantmentLevel(Survivalist.mending.effectId, stack);

        if(level <= 0)
            return false;

        if (!stack.isItemDamaged())
            return false;

        if (stack.isItemDamaged())
        {
            int i = Math.min(event.orb.xpValue * 2, stack.getItemDamage());
            event.orb.xpValue -= i / 2;
            stack.setItemDamage(stack.getItemDamage() - i);
        }

        return false;
    }
}
