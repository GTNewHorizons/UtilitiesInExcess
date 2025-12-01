package com.fouristhenumber.utilitiesinexcess.compat.exu;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExuCompat {

    public static void onMissingMappings(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()) {
            if (mapping == null) continue;

            if (mapping.type == GameRegistry.Type.ITEM) {
                Item newItem = Remappings.itemMappings.getOrDefault(mapping.name, null);
                if (newItem != null) {
                    mapping.remap(newItem);
                }
            } else { // BLOCK
                Block newBlock = Remappings.blockMappings.getOrDefault(mapping.name, null);
                if (newBlock != null) {
                    mapping.remap(newBlock);
                }
            }
        }
    }
}
