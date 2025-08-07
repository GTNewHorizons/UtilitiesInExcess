package com.fouristhenumber.utilitiesinexcess.mixins;

import java.util.List;
import java.util.function.Supplier;

import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.CursedEarthConfig;
import com.gtnewhorizon.gtnhlib.mixin.IMixins;
import com.gtnewhorizon.gtnhlib.mixin.ITargetedMod;
import com.gtnewhorizon.gtnhlib.mixin.MixinBuilder;
import com.gtnewhorizon.gtnhlib.mixin.Phase;
import com.gtnewhorizon.gtnhlib.mixin.Side;
import com.gtnewhorizon.gtnhlib.mixin.TargetedMod;

public enum Mixins implements IMixins {
    // spotless:off

    // make sure to leave a trailing comma
    CURSED_EARTH_SPAWNER(new MixinBuilder("Boost spawners when placed on Cursed Earth")
        .addMixinClasses("minecraft.MixinMobSpawnerBaseLogic_CursedEarthSpawner")
        .setPhase(Phase.EARLY)
        .setSide(Side.BOTH)
        .setApplyIf(() -> CursedEarthConfig.enableCursedEarth)
        .addTargetedMod(TargetedMod.VANILLA)),
    MAGIC_WOOD_PARTICLES(new MixinBuilder("Adds particles for Magic Wood when connected to an Enchantment Table")
        .addMixinClasses("minecraft.MixinBlockEnchantmentTable_MagicWood")
        .setPhase(Phase.EARLY)
        .setSide(Side.CLIENT)
        .setApplyIf(() -> BlockConfig.enableMagicWood)
        .addTargetedMod(TargetedMod.VANILLA)
    ),
    ; // leave trailing semicolon
    // spotless:on

    private final List<String> mixinClasses;
    private final List<ITargetedMod> targetedMods;
    private final List<ITargetedMod> excludedMods;
    private final Supplier<Boolean> applyIf;
    private final Phase phase;
    private final Side side;

    Mixins(MixinBuilder builder) {

        this.mixinClasses = builder.mixinClasses;
        this.targetedMods = builder.targetedMods;
        this.excludedMods = builder.excludedMods;
        this.applyIf = builder.applyIf;
        this.phase = builder.phase;
        this.side = builder.side;
        if (this.mixinClasses.isEmpty()) {
            throw new RuntimeException("No mixin class specified for Mixin : " + this.name());
        }
        if (this.targetedMods.isEmpty()) {
            throw new RuntimeException("No targeted mods specified for Mixin : " + this.name());
        }
        if (this.applyIf == null) {
            throw new RuntimeException("No ApplyIf function specified for Mixin : " + this.name());
        }
        if (this.phase == null) {
            throw new RuntimeException("No Phase specified for Mixin : " + this.name());
        }
        if (this.side == null) {
            throw new RuntimeException("No Side function specified for Mixin : " + this.name());
        }
    }

    @Override
    public List<String> getMixinClasses() {
        return mixinClasses;
    }

    @Override
    public Supplier<Boolean> getApplyIf() {
        return applyIf;
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public List<ITargetedMod> getTargetedMods() {
        return targetedMods;
    }

    @Override
    public List<ITargetedMod> getExcludedMods() {
        return excludedMods;
    }
}
