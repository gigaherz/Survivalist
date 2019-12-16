package gigaherz.survivalist.slime;

import gigaherz.survivalist.ConfigManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.*;

public class SlimeMerger
{
    private static final int BIG_SLIME = 8;
    private static final int AGE_LIMIT = 200;
    private static Random rand = new Random();

    public static void register()
    {
        MinecraftForge.EVENT_BUS.register(new SlimeMerger());
    }

    @SubscribeEvent
    public void slimeConstruct(EntityJoinWorldEvent event)
    {
        if (!ConfigManager.SERVER.mergeSlimes.get())
            return;

        Entity entity = event.getEntity();

        if (!(entity instanceof SlimeEntity))
            return;

        SlimeEntity slime = (SlimeEntity) entity;

        slime.goalSelector.addGoal(7, new MergeWithNearbySlimesGoal(slime));
        slime.targetSelector.addGoal(5, new MoveTowardNearestSlimeGoal(slime));
    }

    private static boolean isValidTarget(SlimeEntity slime, LivingEntity entity)
    {
        return entity != slime
                && entity.isAlive()
                && entity.getClass() == slime.getClass()
                && entity.ticksExisted >= AGE_LIMIT
                && ((SlimeEntity) entity).getSlimeSize() == slime.getSlimeSize();
    }

    static class MergeWithNearbySlimesGoal extends Goal
    {
        private static Method setSlimeSizeMethod;

        static
        {
            setSlimeSizeMethod = ObfuscationReflectionHelper.findMethod(SlimeEntity.class, "func_70799_a", void.class, int.class, boolean.class);
        }

        private final SlimeEntity slime;
        private final Sorter sorter;

        public MergeWithNearbySlimesGoal(SlimeEntity slimeIn)
        {
            this.slime = slimeIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
            this.sorter = new Sorter(slimeIn);
        }

        private List<SlimeEntity> findOtherSlimes()
        {
            return slime.world
                    .getEntitiesWithinAABB(SlimeEntity.class,
                            slime.getBoundingBox().grow(slime.getSlimeSize() * 1.5, slime.getSlimeSize(), slime.getSlimeSize() * 1.5),
                            (other) -> isValidTarget(slime, other));
        }

        @Override
        public boolean shouldExecute()
        {
            if (slime.getSlimeSize() >= BIG_SLIME)
                return false;
            if (slime.ticksExisted < AGE_LIMIT)
                return false;
            if (rand.nextFloat() > 0.5)
                return false;
            return (findOtherSlimes().size() >= 3);
        }

        @Override
        public void startExecuting()
        {
            List<SlimeEntity> list = findOtherSlimes();
            if (list.size() >= 3)
            {
                Collections.sort(list, this.sorter);

                double x = slime.posX;
                double y = slime.posY;
                double z = slime.posZ;

                int size = slime.getSlimeSize() + 1;
                for (int i = 0; i < 8 * size; i++)
                {
                    float angle = rand.nextFloat();
                    float speed = size * (1 + rand.nextFloat() * 0.5f);
                    slime.world.addParticle(ParticleTypes.ITEM_SLIME, slime.posX, slime.posY, slime.posZ,
                            speed * Math.cos(angle), 1, speed * Math.sin(angle));
                }

                for (int i = 0; i < 3; i++)
                {
                    SlimeEntity target = list.get(i);
                    x += target.posX;
                    y += target.posY;
                    z += target.posZ;
                    target.remove();
                    for (int j = 0; j < 8 * size; j++)
                    {
                        float angle = rand.nextFloat();
                        float speed = size * (1 + rand.nextFloat() * 0.5f);
                        slime.world.addParticle(ParticleTypes.ITEM_SLIME, target.posX, target.posY, target.posZ,
                                speed * Math.cos(angle), 1, speed * Math.sin(angle));
                    }
                }

                x /= 4;
                y /= 4;
                z /= 4;

                int newSize = slime.getSlimeSize() + 1;
                try
                {
                    setSlimeSizeMethod.invoke(slime, newSize, true);
                    slime.setPosition(x, y, z);
                }
                catch (ReflectiveOperationException e)
                {
                    throw new ReportedException(new CrashReport("Could not call method '" + setSlimeSizeMethod.getName() + "'", e));
                }

                slime.playSound(Survivalist.SOUND_SHLOP, 1, 0.8f + rand.nextFloat() * 0.4f);
            }

            super.startExecuting();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return false;
        }
    }

    public static class Sorter implements Comparator<Entity>
    {
        private final Entity entity;

        public Sorter(Entity entityIn)
        {
            this.entity = entityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_)
        {
            double d0 = this.entity.getDistanceSq(p_compare_1_);
            double d1 = this.entity.getDistanceSq(p_compare_2_);

            if (d0 < d1)
            {
                return -1;
            }
            else
            {
                return d0 > d1 ? 1 : 0;
            }
        }
    }

    public static class MoveTowardNearestSlimeGoal extends NearestAttackableTargetGoal<SlimeEntity>
    {
        private static final int EXECUTE_CHANCE = 20;

        private final SlimeEntity slime;

        public MoveTowardNearestSlimeGoal(final SlimeEntity mobIn)
        {
            super(mobIn, SlimeEntity.class, EXECUTE_CHANCE, true, true, null);
            this.slime = mobIn;
            this.targetEntitySelector = (new EntityPredicate())
                    .setSkipAttackChecks()
                    .setLineOfSiteRequired()
                    .setDistance(this.getTargetDistance())
                    .setCustomPredicate(other -> isValidTarget(slime, other));
        }

        @Override
        public boolean shouldExecute()
        {
            if (slime.getSlimeSize() >= BIG_SLIME)
                return false;
            if (slime.ticksExisted < AGE_LIMIT)
                return false;
            if (rand.nextFloat() > 0.05f)
                return false;
            return super.shouldExecute();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            if (rand.nextFloat() < 0.01f)
                return false;
            return super.shouldContinueExecuting();
        }
    }
}
