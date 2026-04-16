package com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime;

import net.minecraft.world.biome.BiomeGenBase;

import com.fouristhenumber.utilitiesinexcess.config.dimensions.EndOfTimeConfig;

public class BiomeGenEndOfTime extends BiomeGenBase {

    public BiomeGenEndOfTime(int id) {
        super(id);

        if (!EndOfTimeConfig.endOfTimeSpawning) {
            this.spawnableMonsterList.clear();
            this.spawnableCreatureList.clear();
            this.spawnableWaterCreatureList.clear();
            this.spawnableCaveCreatureList.clear();
        }

        this.enableRain = EndOfTimeConfig.endOfTimeRain;

        this.setBiomeName("End of Time");
    }
}
