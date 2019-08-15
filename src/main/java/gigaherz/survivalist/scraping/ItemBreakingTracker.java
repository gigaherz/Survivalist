package gigaherz.survivalist.scraping;

import com.google.common.collect.Lists;
import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ItemBreakingTracker
{
    public static final ResourceLocation PROP_KEY = Survivalist.location("ItemBreakingTracker");

    EntityPlayer player;
    World world;

    ItemStack[] equipmentSlots;

    @Nullable
    public static ItemBreakingTracker get(EntityPlayer p)
    {
        if (p.hasCapability(Handler.TRACKER, null))
            return p.getCapability(Handler.TRACKER, null);
        return null;
    }

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new Handler());
    }

    public void init(Entity entity, World world)
    {
        this.player = (EntityPlayer) entity;
        this.world = world;
    }

    public void before()
    {
        List<ItemStack> equipment = Lists.newArrayList(player.getArmorInventoryList());
        equipmentSlots = new ItemStack[equipment.size()];
        for (int i = 0; i < equipment.size(); i++)
        {
            ItemStack stack = equipment.get(i);
            equipmentSlots[i] = stack != null ? stack.copy() : null;
        }
    }

    public Collection<ItemStack> after()
    {
        if (equipmentSlots == null)
            return Collections.emptyList();

        List<ItemStack> changes = Lists.newArrayList();
        List<ItemStack> equipment = Lists.newArrayList(player.getArmorInventoryList());
        for (int i = 0; i < equipment.size(); i++)
        {
            ItemStack stack2 = equipmentSlots[i];
            if (stack2 != null)
            {
                ItemStack stack = equipment.get(i);
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

        @CapabilityInject(ItemBreakingTracker.class)
        public static Capability<ItemBreakingTracker> TRACKER;

        public static Handler instance;

        List<Triple<ItemStack, ItemStack, ItemStack>> scrapingRegistry = Lists.newArrayList();

        public Handler()
        {
            instance = this;

            CapabilityManager.INSTANCE.register(ItemBreakingTracker.class, new Capability.IStorage<ItemBreakingTracker>()
            {
                @Override
                public NBTBase writeNBT(Capability<ItemBreakingTracker> capability, ItemBreakingTracker instance, EnumFacing side)
                {
                    return new NBTTagCompound();
                }

                @Override
                public void readNBT(Capability<ItemBreakingTracker> capability, ItemBreakingTracker instance, EnumFacing side, NBTBase nbt)
                {

                }
            }, () -> null);

            registerScrapoingConversions();
        }

        void registerScrapoingConversions()
        {
            if (ConfigManager.instance.enableToolScraping)
            {
                scrapingRegistry.add(Triple.of(new ItemStack(Items.WOODEN_SHOVEL), new ItemStack(Blocks.PLANKS), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.WOODEN_HOE), new ItemStack(Blocks.PLANKS), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.WOODEN_AXE), new ItemStack(Blocks.PLANKS), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.WOODEN_PICKAXE), new ItemStack(Blocks.PLANKS), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.WOODEN_SWORD), new ItemStack(Blocks.PLANKS), new ItemStack(Items.STICK)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.STONE_SHOVEL), new ItemStack(Blocks.COBBLESTONE), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.STONE_HOE), new ItemStack(Blocks.COBBLESTONE), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.STONE_AXE), new ItemStack(Blocks.COBBLESTONE), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.STONE_PICKAXE), new ItemStack(Blocks.COBBLESTONE), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.STONE_SWORD), new ItemStack(Blocks.COBBLESTONE), new ItemStack(Items.STICK)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.IRON_HOE), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.IRON_AXE), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.IRON_SWORD), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.STICK)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.GOLDEN_SHOVEL), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.GOLDEN_HOE), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.GOLDEN_AXE), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.GOLDEN_PICKAXE), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.GOLDEN_SWORD), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.STICK)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.DIAMOND_SHOVEL), new ItemStack(Items.DIAMOND), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.DIAMOND_HOE), new ItemStack(Items.DIAMOND), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.DIAMOND_AXE), new ItemStack(Items.DIAMOND), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.DIAMOND_PICKAXE), new ItemStack(Items.DIAMOND), new ItemStack(Items.STICK)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.DIAMOND), new ItemStack(Items.STICK)));
            }

            if (ConfigManager.instance.enableArmorScraping)
            {
                scrapingRegistry.add(Triple.of(new ItemStack(Items.LEATHER_BOOTS), new ItemStack(Items.LEATHER, 2), new ItemStack(Items.LEATHER)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.LEATHER, 2), new ItemStack(Items.LEATHER)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.LEATHER, 2), new ItemStack(Items.LEATHER)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.LEATHER, 2), new ItemStack(Items.LEATHER)));

                scrapingRegistry.add(Triple.of(new ItemStack(Survivalist.Items.tanned_boots), new ItemStack(Survivalist.Items.tanned_leather, 2), new ItemStack(Survivalist.Items.tanned_leather)));
                scrapingRegistry.add(Triple.of(new ItemStack(Survivalist.Items.tanned_helmet), new ItemStack(Survivalist.Items.tanned_leather, 2), new ItemStack(Survivalist.Items.tanned_leather)));
                scrapingRegistry.add(Triple.of(new ItemStack(Survivalist.Items.tanned_chestplate), new ItemStack(Survivalist.Items.tanned_leather, 2), new ItemStack(Survivalist.Items.tanned_leather)));
                scrapingRegistry.add(Triple.of(new ItemStack(Survivalist.Items.tanned_leggings), new ItemStack(Survivalist.Items.tanned_leather, 2), new ItemStack(Survivalist.Items.tanned_leather)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.CHAINMAIL_BOOTS), new ItemStack(Survivalist.Items.chainmail, 2), new ItemStack(Survivalist.Items.chainmail)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.CHAINMAIL_HELMET), new ItemStack(Survivalist.Items.chainmail, 2), new ItemStack(Survivalist.Items.chainmail)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.CHAINMAIL_CHESTPLATE), new ItemStack(Survivalist.Items.chainmail, 2), new ItemStack(Survivalist.Items.chainmail)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.CHAINMAIL_LEGGINGS), new ItemStack(Survivalist.Items.chainmail, 2), new ItemStack(Survivalist.Items.chainmail)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.IRON_BOOTS), new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.IRON_INGOT)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.IRON_INGOT)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.IRON_INGOT)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.IRON_INGOT)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.GOLDEN_BOOTS), new ItemStack(Items.GOLD_INGOT, 2), new ItemStack(Items.GOLD_INGOT)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.GOLDEN_HELMET), new ItemStack(Items.GOLD_INGOT, 2), new ItemStack(Items.GOLD_INGOT)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.GOLDEN_CHESTPLATE), new ItemStack(Items.GOLD_INGOT, 2), new ItemStack(Items.GOLD_INGOT)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.GOLDEN_LEGGINGS), new ItemStack(Items.GOLD_INGOT, 2), new ItemStack(Items.GOLD_INGOT)));

                scrapingRegistry.add(Triple.of(new ItemStack(Items.DIAMOND_BOOTS), new ItemStack(Items.DIAMOND, 2), new ItemStack(Items.DIAMOND)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.DIAMOND, 2), new ItemStack(Items.DIAMOND)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(Items.DIAMOND, 2), new ItemStack(Items.DIAMOND)));
                scrapingRegistry.add(Triple.of(new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND, 2), new ItemStack(Items.DIAMOND)));
            }
        }

        private void onItemBroken(EntityPlayer player, ItemStack stack)
        {
            int scrappingLevel = EnchantmentHelper.getEnchantmentLevel(Survivalist.scraping, stack);

            if (player.getClass().getName().equals("com.rwtema.extrautils2.fakeplayer.XUFakePlayer"))
                return;

            boolean fortune = rnd.nextDouble() > 0.9 / (1 + scrappingLevel);

            ItemStack ret = null;

            for (Triple<ItemStack, ItemStack, ItemStack> scraping : scrapingRegistry)
            {
                ItemStack source = scraping.getLeft();

                if (source.getItem() != stack.getItem())
                    continue;

                ItemStack good = scraping.getMiddle();
                ItemStack bad = scraping.getRight();

                ret = fortune ? good.copy() : bad.copy();

                break;
            }

            if (ret != null)
            {
                Survivalist.logger.debug("Item broke (" + stack + ") and the player got " + ret + " in return!");

                Survivalist.channel.sendTo(new MessageScraping(stack, ret), (EntityPlayerMP) player);

                EntityItem entityitem = new EntityItem(player.world, player.posX, player.posY + 0.5, player.posZ, ret);
                entityitem.motionX = 0;
                entityitem.motionZ = 0;

                player.world.spawnEntity(entityitem);
            }
        }

        @SubscribeEvent
        public void onPlayerDestroyItem(PlayerDestroyItemEvent ev)
        {
            if (ev.getEntityPlayer().world.isRemote)
                return;

            ItemStack stack = ev.getOriginal();

            Item item = stack.getItem();

            if (!(item instanceof ItemTool) && !(item instanceof ItemSword))
                return;

            onItemBroken(ev.getEntityPlayer(), stack);
        }

        @SubscribeEvent
        public void onLivingHurt(LivingHurtEvent ev)
        {
            if (ev.getEntity().world.isRemote)
                return;

            if (ev.getEntity() instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) ev.getEntityLiving();

                ItemBreakingTracker.get(player).before();
            }
        }

        @SubscribeEvent
        public void entityJoinWorld(EntityJoinWorldEvent ev)
        {
            if (ev.getEntity().world.isRemote)
                return;

            if (ev.getEntity() instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) ev.getEntity();

                CombatTrackerIntercept interceptTracker = new CombatTrackerIntercept(player);
                ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, player, interceptTracker, "field_94063_bt");
            }
        }

        public void onTrackDamage(EntityPlayer player)
        {
            Collection<ItemStack> missing = ItemBreakingTracker.get(player).after();
            for (ItemStack broken : missing)
            {
                onItemBroken(player, broken);
            }
        }

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> e)
        {
            final Entity entity = e.getObject();

            if (!(entity instanceof EntityPlayerMP))
                return;

            if (entity.world == null || entity.world.isRemote)
                return;

            if (entity.hasCapability(TRACKER, null))
                return;

            e.addCapability(PROP_KEY, new ICapabilityProvider()
            {
                ItemBreakingTracker cap = new ItemBreakingTracker();

                {
                    cap.init(entity, entity.world);
                }

                @Override
                public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
                {
                    return capability == TRACKER;
                }

                @SuppressWarnings("unchecked")
                @Override
                public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
                {
                    if (capability == TRACKER)
                        return (T) cap;
                    return null;
                }
            });
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
        public int getCombatDuration()
        {
            return inner.getCombatDuration();
        }

        @Override
        public EntityLivingBase getBestAttacker()
        {
            return inner.getBestAttacker();
        }

        @Override
        public void calculateFallSuffix()
        {
            inner.calculateFallSuffix();
        }

        @Override
        public ITextComponent getDeathMessage()
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
