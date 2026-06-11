package com.fouristhenumber.utilitiesinexcess.compat.exu.postea;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Represents a Postea transformation which handles the process of migrating a now-removed items and blocks
 * in already existing saves.
 * The process goes:
 * "modid:removed" -> FMLMissingMappingsEvent -> "ourmodid:dummyitem" -> Postea -> "ourmodid:corrected_item"
 */
public interface IPosteaTransformation {

    /**
     * This method is called during a common FMLPreInitializationEvent,
     * do all your GameRegistry.registerItem and GameRegistry.registerBlock calls here.
     */
    default void registerDummies() {}

    /**
     * This method is called during a common FMLInitializationEvent,
     * do all your GameRegistry.registerTileEntity calls here.
     */
    default void registerTEDummies() {}

    /**
     * Called to add your dummy items to be mapped during the FMLMissingMappingsEvent event,
     * add your items with the old item's "modid:itemname" registry name as the key
     * and your dummy {@link Item} instance as the value.
     * <p>
     * See {@link cpw.mods.fml.common.event.FMLMissingMappingsEvent} for more.
     *
     * @param remappings Current running list of remappings, add yours to this.
     */
    default void addItemRemappings(Map<String, Item> remappings) {}

    /**
     * Called to add your dummy blocks to be mapped during the FMLMissingMappingsEvent event,
     * add your blocks with the old block's "modid:itemname" registry name as the key
     * and your dummy {@link Block} instance as the value.
     * Remember to also add it's ItemBlock instance to addItemRemappings.
     * <p>
     * See {@link cpw.mods.fml.common.event.FMLMissingMappingsEvent} for more.
     *
     * @param remappings Current running list of remappings, add yours to this.
     */
    default void addBlockRemappings(Map<String, Block> remappings) {}

    /**
     * This method is called during a common FMLPostInitializationEvent,
     * do all your BlockReplacementManager.addBlockReplacement and
     * ItemStackReplacementManager.addItemReplacement calls here.
     */
    default void registerTransformations() {}
}
