package gigaherz.survivalist.rocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class RockItem extends Item
{

    public RockItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand)
    {
        ItemStack stack = playerIn.getHeldItem(hand);

        if (!playerIn.abilities.isCreativeMode)
        {
            stack.grow(-1);
        }

        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
                0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote)
        {
            RockEntity entity = new RockEntity(worldIn, playerIn, this);
            entity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.addEntity(entity);
        }

        //playerIn.addStat(StatList.getObjectUseStats(this));

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}