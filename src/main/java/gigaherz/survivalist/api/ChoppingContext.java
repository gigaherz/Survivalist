package gigaherz.survivalist.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Random;

public class ChoppingContext extends ItemHandlerWrapper
{
    final PlayerEntity player;
    final int axeLevel;
    final int fortune;
    final Random random;

    public ChoppingContext(IItemHandlerModifiable inner, @Nullable PlayerEntity player, int axeLevel, int fortune, Random random)
    {
        super(inner, player != null ? player::getPositionVec : null, 64);
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

    public Random getRandom()
    {
        return random;
    }
}