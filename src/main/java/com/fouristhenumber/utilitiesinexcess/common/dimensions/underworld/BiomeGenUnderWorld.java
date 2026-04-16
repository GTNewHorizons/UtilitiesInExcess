package com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenUnderWorld extends BiomeGenBase {

    public BiomeGenUnderWorld(int id) {
        super(id);

        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();

        this.topBlock = Blocks.cobblestone;
        this.fillerBlock = Blocks.stone;

        this.theBiomeDecorator = new BiomeDecoratorUnderWorld();

        this.setBiomeName("Underworld");
    }
}
