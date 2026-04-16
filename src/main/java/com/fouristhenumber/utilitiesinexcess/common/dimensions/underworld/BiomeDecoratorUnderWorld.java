package com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld;

import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.COAL;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.DIAMOND;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.GOLD;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.IRON;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.LAPIS;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.REDSTONE;

import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeDecoratorUnderWorld extends BiomeDecorator {

    @Override
    protected void generateOres() {
        double difficulty = getChunkProvider().getDifficulty(chunk_X, chunk_Z);

        boolean isAggressiveArea = difficulty >= 2;

        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(currentWorld, randomGenerator, chunk_X, chunk_Z));

        if (TerrainGen.generateOre(currentWorld, randomGenerator, coalGen, chunk_X, chunk_Z, COAL)) {
            this.genStandardOre1(isAggressiveArea ? 60 : 20, this.coalGen, 0, 255);

            this.genStandardOre1(isAggressiveArea ? 60 : 20, this.coalGen, 0, 80);
        }

        if (TerrainGen.generateOre(currentWorld, randomGenerator, ironGen, chunk_X, chunk_Z, IRON)) {
            this.genStandardOre1(isAggressiveArea ? 60 : 20, this.ironGen, 0, 255);

            this.genStandardOre1(isAggressiveArea ? 60 : 20, this.ironGen, 0, 80);
        }

        if (TerrainGen.generateOre(currentWorld, randomGenerator, goldGen, chunk_X, chunk_Z, GOLD)) {
            this.genStandardOre1(isAggressiveArea ? 18 : 2, this.goldGen, 0, 32);
        }

        if (TerrainGen.generateOre(currentWorld, randomGenerator, redstoneGen, chunk_X, chunk_Z, REDSTONE)) {
            this.genStandardOre1(isAggressiveArea ? 32 : 8, this.redstoneGen, 0, 48);
        }

        if (TerrainGen.generateOre(currentWorld, randomGenerator, diamondGen, chunk_X, chunk_Z, DIAMOND)) {
            this.genStandardOre1(isAggressiveArea ? 9 : 1, this.diamondGen, 0, 32);
        }

        if (TerrainGen.generateOre(currentWorld, randomGenerator, lapisGen, chunk_X, chunk_Z, LAPIS)) {
            this.genStandardOre2(isAggressiveArea ? 9 : 1, this.lapisGen, 32, 64);
        }

        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(currentWorld, randomGenerator, chunk_X, chunk_Z));
    }

    private ChunkProviderUnderWorld getChunkProvider() {
        return (ChunkProviderUnderWorld) ((WorldServer) currentWorld).theChunkProviderServer.currentChunkProvider;
    }
}
