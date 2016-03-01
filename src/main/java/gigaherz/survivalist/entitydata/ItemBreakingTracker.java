package gigaherz.survivalist.entitydata;

import com.google.common.collect.Lists;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ItemBreakingTracker implements IExtendedEntityProperties
{
    public static final String PROP_NAME = Survivalist.MODID + "_ItemBreakingTracker";

    EntityPlayer player;
    World world;

    ItemStack[] equipmentSlots;

    public static ItemBreakingTracker get(EntityPlayer p)
    {
        return (ItemBreakingTracker) p.getExtendedProperties(PROP_NAME);
    }

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new Handler());
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
    }

    @Override
    public void init(Entity entity, World world)
    {
        this.player = (EntityPlayer) entity;
        this.world = world;
    }

    public void before()
    {
        ItemStack[] equipment = player.getInventory();
        equipmentSlots = new ItemStack[equipment.length];
        for(int i=0;i<equipment.length;i++)
        {
            ItemStack stack = equipment[i];
            equipmentSlots[i] = stack != null ? stack.copy() : null;
        }
    }

    public Collection<ItemStack> after()
    {
        List<ItemStack> changes = Lists.newArrayList();
        ItemStack[] equipment = player.getInventory();
        for(int i=0;i<equipment.length;i++)
        {
            ItemStack stack2 = equipmentSlots[i];
            if(stack2 != null)
            {
                ItemStack stack = equipment[i];
                if (stack == null)
                {
                    changes.add(stack2);
                }
            }
        }
        return changes;
    }

    public static class Handler
    {
        final Random rnd = new Random();

        public static Handler instance;

        List<Triple<ItemStack, ItemStack, ItemStack>> scrapingRegistry = Lists.newArrayList();

        public Handler()
        {
            instance = this;

            registerScrapoingConversions();
        }

        void registerScrapoingConversions()
        {
            if(ConfigManager.instance.enableToolScraping)
            {
                scrapingRegistry.add(Triple.of(new ItemStack(Items.wooden_shovel), new ItemStack(Blocks.planks), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.wooden_hoe), new ItemStack(Blocks.planks), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.wooden_axe), new ItemStack(Blocks.planks), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.wooden_pickaxe), new ItemStack(Blocks.planks), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.wooden_sword), new ItemStack(Blocks.planks), new ItemStack(Items.stick)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.stone_shovel), new ItemStack(Blocks.cobblestone), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.stone_hoe), new ItemStack(Blocks.cobblestone), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.stone_axe), new ItemStack(Blocks.cobblestone), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.stone_pickaxe), new ItemStack(Blocks.cobblestone), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.stone_sword), new ItemStack(Blocks.cobblestone), new ItemStack(Items.stick)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.iron_shovel), new ItemStack(Items.iron_ingot), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.iron_hoe), new ItemStack(Items.iron_ingot), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.iron_axe), new ItemStack(Items.iron_ingot), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.iron_pickaxe), new ItemStack(Items.iron_ingot), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.iron_sword), new ItemStack(Items.iron_ingot), new ItemStack(Items.stick)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.golden_shovel), new ItemStack(Items.gold_ingot), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.golden_hoe), new ItemStack(Items.gold_ingot), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.golden_axe), new ItemStack(Items.gold_ingot), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.golden_pickaxe), new ItemStack(Items.gold_ingot), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.golden_sword), new ItemStack(Items.gold_ingot), new ItemStack(Items.stick)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.diamond_shovel), new ItemStack(Items.diamond), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.diamond_hoe), new ItemStack(Items.diamond), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.diamond_axe), new ItemStack(Items.diamond), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.diamond_pickaxe), new ItemStack(Items.diamond), new ItemStack(Items.stick)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.diamond_sword), new ItemStack(Items.diamond), new ItemStack(Items.stick)));
            }

            if(ConfigManager.instance.enableArmorScraping)
            {
                scrapingRegistry.add(Triple.of(new ItemStack(Items.leather_boots), new ItemStack(Items.leather, 2), new ItemStack(Items.leather)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.leather_helmet), new ItemStack(Items.leather, 2), new ItemStack(Items.leather)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.leather_chestplate), new ItemStack(Items.leather, 2), new ItemStack(Items.leather)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.leather_leggings), new ItemStack(Items.leather, 2), new ItemStack(Items.leather)));

                scrapingRegistry.add(Triple.of(new ItemStack(Survivalist.tanned_boots), new ItemStack(Survivalist.tanned_leather, 2), new ItemStack(Survivalist.tanned_leather)));
                scrapingRegistry.add(Triple.of(new ItemStack(Survivalist.tanned_helmet), new ItemStack(Survivalist.tanned_leather, 2), new ItemStack(Survivalist.tanned_leather)));
                scrapingRegistry.add(Triple.of(new ItemStack(Survivalist.tanned_chestplate), new ItemStack(Survivalist.tanned_leather, 2), new ItemStack(Survivalist.tanned_leather)));
                scrapingRegistry.add(Triple.of(new ItemStack(Survivalist.tanned_leggings), new ItemStack(Survivalist.tanned_leather, 2), new ItemStack(Survivalist.tanned_leather)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.chainmail_boots), new ItemStack(Survivalist.chainmail, 2), new ItemStack(Survivalist.chainmail)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.chainmail_helmet), new ItemStack(Survivalist.chainmail, 2), new ItemStack(Survivalist.chainmail)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.chainmail_chestplate), new ItemStack(Survivalist.chainmail, 2), new ItemStack(Survivalist.chainmail)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.chainmail_leggings), new ItemStack(Survivalist.chainmail, 2), new ItemStack(Survivalist.chainmail)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.iron_boots), new ItemStack(Items.iron_ingot, 2), new ItemStack(Items.iron_ingot)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.iron_helmet), new ItemStack(Items.iron_ingot, 2), new ItemStack(Items.iron_ingot)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.iron_chestplate), new ItemStack(Items.iron_ingot, 2), new ItemStack(Items.iron_ingot)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.iron_leggings), new ItemStack(Items.iron_ingot, 2), new ItemStack(Items.iron_ingot)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.golden_boots), new ItemStack(Items.gold_ingot, 2), new ItemStack(Items.gold_ingot)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.golden_helmet), new ItemStack(Items.gold_ingot, 2), new ItemStack(Items.gold_ingot)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.golden_chestplate), new ItemStack(Items.gold_ingot, 2), new ItemStack(Items.gold_ingot)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.golden_leggings), new ItemStack(Items.gold_ingot, 2), new ItemStack(Items.gold_ingot)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.diamond_boots), new ItemStack(Items.diamond, 2), new ItemStack(Items.diamond)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.diamond_helmet), new ItemStack(Items.diamond, 2), new ItemStack(Items.diamond)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.diamond_chestplate), new ItemStack(Items.diamond, 2), new ItemStack(Items.diamond)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.diamond_leggings), new ItemStack(Items.diamond, 2), new ItemStack(Items.diamond)));
            }
        }

        private void onItemBroken(EntityPlayer player, ItemStack stack)
        {
            int survivalism = EnchantmentHelper.getEnchantmentLevel(Survivalist.scraping.effectId, stack);
            boolean fortune = rnd.nextDouble() > 0.9/(1+survivalism);

            ItemStack ret = null;

            for(Triple<ItemStack, ItemStack, ItemStack> scraping : scrapingRegistry)
            {
                ItemStack source = scraping.getLeft();

                if(!ItemStack.areItemsEqual(source, stack))
                    continue;

                ItemStack good = scraping.getMiddle();
                ItemStack bad = scraping.getRight();

                ret = fortune ? good.copy() : bad.copy();

                break;
            }

            if(ret != null)
            {
                Survivalist.logger.warn("Item broke (" + stack + ") and the player got " + ret + " in return!");

                player.addChatMessage(new ChatComponentText("Item broke (" + stack + ") and the player got " + ret + " in return!"));

                EntityItem entityitem = new EntityItem(player.worldObj, player.posX, player.posY + 0.5, player.posZ, ret);
                entityitem.motionX = 0;
                entityitem.motionZ = 0;

                player.worldObj.spawnEntityInWorld(entityitem);
            }
        }

        @SubscribeEvent
        public void onPlayerDestroyItem(PlayerDestroyItemEvent ev)
        {
            if(ev.entityPlayer.worldObj.isRemote)
                return;

            ItemStack stack = ev.original;

            Item item = stack.getItem();
            if(!(item instanceof ItemTool))
                return;

            onItemBroken(ev.entityPlayer, stack);
        }

        @SubscribeEvent
        public void onLivingHurt(LivingHurtEvent ev)
        {
            if(ev.entity.worldObj.isRemote)
                return;

            if(ev.entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer)ev.entityLiving;

                ItemBreakingTracker.get(player).before();
            }
        }

        @SubscribeEvent
        public void entityJoinWorld(EntityJoinWorldEvent ev)
        {
            if(ev.entity.worldObj.isRemote)
                return;

            if(ev.entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer)ev.entity;

                CombatTrackerIntercept interceptTracker = new CombatTrackerIntercept(player);
                ReflectionHelper.setPrivateValue(EntityLivingBase.class, player, interceptTracker,
                        "field_94063_bt", "_combatTracker");
            }
        }

        public void onTrackDamage(EntityPlayer player)
        {
            Collection<ItemStack> missing = ItemBreakingTracker.get(player).after();
            for(ItemStack broken : missing)
            {
                onItemBroken(player, broken);
            }
        }

        @SubscribeEvent
        public void entityConstruct(EntityEvent.EntityConstructing e)
        {
            if(e.entity.worldObj.isRemote)
                return;

            if (e.entity instanceof EntityPlayer)
            {
                if (e.entity.getExtendedProperties(PROP_NAME) == null)
                    e.entity.registerExtendedProperties(PROP_NAME, new ItemBreakingTracker());
            }
        }
    }

    // Forwards all calls to the existing instance, so that if some other mod overrides this class, it will still work as expected
    public static class CombatTrackerIntercept extends CombatTracker
    {
        CombatTracker inner;
        EntityPlayer entity;

        public CombatTrackerIntercept(EntityPlayer fighterIn)
        {
            super(fighterIn);
            inner = fighterIn.getCombatTracker();
            entity = fighterIn;
        }

        @Override
        public void trackDamage(DamageSource damageSrc, float healthIn, float damageAmount)
        {
            Handler.instance.onTrackDamage(entity);

            inner.trackDamage(damageSrc, healthIn, damageAmount);
        }

        @Override
        public int func_180134_f()
        {
            return inner.func_180134_f();
        }

        @Override
        public void func_94545_a()
        {
            inner.func_94545_a();
        }

        @Override
        public EntityLivingBase func_94550_c()
        {
            return inner.func_94550_c();
        }

        @Override
        public IChatComponent getDeathMessage()
        {
            return inner.getDeathMessage();
        }

        @Override
        public EntityLivingBase getFighter()
        {
            return inner.getFighter();
        }

        @Override
        public void reset()
        {
            inner.reset();
        }
    }
}
