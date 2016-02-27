package gigaherz.survivalist.entitydata;

import com.google.common.collect.Lists;
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

        public Handler()
        {
            instance = this;
        }

        private void onItemBroken(EntityPlayer player, ItemStack stack)
        {
            Item item = stack.getItem();

            int survivalism = EnchantmentHelper.getEnchantmentLevel(Survivalist.scraping.effectId, stack);
            boolean fortune = rnd.nextDouble() > 0.9/(1+survivalism);

            ItemStack ret = null;

            if (item == Items.wooden_shovel ||
                    item == Items.wooden_hoe ||
                    item == Items.wooden_axe ||
                    item == Items.wooden_pickaxe ||
                    item == Items.wooden_sword)
            {
                ret = fortune ? new ItemStack(Blocks.planks) : new ItemStack(Items.stick);
            }
            else if (item == Items.stone_shovel ||
                    item == Items.stone_hoe ||
                    item == Items.stone_axe ||
                    item == Items.stone_pickaxe ||
                    item == Items.stone_sword)
            {
                ret = fortune ? new ItemStack(Blocks.cobblestone) : new ItemStack(Items.stick);
            }
            else if (item == Items.iron_shovel ||
                    item == Items.iron_hoe ||
                    item == Items.iron_axe ||
                    item == Items.iron_pickaxe ||
                    item == Items.iron_sword)
            {
                ret = fortune ? new ItemStack(Items.iron_ingot) : new ItemStack(Items.stick);
            }
            else if (item == Items.golden_shovel ||
                    item == Items.golden_hoe ||
                    item == Items.golden_axe ||
                    item == Items.golden_pickaxe ||
                    item == Items.golden_sword)
            {
                ret = fortune ? new ItemStack(Items.gold_ingot) : new ItemStack(Items.stick);
            }
            else if (item == Items.diamond_shovel ||
                    item == Items.diamond_hoe ||
                    item == Items.diamond_axe ||
                    item == Items.diamond_pickaxe ||
                    item == Items.diamond_sword)
            {
                ret = fortune ? new ItemStack(Items.diamond) : new ItemStack(Items.stick);
            }
            else if (item == Items.leather_boots ||
                    item == Items.leather_helmet ||
                    item == Items.leather_chestplate ||
                    item == Items.leather_leggings)
            {
                ret = new ItemStack(Items.leather, fortune ? 2 : 1);
            }
            else if (item == Survivalist.tanned_boots ||
                    item == Survivalist.tanned_helmet ||
                    item == Survivalist.tanned_chestplate ||
                    item == Survivalist.tanned_leggings)
            {
                ret = new ItemStack(Survivalist.tanned_leather, fortune ? 2 : 1);
            }
            else if (item == Items.chainmail_boots ||
                    item == Items.chainmail_helmet ||
                    item == Items.chainmail_chestplate ||
                    item == Items.chainmail_leggings)
            {
                ret = new ItemStack(Survivalist.chainmail, fortune ? 2 : 1);
            }
            else if (item == Items.iron_boots ||
                    item == Items.iron_helmet ||
                    item == Items.iron_chestplate ||
                    item == Items.iron_leggings)
            {
                ret = new ItemStack(Items.iron_ingot, fortune ? 2 : 1);
            }
            else if(item == Items.golden_boots ||
                    item == Items.golden_helmet ||
                    item == Items.golden_chestplate ||
                    item == Items.golden_leggings)
            {
                ret = new ItemStack(Items.gold_ingot, fortune ? 2 : 1);
            }
            else if(item == Items.diamond_boots ||
                    item == Items.diamond_helmet ||
                    item == Items.diamond_chestplate ||
                    item == Items.diamond_leggings)
            {
                ret = new ItemStack(Items.diamond, fortune ? 2 : 1);
            }
            else
            {
                Survivalist.logger.warn("Unknown item broken! " + stack);
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
            EntityPlayer player = ev.entityPlayer;
            if(player.worldObj.isRemote)
                return;

            ItemStack stack = ev.original;

            Item item = stack.getItem();
            if(!(item instanceof ItemTool))
                return;

            onItemBroken(player, stack);
        }

        @SubscribeEvent
        public void onLivingHurt(LivingHurtEvent ev)
        {
            if(ev.entity instanceof EntityPlayer && !ev.entity.worldObj.isRemote)
            {
                EntityPlayer player = (EntityPlayer)ev.entityLiving;

                ItemBreakingTracker.get(player).before();
            }
        }

        @SubscribeEvent
        public void onEntityConstructing(EntityJoinWorldEvent ev)
        {
            if(ev.entity instanceof EntityPlayer && !ev.entity.worldObj.isRemote)
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
            if (e.entity instanceof EntityPlayer && !e.entity.worldObj.isRemote)
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
