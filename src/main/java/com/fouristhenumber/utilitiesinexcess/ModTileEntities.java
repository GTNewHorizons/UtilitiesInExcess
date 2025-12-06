package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.tileentity.TileEntity;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityBlockUpdateDetector;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityConveyor;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityDrum;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityEnderMarker;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityEnderQuarry;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityMarginallyMaximisedChest;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPureLove;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRadicallyReducedChest;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRedstoneClock;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySignificantlyShrunkChest;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySoundMuffler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanEnergy;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanFluid;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanItem;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityEnderGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityFoodGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityFurnaceGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityHighTemperatureFurnaceGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityLavaGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityLowTemperatureFurnaceGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityNetherStarGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityPinkGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityPotionGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityRedstoneGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntitySolarGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityTNTGenerator;

import cpw.mods.fml.common.registry.GameRegistry;

public enum ModTileEntities {
    // spotless:off

    // make sure to leave a trailing comma
    REDSTONE_CLOCK(TileEntityRedstoneClock .class                              , "RedstoneClock"),
    TRASH_CAN_ITEM(TileEntityTrashCanItem.class                                , "TrashCanItem"),
    TRASH_CAN_FLUID(TileEntityTrashCanFluid.class                              , "TrashCanFluid"),
    TRASH_CAN_ENERGY(TileEntityTrashCanEnergy.class                            , "TrashCanEnergy"),
    DRUM(TileEntityDrum.class                                                  , "Drum"),
    PURE_LOVE(TileEntityPureLove.class                                         , "PureLove"),
    CHEST_MAX(TileEntityMarginallyMaximisedChest.class                         , "MarginallyMaximisedChest"),
    CHEST_SHRUNK(TileEntitySignificantlyShrunkChest.class                      , "SignificantlyShrunkChest"),
    CHEST_SMALL(TileEntityRadicallyReducedChest.class                          , "RadicallyReducedChest"),
    MUFFLER_SOUND(TileEntitySoundMuffler.class                                 , "SoundMuffler"),
    MUFFLER_RAIN(TileEntityRainMuffler.class                                   , "RainMuffler"),
    BLOCK_UPDATE_DETECTOR(TileEntityBlockUpdateDetector.class                  , "BlockUpdateDetector"),
    CONVEYOR(TileEntityConveyor.class                                          , "Conveyor"),
    PORTAL_UNDERWORLD(TileEntityPortalUnderWorld.class                         , "PortalUnderWorld"),
    GENERATOR_LOW_TEMP_FURNACE(TileEntityLowTemperatureFurnaceGenerator.class  , "LowTemperatureFurnaceGenerator"),
    GENERATOR_FURNACE(TileEntityFurnaceGenerator.class                         , "FurnaceGenerator"),
    GENERATOR_HIGH_TEMP_FURNACE(TileEntityHighTemperatureFurnaceGenerator.class, "HighTemperatureFurnaceGenerator"),
    GENERATOR_LAVA(TileEntityLavaGenerator.class                               , "LavaGenerator"),
    GENERATOR_ENDER(TileEntityEnderGenerator.class                             , "EnderGenerator"),
    GENERATOR_REDSTONE(TileEntityRedstoneGenerator.class                       , "RedstoneGenerator"),
    GENERATOR_FOOD(TileEntityFoodGenerator.class                               , "FoodGenerator"),
    GENERATOR_POTION(TileEntityPotionGenerator.class                           , "PotionGenerator"),
    GENERATOR_SOLAR(TileEntitySolarGenerator.class                             , "SolarGenerator"),
    GENERATOR_TNT(TileEntityTNTGenerator.class                                 , "TNTGenerator"),
    GENERATOR_PINK(TileEntityPinkGenerator.class                               , "PinkGenerator"),
    GENERATOR_NETHER_STAR(TileEntityNetherStarGenerator.class                  , "NetherStarGenerator"),
    ENDER_QUARRY(TileEntityEnderQuarry.class                                   , "EnderQuarry"),
    ENDER_MARKER(TileEntityEnderMarker.class                                   , "EnderMarker"),
    ;
    // spotless:on

    public static final ModTileEntities[] VALUES = values();

    public static void init() {
        for (ModTileEntities te : VALUES) {
            if (te.isEnabled()) {
                GameRegistry.registerTileEntity(te.clazz, te.name);
            }
        }
    }

    private final boolean isEnabled;
    private final Class<? extends TileEntity> clazz;
    private final String name;

    ModTileEntities(Class<? extends TileEntity> clazz, String name) {
        this(true, clazz, name);
    }

    ModTileEntities(Boolean enabled, Class<? extends TileEntity> clazz, String name) {
        this.isEnabled = enabled;
        this.clazz = clazz;
        this.name = "TileEntity" + name + "UIE";
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
