package com.fouristhenumber.utilitiesinexcess.common.blocks.void_quarry;

import java.util.HashMap;
import java.util.HashSet;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.blocks.VoidQuarryConfig;

public class VoidQuarryUpgradeManager {

    private final HashMap<String, VoidQuarryUpgrade> activeUpgrades = new HashMap<>();
    public double totalCostMultiplier = 0.0;

    // Add an upgrade - keeps highest tier
    public void addUpgrade(VoidQuarryUpgrade upgrade) {
        if (upgrade.isBoolean()) {
            activeUpgrades.put(upgrade.name(), upgrade);
        } else {
            String baseName = upgrade.getTierGroup();
            VoidQuarryUpgrade current = activeUpgrades.get(baseName);

            // Only set if no current upgrade or new one is higher tier
            if (current == null || upgrade.getTier() > current.getTier()) {
                activeUpgrades.put(baseName, upgrade);
            }
        }
        totalCostMultiplier = getTotalCostMultiplier();
    }

    // Check if a specific upgrade is active
    public boolean has(VoidQuarryUpgrade upgrade) {
        if (upgrade.isBoolean()) {
            return activeUpgrades.containsKey(upgrade.name());
        } else {
            VoidQuarryUpgrade current = activeUpgrades.get(upgrade.getTierGroup());
            return current != null && current.getTier() >= upgrade.getTier();
        }
    }

    // Get the active upgrade for a tiered upgrade type
    public VoidQuarryUpgrade getActive(TieredVoidQuarryUpgrade tieredUpgrade) {
        return activeUpgrades.get(tieredUpgrade.getBaseName());
    }

    // Get value for a tiered upgrade type (returns defaultValue if not present)
    public double getValue(TieredVoidQuarryUpgrade tieredUpgrade, double defaultValue) {
        VoidQuarryUpgrade upgrade = activeUpgrades.get(tieredUpgrade.getBaseName());
        return upgrade != null ? upgrade.getValue() : defaultValue;
    }

    // Check if any tier of an upgrade type is active
    public boolean hasAny(TieredVoidQuarryUpgrade tieredUpgrade) {
        return activeUpgrades.containsKey(tieredUpgrade.getBaseName());
    }

    public void remove(VoidQuarryUpgrade upgrade) {
        if (upgrade.isBoolean()) {
            activeUpgrades.remove(upgrade.name());
        } else {
            activeUpgrades.remove(upgrade.getTierGroup());
        }
        totalCostMultiplier = getTotalCostMultiplier();
    }

    public void clear() {
        activeUpgrades.clear();
        totalCostMultiplier = 0.0;
    }

    /**
     * Calculate the total cost multiplier from all active upgrades, mainly used internally.
     * Use totalCostMultiplier field for fewer recalculations instead.
     */
    public double getTotalCostMultiplier() {
        double total = 0.0;
        HashSet<TieredVoidQuarryUpgrade> countedTieredUpgrades = new HashSet<>();
        for (VoidQuarryUpgrade upgrade : VoidQuarryUpgrade.VALUES) {
            if (this.has(upgrade)) {
                if (upgrade.isBoolean()) {
                    total += upgrade.cost;
                } else {
                    // Tiered upgrade - only count the highest tier once
                    TieredVoidQuarryUpgrade tierGroup = upgrade.tierGroup;
                    if (tierGroup == null || countedTieredUpgrades.contains(tierGroup)) {
                        continue;
                    }
                    countedTieredUpgrades.add(tierGroup);

                    total += this.getActive(tierGroup).cost;
                }
            }
        }
        return total;
    }

    public enum VoidQuarryUpgrade {

        // Boolean upgrades (presence only)
        WORLD_HOLE(VoidQuarryConfig.voidQuarryWorldHoleEnergyMultiplier, "upgrade_world_hole"),
        SILK_TOUCH(VoidQuarryConfig.voidQuarrySilkTouchEnergyMultiplier, "upgrade_silk_touch"),
        PUMP_FLUIDS(VoidQuarryConfig.voidQuarryFluidPumpEnergyMultiplier, "upgrade_pump_fluids"),

        // Tiered upgrades with hardcoded values
        SPEED_1(TieredVoidQuarryUpgrade.SPEED, 1, VoidQuarryConfig.voidQuarrySpeed1EnergyMultiplier,
            VoidQuarryConfig.voidQuarrySpeed1Multiplier, "upgrade_speed_1"),
        SPEED_2(TieredVoidQuarryUpgrade.SPEED, 2, VoidQuarryConfig.voidQuarrySpeed2EnergyMultiplier,
            VoidQuarryConfig.voidQuarrySpeed2Multiplier, "upgrade_speed_2"),
        SPEED_3(TieredVoidQuarryUpgrade.SPEED, 3, VoidQuarryConfig.voidQuarrySpeed3EnergyMultiplier,
            VoidQuarryConfig.voidQuarrySpeed3Multiplier, "upgrade_speed_3"),

        FORTUNE_1(TieredVoidQuarryUpgrade.FORTUNE, 1, VoidQuarryConfig.voidQuarryFortune1EnergyMultiplier, 1,
            "upgrade_fortune_1"),
        FORTUNE_2(TieredVoidQuarryUpgrade.FORTUNE, 2, VoidQuarryConfig.voidQuarryFortune2EnergyMultiplier, 2,
            "upgrade_fortune_2"),
        FORTUNE_3(TieredVoidQuarryUpgrade.FORTUNE, 3, VoidQuarryConfig.voidQuarryFortune3EnergyMultiplier, 3,
            "upgrade_fortune_3");

        public static final VoidQuarryUpgrade[] VALUES = values();

        private final boolean isBoolean;
        private final double value;
        private final double cost;
        private final @Nullable TieredVoidQuarryUpgrade tierGroup;
        private final int tier;
        private final String textureName;

        // Constructor for boolean upgrades
        VoidQuarryUpgrade(double cost, String textureName) {
            this.isBoolean = true;
            value = 0.0;
            this.cost = cost;
            tierGroup = null;
            tier = 0;
            this.textureName = String.format("%s:%s", UtilitiesInExcess.MODID, textureName);
        }

        // Constructor for value-based upgrades
        VoidQuarryUpgrade(@Nullable TieredVoidQuarryUpgrade tierGroup, int tier, double cost, double value,
            String textureName) {
            this.isBoolean = false;
            this.value = value;
            this.cost = cost;
            this.tierGroup = tierGroup;
            this.tier = tier;
            this.textureName = String.format("%s:%s", UtilitiesInExcess.MODID, textureName);
        }

        public boolean isBoolean() {
            return isBoolean;
        }

        public double getValue() {
            if (isBoolean) {
                throw new IllegalStateException(this + " is a boolean upgrade and has no value");
            }
            return value;
        }

        public double getCost() {
            return cost;
        }

        public String getTierGroup() {
            if (isBoolean || tierGroup == null) {
                throw new IllegalStateException(this + " is a boolean upgrade and has no tier group");
            }
            return tierGroup.getBaseName();
        }

        public int getTier() {
            if (isBoolean) {
                throw new IllegalStateException(this + " is a boolean upgrade and has no tier");
            }
            return tier;
        }

        public String getTextureName() {
            return textureName;
        }
    }

    public enum TieredVoidQuarryUpgrade {

        SPEED,
        FORTUNE;

        public String getBaseName() {
            return this.name();
        }
    }
}
