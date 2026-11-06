package com.fouristhenumber.utilitiesinexcess.compat.angelica.coloredblocks;

import java.util.ArrayList;
import java.util.HashMap;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColoredCTM;
import com.prupe.mcpatcher.ctm.CTMUtils;
import com.prupe.mcpatcher.ctm.TileOverride;
import com.prupe.mcpatcher.mal.block.BlockStateMatcher;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

public class CTMForColoredBlocks {

    public static void init() {
        CTMUtils.addCtmRegistrationCallback(overrides -> {
            if (Loader.instance()
                .getLoaderState() == LoaderState.AVAILABLE) {
                BlockColored.initColoredBlocks(null);
            }

            ArrayList<PropertiesFileColored> toRegister = new ArrayList<>();
            overrides.getBlock()
                .forEach((block, blockStateMatchers) -> {
                    for (BlockStateMatcher stateMatcher : blockStateMatchers) {
                        for (BlockColored bc : BlockColoredCTM.CTM_COLORED_BLOCKS) {
                            if (stateMatcher.match(
                                bc.getBase()
                                    .getBlock(),
                                bc.getBase()
                                    .getMeta())) {
                                toRegister.add(
                                    PropertiesFileColored.from(((TileOverride) stateMatcher.getData()).properties, bc));
                            }
                        }
                    }
                });

            HashMap<Float, TileLoaderColored> tileLoaders = new HashMap<>();
            for (PropertiesFileColored propertiesFileColored : toRegister) {
                TileLoaderColored loader;
                float bm = propertiesFileColored.getBlockColored()
                    .getBrightnessMultiplier();
                if (tileLoaders.containsKey(bm)) {
                    loader = tileLoaders.get(bm);
                } else {
                    loader = new TileLoaderColored("textures/blocks", CTMUtils.logger, bm);
                    tileLoaders.put(bm, loader);
                }
                CTMUtils.registerOverrideWithoutLock(overrides, TileOverride.create(propertiesFileColored, loader));
            }
        });
    }
}
