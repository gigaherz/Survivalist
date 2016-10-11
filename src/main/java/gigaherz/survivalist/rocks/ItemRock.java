package gigaherz.survivalist.rocks;

import gigaherz.common.state.IItemState;
import gigaherz.common.state.IItemStateManager;
import gigaherz.common.state.ItemStateful;
import gigaherz.common.state.implementation.ItemStateManager;
import gigaherz.survivalist.Survivalist;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ItemRock extends ItemStateful
{
    public static final PropertyEnum<Subtype> TYPE = PropertyEnum.create("rock", Subtype.class);

    public ItemRock(String name)
    {
        super(name);
        setHasSubtypes(true);
        setUnlocalizedName(Survivalist.MODID + ".rock");
        setCreativeTab(CreativeTabs.MATERIALS);
    }

    @Override
    public IItemStateManager createStateManager()
    {
        return new ItemStateManager(this, TYPE);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        IItemState state = getStateManager().get(stack.getMetadata());

        if (state == null)
            return getUnlocalizedName();

        String subName = state.getValue(TYPE).getUnlocalizedSuffix();

        return "item." + Survivalist.MODID + subName;
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (Subtype type : TYPE.getAllowedValues())
        {
            IItemState state = getDefaultState().withProperty(TYPE, type);
            subItems.add(state.getStack());
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        if (!playerIn.capabilities.isCreativeMode)
        {
            --itemStackIn.stackSize;
        }

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
                0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote)
        {
            EntityRock entity = new EntityRock(worldIn, playerIn);
            entity.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntityInWorld(entity);
        }

        playerIn.addStat(StatList.getObjectUseStats(this));

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

    public ItemStack getStack(Subtype iron)
    {
        return getDefaultState().withProperty(TYPE, iron).getStack();
    }

    public enum Subtype implements IStringSerializable
    {
        NORMAL("normal", ".rock"),
        ANDESITE("andesite", ".rock_andesite"),
        DIORITE("diorite", ".rock_diorite"),
        GRANITE("granite", ".rock_granite");

        final String name;
        final String unlocalizedSuffix;

        Subtype(String name, String unlocalizedSuffix)
        {
            this.name = name;
            this.unlocalizedSuffix = unlocalizedSuffix;
        }

        @Override
        public String getName()
        {
            return name;
        }

        public String getUnlocalizedSuffix()
        {
            return unlocalizedSuffix;
        }
    }
}
