package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class TileEntityRedstoneGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityRedstoneGenerator() {
        super(5000000, 5000);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return 1000;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        return getPotionComplexity(stack) * 600;
    }

    // Each effect is 1 complexity. This is not relevant in vanilla but will make it compatible with custom modded
    // potions.
    // Similarly, each level above 1 gives an additional point due to requiring glowstone. Again, modded potions could
    // be even stronger.
    // If the duration is longer than 3600 ticks, 1 point because redstone has been applied.
    // If the potion is splash, 1 point because gunpowder has been applied.
    protected int getPotionComplexity(ItemStack potion) {
        if (!(potion.getItem() instanceof ItemPotion)) return 0;
        List<PotionEffect> effects = Items.potionitem.getEffects(potion);
        // Awkward and mundane potions are still 1 step!
        if (effects == null || effects.isEmpty()) return 1;

        int complexity = 1;

        for (PotionEffect e : effects) {
            complexity += 1;
            complexity += e.getAmplifier();

            if (e.getDuration() > 3600) complexity += 1;

            // These effects grant an additional point because they are brewed using the additional fermented spider eye
            // step.
            if (e.getPotionID() == Potion.harm.id || e.getPotionID() == Potion.invisibility.id) {
                complexity += 1;
            }
        }

        if (ItemPotion.isSplash(potion.getItemDamage())) {
            complexity += 1;
        }

        return complexity;
    }

    @Override
    public String getInventoryName() {
        return "tile.potion_generator.name";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() instanceof ItemPotion;
    }
}
