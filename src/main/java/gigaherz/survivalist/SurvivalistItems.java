package gigaherz.survivalist;

import gigaherz.survivalist.rocks.RockItem;
import gigaherz.survivalist.util.RegSitter;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.RegistryObject;

public class SurvivalistItems
{
    static final RegSitter HELPER = new RegSitter(SurvivalistMod.MODID);
    
    public static final RegistryObject<Item> TANNED_LEATHER = HELPER.item("tanned_leather", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS))).defer();
    public static final RegistryObject<Item> CHAINMAIL = HELPER.item("chainmail", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS))).defer();
    public static final RegistryObject<Item> JERKY = HELPER.item("jerky", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS).food(SurvivalistFoods.JERKY))).defer();
    public static final RegistryObject<Item> DOUGH = HELPER.item("dough", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS).food(SurvivalistFoods.DOUGH))).defer();
    public static final RegistryObject<Item> ROUND_BREAD = HELPER.item("round_bread", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS).food(SurvivalistFoods.BREAD))).defer();
    public static final RegistryObject<Item> COPPER_NUGGET = HELPER.item("copper_nugget", () -> new Item(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<Item> TIN_NUGGET = HELPER.item("tin_nugget", () -> new Item(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<Item> LEAD_NUGGET = HELPER.item("lead_nugget", () -> new Item(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<Item> SILVER_NUGGET = HELPER.item("silver_nugget", () -> new Item(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<Item> ALUMINUM_NUGGET = HELPER.item("aluminum_nugget", () -> new Item(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> STONE_ROCK = HELPER.item("stone_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> ANDESITE_ROCK = HELPER.item("andesite_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> DIORITE_ROCK = HELPER.item("diorite_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> GRANITE_ROCK = HELPER.item("granite_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> IRON_ORE_ROCK = HELPER.item("iron_ore_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> GOLD_ORE_ROCK = HELPER.item("gold_ore_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> COPPER_ORE_ROCK = HELPER.item("copper_ore_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> TIN_ORE_ROCK = HELPER.item("tin_ore_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> LEAD_ORE_ROCK = HELPER.item("lead_ore_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> SILVER_ORE_ROCK = HELPER.item("silver_ore_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<RockItem> ALUMINUM_ORE_ROCK = HELPER.item("aluminum_ore_rock", () -> new RockItem(new Item.Properties().group(ItemGroup.MISC))).defer();
    public static final RegistryObject<AxeItem> HATCHET = HELPER.<AxeItem>item("hatchet", () -> new AxeItem(SurvivalistItems.TOOL_FLINT, 8.0F, -3.1F, new Item.Properties().group(ItemGroup.TOOLS)){}).defer();
    public static final RegistryObject<PickaxeItem> PICK = HELPER.<PickaxeItem>item("pick", () -> new PickaxeItem(SurvivalistItems.TOOL_FLINT, 4, -2.6F, new Item.Properties().group(ItemGroup.TOOLS)){}).defer();
    public static final RegistryObject<ShovelItem> SPADE = HELPER.item("spade", () -> new ShovelItem(SurvivalistItems.TOOL_FLINT, 3, -2.1F, new Item.Properties().group(ItemGroup.TOOLS))).defer();
    public static final RegistryObject<ArmorItem> TANNED_HELMET = HELPER.item("tanned_helmet", () -> new ArmorItem(SurvivalistItems.TANNED_LEATHER_MATERIAL, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.TOOLS))).defer();
    public static final RegistryObject<ArmorItem> TANNED_CHESTPLATE = HELPER.item("tanned_chestplate", () -> new ArmorItem(SurvivalistItems.TANNED_LEATHER_MATERIAL, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.TOOLS))).defer();
    public static final RegistryObject<ArmorItem> TANNED_LEGGINGS = HELPER.item("tanned_leggings", () -> new ArmorItem(SurvivalistItems.TANNED_LEATHER_MATERIAL, EquipmentSlotType.LEGS, new Item.Properties().group(ItemGroup.TOOLS))).defer();
    public static final RegistryObject<ArmorItem> TANNED_BOOTS = HELPER.item("tanned_boots", () -> new ArmorItem(SurvivalistItems.TANNED_LEATHER_MATERIAL, EquipmentSlotType.FEET, new Item.Properties().group(ItemGroup.TOOLS))).defer();
    public static final RegistryObject<Item> PLANT_FIBRES = HELPER.item("plant_fibres", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS))).defer();

    public static final IArmorMaterial TANNED_LEATHER_MATERIAL = new IArmorMaterial()
    {
        private final int[] armors = new int[]{1, 2, 3, 1};
        private final Tag<Item> leather_tag = new ItemTags.Wrapper(new ResourceLocation("survivalist:items/tanned_leather"));
        private final Ingredient leather_tag_ingredient = Ingredient.fromTag(leather_tag);

        @Override
        public int getDurability(EquipmentSlotType slotIn)
        {
            return 12;
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return this.armors[slotIn.getIndex()];
        }

        @Override
        public int getEnchantability()
        {
            return 15;
        }

        @Override
        public SoundEvent getSoundEvent()
        {
            return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairMaterial()
        {
            return leather_tag_ingredient;
        }

        @Override
        public String getName()
        {
            return "tanned_leather";
        }

        @Override
        public float getToughness()
        {
            return 1;
        }
    };

    public static final IItemTier TOOL_FLINT = new IItemTier()
    {
        private final Tag<Item> flint_tag = new ItemTags.Wrapper(new ResourceLocation("forge:items/flint"));
        private final Ingredient flint_tag_ingredient = Ingredient.fromTag(flint_tag);

        @Override
        public int getMaxUses()
        {
            return 150;
        }

        @Override
        public float getEfficiency()
        {
            return 5.0f;
        }

        @Override
        public float getAttackDamage()
        {
            return 1.5f;
        }

        @Override
        public int getHarvestLevel()
        {
            return 1;
        }

        @Override
        public int getEnchantability()
        {
            return 5;
        }

        @Override
        public Ingredient getRepairMaterial()
        {
            return flint_tag_ingredient;
        }
    };

}
