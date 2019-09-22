package gigaherz.survivalist.rocks;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
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

    public RockEntity(World worldIn, PlayerEntity playerIn, ItemRock itemRock)
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
}
