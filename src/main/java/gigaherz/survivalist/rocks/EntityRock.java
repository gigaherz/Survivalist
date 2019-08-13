package gigaherz.survivalist.rocks;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class EntityRock extends ProjectileItemEntity
{
    @ObjectHolder("survivalist:rock")
    public static EntityType<EntityRock> TYPE;

    private Item item;

    public EntityRock(World worldIn, PlayerEntity playerIn, ItemRock itemRock)
    {
        super(TYPE, playerIn, worldIn);
        this.item = itemRock;
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result instanceof EntityRayTraceResult)
        {
            ((EntityRayTraceResult)result).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 1);
        }

        for (int j = 0; j < 8; ++j)
        {
            this.world.addParticle(ParticleTypes.ITEM_SNOWBALL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (!this.world.isRemote)
        {
            this.remove();
        }
    }

    @Override
    protected Item func_213885_i()
    {
        return item;
    }
}
