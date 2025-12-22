package com.fouristhenumber.utilitiesinexcess.common.blocks.ender_quarry;

import java.util.HashMap;

import com.fouristhenumber.utilitiesinexcess.config.blocks.EnderQuarryConfig;
import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class EnderQuarryUpgradeManager {

    private final HashMap<String, EnderQuarryUpgrade> activeUpgrades = new HashMap<>();

    // Add an upgrade - keeps highest tier
    public void addUpgrade(EnderQuarryUpgrade upgrade) {
        if (upgrade.isBoolean()) {
            activeUpgrades.put(upgrade.name(), upgrade);
        } else {
            String baseName = upgrade.getTierGroup();
            EnderQuarryUpgrade current = activeUpgrades.get(baseName);

            // Only set if no current upgrade or new one is higher tier
            if (current == null || upgrade.getTier() > current.getTier()) {
                activeUpgrades.put(baseName, upgrade);
            }
        }
    }

    // Check if a specific upgrade is active
    public boolean has(EnderQuarryUpgrade upgrade) {
        if (upgrade.isBoolean()) {
            return activeUpgrades.containsKey(upgrade.name());
        } else {
            EnderQuarryUpgrade current = activeUpgrades.get(upgrade.getTierGroup());
            return current != null && current.getTier() >= upgrade.getTier();
        }
    }

    // Get the active upgrade for a tiered upgrade type
    public EnderQuarryUpgrade getActive(TieredEnderQuarryUpgrade tieredUpgrade) {
        return activeUpgrades.get(tieredUpgrade.getBaseName());
    }

    // Get value for a tiered upgrade type (returns defaultValue if not present)
    public double getValue(TieredEnderQuarryUpgrade tieredUpgrade, double defaultValue) {
        EnderQuarryUpgrade upgrade = activeUpgrades.get(tieredUpgrade.getBaseName());
        return upgrade != null ? upgrade.getValue() : defaultValue;
    }

    // Check if any tier of an upgrade type is active
    public boolean hasAny(TieredEnderQuarryUpgrade tieredUpgrade) {
        return activeUpgrades.containsKey(tieredUpgrade.getBaseName());
    }

    public void remove(EnderQuarryUpgrade upgrade) {
        if (upgrade.isBoolean()) {
            activeUpgrades.remove(upgrade.name());
        } else {
            activeUpgrades.remove(upgrade.getTierGroup());
        }
    }

    public void clear() {
        activeUpgrades.clear();
    }

    public double getTotalCostMultiplier() {
        return activeUpgrades.values()
            .stream()
            .mapToDouble(EnderQuarryUpgrade::getCost)
            .sum();
    }

    public enum EnderQuarryUpgrade {

        // Boolean upgrades (presence only)
        WORLD_HOLE(EnderQuarryConfig.enderQuarryWorldHoleEnergyMultiplier, "upgrade_world_hole"),
        SILK_TOUCH(EnderQuarryConfig.enderQuarrySilkTouchEnergyMultiplier, "upgrade_silk_touch"),
        PUMP_FLUIDS(EnderQuarryConfig.enderQuarryFluidPumpEnergyMultiplier, "upgrade_pump_fluids"),

        // Tiered upgrades with hardcoded values
        SPEED_1(TieredEnderQuarryUpgrade.SPEED, 1, EnderQuarryConfig.enderQuarrySpeed1EnergyMultiplier, EnderQuarryConfig.enderQuarrySpeed1Multiplier, "upgrade_speed_1"),
        SPEED_2(TieredEnderQuarryUpgrade.SPEED, 2, EnderQuarryConfig.enderQuarrySpeed2EnergyMultiplier, EnderQuarryConfig.enderQuarrySpeed2Multiplier, "upgrade_speed_2"),
        SPEED_3(TieredEnderQuarryUpgrade.SPEED, 3, EnderQuarryConfig.enderQuarrySpeed3EnergyMultiplier, EnderQuarryConfig.enderQuarrySpeed3Multiplier, "upgrade_speed_3"),

        FORTUNE_1(TieredEnderQuarryUpgrade.FORTUNE, 1, EnderQuarryConfig.enderQuarryFortune1EnergyMultiplier, 1, "upgrade_fortune_1"),
        FORTUNE_2(TieredEnderQuarryUpgrade.FORTUNE, 2, EnderQuarryConfig.enderQuarryFortune2EnergyMultiplier, 2, "upgrade_fortune_2"),
        FORTUNE_3(TieredEnderQuarryUpgrade.FORTUNE, 3, EnderQuarryConfig.enderQuarryFortune3EnergyMultiplier, 3, "upgrade_fortune_3");

        public static final EnderQuarryUpgrade[] VALUES = values();

        private final boolean isBoolean;
        private final double value;
        private final double cost;
        private final @Nullable TieredEnderQuarryUpgrade tierGroup;
        private final int tier;
        private final String textureName;

        // Constructor for boolean upgrades
        EnderQuarryUpgrade(double cost, String textureName) {
            this.isBoolean = true;
            value = 0.0;
            this.cost = cost;
            tierGroup = null;
            tier = 0;
            this.textureName = String.format("%s:%s", UtilitiesInExcess.MODID, textureName);
        }

        // Constructor for value-based upgrades
        EnderQuarryUpgrade(@Nullable TieredEnderQuarryUpgrade tierGroup, int tier, double cost, double value,
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

    public enum TieredEnderQuarryUpgrade {

        SPEED,
        FORTUNE;

        public String getBaseName() {
            return this.name();
        }
    }
}
