package com.fouristhenumber.utilitiesinexcess.common.dimensions;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class UIEWorldChunkManager extends WorldChunkManager {

    private final BiomeGenBase biome;

    public UIEWorldChunkManager(BiomeGenBase biome) {
        this.biome = biome;
    }

    public BiomeGenBase getBiomeGenAt(int x, int z) {
        return this.biome;
    }

    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] array, int x, int z, int width, int depth) {
        if (array == null || array.length < width * depth) {
            array = new BiomeGenBase[width * depth];
        }

        Arrays.fill(array, 0, width * depth, this.biome);
        return array;
    }

    public float[] getRainfall(float[] array, int x, int z, int width, int depth) {
        if (array == null || array.length < width * depth) {
            array = new float[width * depth];
        }

        Arrays.fill(array, 0, width * depth, 0f);
        return array;
    }

    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] array, int x, int z, int width, int depth) {
        if (array == null || array.length < width * depth) {
            array = new BiomeGenBase[width * depth];
        }

        Arrays.fill(array, 0, width * depth, this.biome);
        return array;
    }

    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] array, int x, int z, int width, int depth, boolean cache) {
        return this.loadBlockGeneratorData(array, x, z, width, depth);
    }

    public ChunkPosition findBiomePosition(int x, int z, int range, List<BiomeGenBase> allowed, Random rng) {
        if (allowed.contains(this.biome)) {
            return new ChunkPosition(x - range + rng.nextInt(range * 2 + 1), 0, z - range + rng.nextInt(range * 2 + 1));
        } else {
            return null;
        }
    }

    public boolean areBiomesViable(int x, int z, int range, List<BiomeGenBase> allowed) {
        return allowed.contains(this.biome);
    }
}
