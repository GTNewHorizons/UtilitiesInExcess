package com.fouristhenumber.utilitiesinexcess.mixins;

import java.util.List;
import java.util.function.Supplier;

import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.CursedEarthConfig;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

import javax.annotation.Nonnull;


public enum Mixins implements IMixins {
    // spotless:off

    // make sure to leave a trailing comma
    CURSED_EARTH_SPAWNER(new MixinBuilder("Boost spawners when placed on Cursed Earth")
        .addCommonMixins("minecraft.MixinMobSpawnerBaseLogic_CursedEarthSpawner")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> CursedEarthConfig.enableCursedEarth)
        /*.addRequiredMod(TargetedMod.VANILLA)*/),
    MAGIC_WOOD_PARTICLES(new MixinBuilder("Adds particles for Magic Wood when connected to an Enchantment Table")
        .addClientMixins("minecraft.MixinBlockEnchantmentTable_MagicWood")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> BlockConfig.enableMagicWood)
        /*.addRequiredMod(TargetedMod.VANILLA)*/
    ),
    ; // leave trailing semicolon
    // spotless:on

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return this.builder;
    }
}
