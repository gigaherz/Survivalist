package gigaherz.survivalist.slime;

import com.google.common.base.Predicate;
import gigaherz.survivalist.Survivalist;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
        Entity entity = event.getEntity();

        if (!(entity instanceof EntitySlime))
            return;

        EntitySlime slime = (EntitySlime) entity;

        slime.tasks.addTask(7, new AISlimeMerge(slime));
        slime.targetTasks.addTask(5, new EntityAIFindOtherSlimeNearest(slime));
    }

    static class AISlimeMerge extends EntityAIBase
    {
        private static Method setSlimeSizeMethod;

        static
        {
            setSlimeSizeMethod = ObfuscationReflectionHelper.findMethod(EntitySlime.class, "func_70799_a", void.class, int.class, boolean.class);
        }

        private final EntitySlime slime;
        private final EntityAINearestAttackableTarget.Sorter sorter;

        public AISlimeMerge(EntitySlime slimeIn)
        {
            this.slime = slimeIn;
            this.setMutexBits(2);
            this.sorter = new EntityAINearestAttackableTarget.Sorter(slimeIn);
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
            List<EntitySlime> list = slime.world
                    .getEntitiesWithinAABB(EntitySlime.class,
                            slime.getEntityBoundingBox().grow(slime.getSlimeSize() * 1.5, slime.getSlimeSize(), slime.getSlimeSize() * 1.5),
                            (other) -> other != slime
                                    && other.isEntityAlive()
                                    && other.getClass() == slime.getClass()
                                    && other.ticksExisted > AGE_LIMIT
                                    && other.getSlimeSize() == slime.getSlimeSize());
            return (list.size() >= 3);
        }

        @Override
        public void startExecuting()
        {
            List<EntitySlime> list = slime.world
                    .getEntitiesWithinAABB(EntitySlime.class,
                            slime.getEntityBoundingBox().grow(slime.getSlimeSize() * 1.5, slime.getSlimeSize(), slime.getSlimeSize() * 1.5),
                            (other) -> other != slime
                                    && other.isEntityAlive()
                                    && other.getClass() == slime.getClass()
                                    && other.ticksExisted > AGE_LIMIT
                                    && other.getSlimeSize() == slime.getSlimeSize());
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
                    slime.world.spawnParticle(EnumParticleTypes.SLIME, slime.posX, slime.posY, slime.posZ,
                            speed * Math.cos(angle), 1, speed * Math.sin(angle));
                }

                for (int i = 0; i < 3; i++)
                {
                    EntitySlime target = list.get(i);
                    x += target.posX;
                    y += target.posY;
                    z += target.posZ;
                    target.setDead();
                    for (int j = 0; j < 8 * size; j++)
                    {
                        float angle = rand.nextFloat();
                        float speed = size * (1 + rand.nextFloat() * 0.5f);
                        slime.world.spawnParticle(EnumParticleTypes.SLIME, target.posX, target.posY, target.posZ,
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

                slime.playSound(Survivalist.shlop, 1, 0.8f + rand.nextFloat() * 0.4f);
            }

            super.startExecuting();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return false;
        }
    }

    public static class EntityAIFindOtherSlimeNearest extends EntityAIFindEntityNearest
    {
        private static Field predicateField;

        int cooldown = 5;

        static
        {
            predicateField = ObfuscationReflectionHelper.findField(EntityAIFindEntityNearest.class, "field_179443_c");
        }

        private final EntitySlime slime;

        public EntityAIFindOtherSlimeNearest(final EntitySlime mobIn)
        {
            super(mobIn, EntitySlime.class);
            this.slime = mobIn;
            Predicate<EntityLivingBase> predicate = entity -> {
                double range = EntityAIFindOtherSlimeNearest.this.getFollowRange();
                return entity != null
                        && entity != mobIn
                        && entity.getClass() == mobIn.getClass()
                        && entity.ticksExisted > AGE_LIMIT
                        && !entity.isInvisible()
                        && entity.getDistance(mobIn) <= range
                        && ((EntitySlime) entity).getSlimeSize() == mobIn.getSlimeSize()
                        && EntityAITarget.isSuitableTarget(mobIn, entity, false, true);
            };

            try
            {
                predicateField.set(this, predicate);
            }
            catch (IllegalAccessException e)
            {
                throw new ReportedException(new CrashReport("Could not set private field '" + predicateField.getName() + "'", e));
            }
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
