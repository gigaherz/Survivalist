package gigaherz.survivalist.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class ChoppingContext extends ItemHandlerWrapper
{
    protected final PlayerEntity player;
    protected final int axeLevel;
    protected final int fortune;
    protected final Random random;

    public ChoppingContext(IItemHandlerModifiable inner, @Nullable PlayerEntity player, @Nullable Supplier<Vector3d> location, int axeLevel, int fortune, @Nullable Random random)
    {
        super(inner, location, 64);
        this.player = player;
        this.axeLevel = axeLevel;
        this.fortune = fortune;
        this.random = random;
    }

    @Nullable
    public PlayerEntity getPlayer()
    {
        return player;
    }

    public int getAxeLevel()
    {
        return axeLevel;
    }

    public int getFortune()
    {
        return fortune;
    }

    @Nullable
    public Random getRandom()
    {
        return random;
    }
}