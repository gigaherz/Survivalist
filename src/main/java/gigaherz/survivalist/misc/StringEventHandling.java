package gigaherz.survivalist.misc;

import com.google.common.collect.ImmutableList;
import gigaherz.survivalist.Survivalist;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Random;

public class StringEventHandling
{
    private final Random rnd = new Random();

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new StringEventHandling());
    }

    @SubscribeEvent
    public void entityDrops(LivingDropsEvent ev)
    {
        Entity entity = ev.getEntity();
        if (!(entity instanceof EntitySheep))
            return;

        List<EntityItem> drops = ev.getDrops();
        if (drops instanceof ImmutableList)
        {
            Survivalist.logger.warn("WARNING: Some mod is returning an ImmutableList, replacing drops will NOT be possible.");
            return;
        }

        if (rnd.nextFloat() < 0.25f)
            drops.add(new EntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, new ItemStack(Items.STRING)));
    }
}
