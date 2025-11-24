package com.fouristhenumber.utilitiesinexcess.mixins;

import static com.fouristhenumber.utilitiesinexcess.mixins.TargetedMod.ANGELICA;

import javax.annotation.Nonnull;

import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.CursedEarthConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {
    // spotless:off

    // make sure to leave a trailing comma
    CURSED_EARTH_SPAWNER(new MixinBuilder("Boost spawners when placed on Cursed / Blessed Earth")
        .addCommonMixins("minecraft.MixinMobSpawnerBaseLogic_CursedEarthSpawner")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> CursedEarthConfig.enableCursedEarth || CursedEarthConfig.enableBlessedEarth)
        /*.addRequiredMod(TargetedMod.VANILLA)*/),
    MAGIC_WOOD_PARTICLES(new MixinBuilder("Adds particles for Magic Wood when connected to an Enchantment Table")
        .addClientMixins("minecraft.MixinBlockEnchantmentTable_MagicWood")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> BlockConfig.enableMagicWood)
        .addExcludedMod(ANGELICA)
        /*.addRequiredMod(TargetedMod.VANILLA)*/
    ),
    GLOVE(new MixinBuilder("Implements the Glove's special right click")
        .addCommonMixins("minecraft.MixinNetHandlerPlayServer_Glove", "minecraft.MixinItemRenderer_Glove", "minecraft.MixinPlayerControllerMP_Glove")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> ItemConfig.enableGlove)
        /*.addRequiredMod(TargetedMod.VANILLA)*/),
    BUABLE_RENDERS(new MixinBuilder("Renders equipped baubles on the player")
        .addCommonMixins("minecraft.MixinModelBiped_Baubles", "minecraft.MixinModelRenderer_Baubles")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> OtherConfig.enableBaubleRenders)
        /*.addRequiredMod(TargetedMod.VANILLA)*/),
    ACCESSORS(new MixinBuilder("Accessors for the mod to use")
        .setPhase(Phase.EARLY)
        .addCommonMixins("minecraft.accessors.AccessorEntityZombie",
            "minecraft.accessors.AccessorItemTool",
            "minecraft.accessors.AccessorItemSword",
            "minecraft.accessors.AccessorEntityLivingBase",
            "minecraft.accessors.AccessorPotionEffect",
            "minecraft.accessors.AccessorItemRenderer",
            "minecraft.accessors.AccessorClientMinecraft")
    )
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
