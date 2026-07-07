package com.fouristhenumber.utilitiesinexcess.compat.waila;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCollector;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockRainMuffler;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSmartPump;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockSpike;
import com.fouristhenumber.utilitiesinexcess.common.blocks.generators.BlockBaseGenerator;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

@SuppressWarnings("unused")
public class WailaCompat {

    // Passed to waila in CommonProxy
    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.registerNBTProvider((IWailaDataProvider) ModBlocks.COLLECTOR.get(), BlockCollector.class);
        registrar.registerBodyProvider((IWailaDataProvider) ModBlocks.COLLECTOR.get(), BlockCollector.class);

        registrar.registerNBTProvider((IWailaDataProvider) ModBlocks.SPIKE_WOOD.get(), BlockSpike.class);
        registrar.registerBodyProvider((IWailaDataProvider) ModBlocks.SPIKE_WOOD.get(), BlockSpike.class);

        registrar.registerNBTProvider((IWailaDataProvider) ModBlocks.SMART_PUMP.get(), BlockSmartPump.class);
        registrar.registerBodyProvider((IWailaDataProvider) ModBlocks.SMART_PUMP.get(), BlockSmartPump.class);

        registrar.registerNBTProvider(GeneratorDataProvider.INSTANCE, BlockBaseGenerator.class);
        registrar.registerBodyProvider(GeneratorDataProvider.INSTANCE, BlockBaseGenerator.class);

        registrar.registerBodyProvider(RainMufflerDataProvider.INSTANCE, BlockRainMuffler.class);

        registrar.registerBodyProvider(ColoredBlocksDataProvider.INSTANCE, BlockColored.class);
    }
}
