package com.fouristhenumber.utilitiesinexcess.common.worldgen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.config.blocks.EnderLotusConfig;

import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenEnderLotus implements IWorldGenerator {

    private static final int ATTEMPTS_PER_CHUNK = 4;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (!EnderLotusConfig.spawnEnderLotusesInEnd) return;
        if (world.provider.dimensionId == 1) {
            for (int i = 0; i < ATTEMPTS_PER_CHUNK; i++) {

                if (random.nextDouble() < 0.5) continue;

                int x = (chunkX * 16) + random.nextInt(16);
                int z = (chunkZ * 16) + random.nextInt(16);
                int y = 10 + random.nextInt(60);

                for (int dy = y; dy > 1; dy--) {
                    if (world.getBlock(x, dy, z) == Blocks.end_stone && world.isAirBlock(x, dy + 1, z)) {
                        world.setBlock(x, dy + 1, z, ModBlocks.ENDER_LOTUS.get(), 7, 2);
                        break;
                    }
                }
            }
        }
    }
}
