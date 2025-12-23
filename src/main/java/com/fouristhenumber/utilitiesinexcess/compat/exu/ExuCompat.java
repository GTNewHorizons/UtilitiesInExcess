package com.fouristhenumber.utilitiesinexcess.compat.exu;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExuCompat {

    public static void onMissingMappings(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()) {
            if (mapping == null) continue;

            if (Remappings.SKIPPED_MAPPINGS.contains(mapping.name)) {
                mapping.ignore();
                continue;
            }

            if (mapping.type == GameRegistry.Type.ITEM) {
                Item newItem = Remappings.ITEM_MAPPINGS.get(mapping.name);
                if (newItem != null) {
                    mapping.remap(newItem);
                }
            } else { // BLOCK
                Block newBlock = Remappings.BLOCK_MAPPINGS.get(mapping.name);
                if (newBlock != null) {
                    mapping.remap(newBlock);
                }
            }
        }
    }
}
