package gigaherz.survivalist.rocks;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

public class RockEntity extends ProjectileItemEntity implements IEntityAdditionalSpawnData
{
    @ObjectHolder("survivalist:thrown_rock")
    public static EntityType<RockEntity> TYPE;

    private Item item;

    public RockEntity(EntityType<RockEntity> type, World world)
    {
        super(type, world);
    }

    public RockEntity(World worldIn, PlayerEntity playerIn, RockItem itemRock)
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
            Vec3d vec3d = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, ((double)this.rand.nextFloat() - 0.5D) * 0.1D, 0.0D);
            vec3d = vec3d.rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F));
            vec3d = vec3d.rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
            this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), vec3d.x, vec3d.y + 0.05D, vec3d.z);
        }

        if (!this.world.isRemote)
        {
            this.remove();
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        buffer.writeRegistryId(item);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData)
    {
        item = additionalData.readRegistryId();
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem()
    {
        return item;
    }
}
