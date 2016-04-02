package gigaherz.survivalist.rocks;

import gigaherz.survivalist.ItemRegistered;
import gigaherz.survivalist.Survivalist;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.List;

public class ItemRock extends ItemRegistered
{
    static final String[] subNames = {
            ".rock", ".rock_andesite", ".rock_diorite", ".rock_granite"
    };

    public ItemRock(String name)
    {
        super(name);
        setHasSubtypes(true);
        setUnlocalizedName(Survivalist.MODID + ".rock");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = stack.getMetadata();

        if (meta > subNames.length)
            return getUnlocalizedName();

        return "item." + Survivalist.MODID + subNames[meta];
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (int i = 0; i < subNames.length; i++)
        { subItems.add(new ItemStack(this, 1, i)); }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        if (!playerIn.capabilities.isCreativeMode)
        {
            --itemStackIn.stackSize;
        }

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
                SoundEvents.entity_snowball_throw, SoundCategory.NEUTRAL,
                0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote)
        {
            EntityRock entity = new EntityRock(worldIn, playerIn);
            entity.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntityInWorld(entity);
        }

        playerIn.addStat(StatList.getObjectUseStats(this));

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }
}
