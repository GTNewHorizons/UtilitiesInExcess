package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import cofh.api.energy.IEnergyContainerItem;
import com.fouristhenumber.utilitiesinexcess.utils.OreDictionaryUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.RegistryDefaulted;

import static net.minecraft.block.BlockDispenser.dispenseBehaviorRegistry;

public enum AdvancedFilterMode
{
    DEFAULT("Default.exe", "Does nothing") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return true;
        }
    },
    ITEMS("Items.exe", "Filters Items") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return !(stack.getItem() instanceof ItemBlock);
        }
    },
    BLOCKS("Blocks.exe", "Filters Blocks") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return stack.getItem() instanceof ItemBlock;
        }
    },
    HASSUBTYPES("HasSubTypes.exe", "Filters items that have subtypes e.g. wool, or have Meta Items") {
        @Override
        public boolean matches(ItemStack stack)
        {
            Item item = stack.getItem();
            if (item == null)
            {
                return false;
            }
            return stack.getItem().getHasSubtypes();
        }
    },
    STACKSIZE1("StackSize1.exe", "Filters items that have a maximum stack size of 1") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return stack.getMaxStackSize() == 1;
        }
    },
    STACKSIZE64("StackSize64.exe", "Filters items that have a maximum stack size of 64") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return stack.getMaxStackSize() == 64;
        }
    },
    OREDICTORE("OreDictOre.exe", "Filters items that are ore dictionaried to Ore") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return OreDictionaryUtils.IsDictUnder(stack, "ore");
        }
    },
    OREDICTINGOT("OreDictIngot.exe", "Filters items that are ore dictionaried to Ingot") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return OreDictionaryUtils.IsDictUnder(stack, "ingot");
        }
    },
    OREDICTNUGGET("OreDictNugget.exe", "Filters items that are ore dictionaried to Nugget") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return OreDictionaryUtils.IsDictUnder(stack, "nugget");
        }
    },
    OREDICTGEM("OreDictGem.exe", "Filters items that are ore dictionaried to Gem") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return OreDictionaryUtils.IsDictUnder(stack, "gem");
        }
    },
    OREDICTDUST("OreDictDust.exe", "Filters items that are ore dictionaried to Dust") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return OreDictionaryUtils.IsDictUnder(stack, "dust");
        }
    },
    OREDICTBLOCK("OreDictBlock.exe", "Filters items that are ore dictionaried to resource Blocks") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return OreDictionaryUtils.IsDictUnder(stack, "block");
        }
    },
    ENERGYITEM("EnergyItem.exe", "Filters items that can contain RF energy") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return stack.getItem() instanceof IEnergyContainerItem;
        }
    },
    ENERGYITEMEMPTY("EnergyItemEmpty.exe", "Filters items that can contain RF energy and are empty") {
        @Override
        public boolean matches(ItemStack stack)
        {
            if (stack.getItem() instanceof IEnergyContainerItem energyItem)
            {
                return energyItem.getEnergyStored(stack) == 0;
            }
            return false;
        }
    },
    ENERGYITEMLESSTHAN50("EnergyItem<50.exe", "Filters items that can contain RF energy and contain less than 50% energy") {
        @Override
        public boolean matches(ItemStack stack)
        {
            if (stack.getItem() instanceof IEnergyContainerItem energyItem)
            {
                return energyItem.getEnergyStored(stack) < (energyItem.getMaxEnergyStored(stack) / 2);
            }
            return false;
        }
    },
    ENERGYITEMFULL("EnergyItemFull.exe", "Filters items that can contain RF energy and are full of energy") {
        @Override
        public boolean matches(ItemStack stack)
        {
            if (stack.getItem() instanceof IEnergyContainerItem energyItem)
            {
                return energyItem.getEnergyStored(stack) == energyItem.getMaxEnergyStored(stack);
            }
            return false;
        }
    },
    FOOD("Food.exe", "Filters food items") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return stack != null && stack.getItem() instanceof ItemFood;
        }
    },
    SMELTABLE("Smeltable.exe", "Filters items that are smeltable") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return FurnaceRecipes.smelting().getSmeltingResult(stack) != null;
        }
    },
    ENCHANTED("Enchanted.exe", "Filters items that are enchanted") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return stack != null && stack.isItemEnchanted();
        }
    },
    ENCHANTABLE("Enchantable.exe", "Filters items that can be enchanted") {
        @Override
        public boolean matches(ItemStack stack)
        {
            if (stack != null && stack.getItem() != null) {
                return stack.getItem().getItemEnchantability() > 0;
            }
            return false;
        }
    },
    HASCONTAINERITEM("HasContainerItem.exe", "Filters items that have a container, e.g. water buckets") {
        @Override
        public boolean matches(ItemStack stack) {
            if (stack != null && stack.getItem() != null)
            {
                return stack.getItem().hasContainerItem(stack);
            }
            return false;
        }
    },
    HASDURABILITYBARSHOWN("HasDurabilityBarShow.exe", "Filters items that have a durability bar that can be seen") {
        @Override
        public boolean matches(ItemStack stack) {
            return stack != null && stack.isItemStackDamageable() && stack.getItemDamage() != 0;
        }
    },
    HASDURABILITYBARFULL("HasDurabilityBarFull.exe", "Filters items that have a durability bar and aren't damaged") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return stack != null && stack.isItemStackDamageable() && stack.getItemDamage() == 0;
        }
    },
    DURABILITYBARLESSTHAN90("DurabilityBar<90.exe", "Filters items with less than 90% durability") {
        @Override
        public boolean matches(ItemStack stack)
        {
            float d = getDurabilityPercent(stack);
            return d >= 0 && d < 0.90f;
        }
    },
    DURABILITYBARLESSTHAN50("DurabilityBar<50.exe", "Filters items with less than 50% durability") {
        @Override
        public boolean matches(ItemStack stack)
        {
            float d = getDurabilityPercent(stack);
            return d >= 0 && d < 0.50f;
        }
    },
    DURABILITYBARLESSTHAN10("DurabilityBar<10.exe", "Filters items with less than 10% durability") {
        @Override
        public boolean matches(ItemStack stack)
        {
            float d = getDurabilityPercent(stack);
            return d >= 0 && d < 0.10f;
        }
    },
    DURABLITYBAREMPTY("DurabilityBarEmpty.exe", "Filters items that are at max damage") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return stack != null && stack.isItemStackDamageable() && stack.getItemDamage() >= stack.getMaxDamage();
        }
    },
    HASDISPLAYNAME("HasDisplayName.exe", "Filters items with unique display text such as named items") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return stack.hasDisplayName();
        }
    },
    REPAIRABLE("Repairable.exe", "Filters items that are repairable in an anvil") {
        @Override
        public boolean matches(ItemStack stack)
        {
            if (stack == null || stack.getItem() == null)
            {
                return false;
            }
            Item item = stack.getItem();
            return item.isRepairable() && item.isDamageable();
        }
    },
    HASDISPENSERBEHAVIOR("HasDispenserBehavior.exe", "Filters items that have unique usages in dispensers") {
        @Override
        public boolean matches(ItemStack stack)
        {
            return ((RegistryDefaulted) dispenseBehaviorRegistry).containsKey(stack.getItem());
        }
    },
    PLANTABLE("Plantable.exe", "Filters items that are plantable on the ground or tilled soil") {
        @Override
        public boolean matches(ItemStack stack) {
            return false; // God knows how to do this... I don't see how to do this without a registry. It's gonna be case by case.
        }
    };

    private final String label;

    // I've added a description for these filter modes because personally it took me so long
    // to figure some of them out I can assume most players have no idea. Some I still can't figureout.
    private final String description;
    AdvancedFilterMode(String label, String description)
    {
        this.label = label;
        this.description = description;
    }

    public String getLabel()
    {
        return label;
    }

    public String getDescription()
    {
        return description;
    }

    public static int getAdvFilterMode(ItemStack stack)
    {
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("AdvMode"))
        {
            return stack.stackTagCompound.getInteger("AdvMode");
        }
        return DEFAULT.ordinal();
    }

    public abstract boolean matches(ItemStack stack);

    public boolean invMatches(ItemStack stack)
    {
        return !matches(stack);
    }

    private static float getDurabilityPercent(ItemStack stack)
    {
        if (stack == null || !stack.isItemStackDamageable()) {
            return -1f;
        }

        int max = stack.getMaxDamage();
        if (max <= 0) return -1f;

        return (max - stack.getItemDamage()) / (float) max;
    }

}
