package com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld;

import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Post;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Pre;
import net.minecraftforge.event.terraingen.TerrainGen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;

import com.cardinalstar.cubicchunks.CubicChunks;
import com.cardinalstar.cubicchunks.api.ICube;
import com.cardinalstar.cubicchunks.api.util.Box;
import com.cardinalstar.cubicchunks.api.worldgen.GenerationResult;
import com.cardinalstar.cubicchunks.api.worldgen.IWorldGenerator;
import com.cardinalstar.cubicchunks.mixin.api.ICubicWorldInternal;
import com.cardinalstar.cubicchunks.mixin.early.common.IGameRegistry;
import com.cardinalstar.cubicchunks.server.CubeProviderServer;
import com.cardinalstar.cubicchunks.server.chunkio.ICubeLoader;
import com.cardinalstar.cubicchunks.util.CompatHandler;
import com.cardinalstar.cubicchunks.world.api.ICubeProviderServer.Requirement;
import com.cardinalstar.cubicchunks.world.core.IColumnInternal;
import com.cardinalstar.cubicchunks.world.cube.Cube;
import com.cardinalstar.cubicchunks.world.cube.blockview.CubeStackBlockView;
import com.cardinalstar.cubicchunks.worldgen.WorldgenHangWatchdog;
import com.fouristhenumber.utilitiesinexcess.utils.IntRange;
import com.fouristhenumber.utilitiesinexcess.utils.noise.NoiseSampler;
import com.fouristhenumber.utilitiesinexcess.utils.noise.OctavesSampler;
import com.fouristhenumber.utilitiesinexcess.utils.noise.ScaledNoise;
import com.fouristhenumber.utilitiesinexcess.utils.noise.SimplexNoiseSampler;
import com.gtnewhorizon.gtnhlib.hash.Fnv1a64;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;
import com.gtnewhorizon.gtnhlib.util.data.ImmutableBlockMeta;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;

@ParametersAreNonnullByDefault
public class CubicChunkProviderUnderWorld implements IWorldGenerator {

    public static final IntRange FLOOR = new IntRange(50, 70), CEILING = new IntRange(110, 130);

    private static final WorldGenLakes WATER_LAKES = new WorldGenLakes(Blocks.water);
    private static final WorldGenLakes LAVA_LAKES = new WorldGenLakes(Blocks.lava);

    private final World world;
    private final long seed;
    private final Random rng = new Random(0);
    private final ChunkProviderUnderWorld provider;

    private final NoiseSampler floor, ceiling, feature, difficulty, mushrooms, boulders;

    private final MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private final WorldGenBigMushroom bigMushroomGen = new WorldGenBigMushroom(-1);
    private final WorldGenBoulders boulderGen = new WorldGenBoulders(
        Arrays.asList(Blocks.air, Blocks.stone, Blocks.cobblestone),
        Blocks.cobblestone);

    public CubicChunkProviderUnderWorld(World world, long seed) {
        this.world = world;
        this.seed = seed;
        provider = new ChunkProviderUnderWorld(world, seed);

        Random rng = new Random(seed);

        floor = new ScaledNoise(new OctavesSampler(rng, 4), 0.0075d, 0.0075d, 0.0075d);
        ceiling = new ScaledNoise(new OctavesSampler(rng, 4), 0.0075d, 0.0075d, 0.0075d);
        feature = new SimplexNoiseSampler(rng);

        difficulty = new ScaledNoise(new SimplexNoiseSampler(rng), 0.02, 0.02, 0.02, 2, 2);

        mushrooms = new ScaledNoise(new OctavesSampler(rng, 2), 0.1, 5, 0.1, 2, -1.4);
        boulders = new ScaledNoise(new OctavesSampler(rng, 2), 0.1, 5, 0.1, 0.1, 0.1);
    }

    @Override
    public GenerationResult<Cube> provideCube(@Nullable Chunk chunk, int cubeX, int cubeY, int cubeZ) {
        try {
            WorldgenHangWatchdog.startWorldGen();

            List<Chunk> generatedColumns = new ArrayList<>();
            List<Cube> generatedCubes = new ArrayList<>();

            if (chunk == null) {
                chunk = createChunk(world, cubeX, cubeZ);
                generatedColumns.add(chunk);
            }

            generateLayer(chunk, cubeY >> 4, cubeX, cubeZ, generatedCubes);

            Cube primary = null;

            for (int i = 0; i < generatedCubes.size(); i++) {
                Cube c = generatedCubes.get(i);

                if (c.getY() == cubeY) {
                    primary = c;
                    generatedCubes.remove(i);
                    break;
                }
            }

            return new GenerationResult<>(primary, generatedColumns, generatedCubes);
        } finally {
            WorldgenHangWatchdog.endWorldGen();
        }
    }

    @Override
    public GenerationResult<Chunk> provideColumn(World world, int columnX, int columnZ) {
        Chunk chunk = createChunk(world, columnX, columnZ);

        List<Cube> generatedCubes = new ArrayList<>();

        generateLayer(chunk, 0, columnX, columnZ, generatedCubes);

        return new GenerationResult<>(chunk, Collections.emptyList(), generatedCubes);
    }

    private @NotNull Chunk createChunk(World world, int columnX, int columnZ) {
        Chunk chunk = new Chunk(world, columnX, columnZ);

        // Populate the biomes from the world chunk manager
        BiomeGenBase[] biomes = world.getWorldChunkManager()
            .loadBlockGeneratorData(null, columnX * 16, columnZ * 16, 16, 16);

        byte[] biomeIDs = chunk.getBiomeArray();

        for (int k = 0; k < biomeIDs.length; ++k) {
            biomeIDs[k] = (byte) biomes[k].biomeID;
        }

        // Fix the height maps
        chunk.generateSkylightMap();
        chunk.resetRelightChecks();

        // Scan the chunk for mineshaft starts
        mineshaftGenerator.func_151539_a(null, world, columnX, columnZ, null);

        return chunk;
    }

    protected void generateLayer(Chunk chunk, int layer, int columnX, int columnZ, List<Cube> generatedCubes) {
        int cubeLayer = layer << 4;
        int blockLayer = cubeLayer << 4;

        int upperCubeLayer = (layer + 1) << 4;
        int upperBlockLayer = upperCubeLayer << 4;

        CubeStackBlockView view = new CubeStackBlockView(chunk, cubeLayer, cubeLayer + 15);

        view.subViewMutable(new Box(0, blockLayer, 0, 15, blockLayer + 255, 15)).fill(new BlockMeta(Blocks.stone, 0));

        int blockX = columnX << 4;
        int blockZ = columnZ << 4;

        ImmutableBlockMeta stone = new BlockMeta(Blocks.stone, 0);
        ImmutableBlockMeta cobble = new BlockMeta(Blocks.cobblestone, 0);
        ImmutableBlockMeta air = new BlockMeta(Blocks.air, 0);

        // Each chunk is generated by column.
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int x2 = blockX + x;
                int z2 = blockZ + z;

                int floorLevel = getFloorLevel(cubeLayer, x2, z2) + blockLayer;
                int ceilingLevel = getCeilingLevel(cubeLayer, x2, z2) + blockLayer;

                // Generate the basic terrain
                for (int y = floorLevel - 2; y < ceilingLevel + 3; y++) {
                    ImmutableBlockMeta bm = air;

                    if (y <= floorLevel || y >= ceilingLevel) {
                        bm = cobble;
                    }

                    view.setBlock(x, y, z, bm);
                }

                // Get the raw feature noise. This controls pillars and holes.
                // The domain is [-1, 1]
                double upperFeature = this.feature.sample(x2 * 0.025, upperBlockLayer, z2 * 0.025);

                // Get the raw feature noise. This controls pillars and holes.
                // The domain is [-1, 1]
                double ourFeature = this.feature.sample(x2 * 0.025, blockLayer, z2 * 0.025);

                // We only want to generate pillars at noise values 0.75 and up.
                // This value controls how much of the pillar is 'full' for this column.
                // In other words, 0.5 means the stone goes 25% up from the bottom and 25% down from the top.
                double pillar = linearCurve(ourFeature, 0.75, 0, 0.9, 1);

                boolean hasPillar = pillar > 0;

                // Generate pillar, if present
                if (hasPillar) {
                    double span = 1d / (ceilingLevel - floorLevel);

                    double bump = this.feature.sample(x2, blockLayer, z2) * 0.1 - 0.05;

                    // Replace the cobble shell from the terraingen with stone
                    for (int y = floorLevel - 3; y <= floorLevel; y++) {
                        view.setBlock(x, y, z, stone);
                    }

                    for (int y = ceilingLevel; y <= ceilingLevel + 3; y++) {
                        view.setBlock(x, y, z, stone);
                    }

                    // Generate the pillar stone
                    for (int y = floorLevel + 1; y < ceilingLevel; y++) {
                        int dist = Math.min(y - floorLevel - 1, ceilingLevel - y);

                        // This is magic... I don't know how it works and I didn't know how it worked when I made it
                        double scalar = MathHelper.sin((float) (span * (double) dist * 2 + bump));

                        if (scalar < pillar) {
                            view.setBlock(x, y, z, stone);
                        }
                    }

                    // Set the inner-most 2 stone blocks to cobble stone (bottom half)
                    for (int y = floorLevel + 1; y < ceilingLevel; y++) {
                        if (view.getBlock(x, y, z) != Blocks.stone) {
                            for (int y2 = y - 1; y2 > Math.max(floorLevel, y - 3); y2--) {
                                view.setBlock(x, y2, z, cobble);
                            }
                            break;
                        }
                    }

                    // Set the inner-most 2 stone blocks to cobble stone (top half)
                    for (int y = ceilingLevel - 1; y > floorLevel; y--) {
                        if (view.getBlock(x, y, z) != Blocks.stone) {
                            for (int y2 = y + 1; y2 < Math.min(ceilingLevel, y + 3); y2++) {
                                view.setBlock(x, y2, z, cobble);
                            }
                            break;
                        }
                    }
                }

                // Generate our void holes
                if (ourFeature <= -0.7) {
                    // Use an aggressive curve here to make the walls extremely steep, while maintaining an obvious
                    // slope.
                    double holeDepth = linearCurve(ourFeature, -0.75, 1, -0.7, 0);

                    int holeLevel = (int) ((floorLevel - blockLayer) * (1 - holeDepth));

                    for (int y = floorLevel - blockLayer; y >= holeLevel; y--) {
                        view.setBlock(x, y + blockLayer, z, air);
                    }
                }

                // Generate the continuation of the upper layer's void holes
                if (upperFeature <= -0.75) {
                    for (int y = ceilingLevel; y < upperBlockLayer; y++) {
                        view.setBlock(x, y, z, air);
                    }
                }
            }
        }

        generatedCubes.addAll(view.cubes.values());
    }

    @Override
    public void populate(Cube cube) {
        ICubeLoader loader = getCubeLoader();

        int cx = cube.getX();
        int cy = cube.getY();
        int cz = cube.getZ();

        int layer = cy >> 4;
        int cubeBottom = layer << 4;

        primeRNG(cx, cy, cz);

        try {
            WorldgenHangWatchdog.startWorldGen();

            Box generate = new Box(cx, cubeBottom, cz, cx + 1, cubeBottom + 15, cz + 1);
            Box populate = new Box(cx, cubeBottom, cz, cx, cubeBottom + 15, cz);

            // Generate all relevant cubes and store them in an array cache
            loader.cacheCubes(generate, Requirement.GENERATE);

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    ((IColumnInternal) loader.getColumn(cx + x, cz + z, Requirement.GENERATE))
                        .recalculateStagingHeightmap();
                }
            }

            Chunk centerChunk = loader.getColumn(cx, cz, Requirement.GENERATE);

            centerChunk.isTerrainPopulated = true;
            centerChunk.isModified = true;

            for (Vector3ic v : populate) {
                Cube center = loader.getCube(v.x(), v.y(), v.z(), Requirement.GENERATE);

                if (!center.isPopulated()) {
                    if (v.y() == 0) {
                        // If we're populating layer 0, do the normal mod population
                        populateChunk(loader, v.x(), v.z());
                    }

                    if (v.y() == cubeBottom) {
                        populateLayerChunk(layer, v.x(), v.z());
                    }

                    center.markPopulated(Cube.POP_ALL);
                    loader.onCubeGenerated(center);
                }
            }
        } catch (Throwable t) {
            CubicChunks.LOGGER.error("Could not run non-vanilla population for cube {},{},{}", cx, cy, cz, t);
        } finally {
            WorldgenHangWatchdog.endWorldGen();
            loader.uncacheCubes();
        }
    }

    private void primeRNG(int cx, int cy, int cz) {
        long seed = Fnv1a64.initialState();

        seed = Fnv1a64.hashStep(seed, this.seed);
        seed = Fnv1a64.hashStep(seed, cx);
        seed = Fnv1a64.hashStep(seed, cy);
        seed = Fnv1a64.hashStep(seed, cz);

        rng.setSeed(seed);
    }

    private void populateChunk(ICubeLoader loader, int columnX, int columnZ) {
        Chunk column = loader.getColumn(columnX, columnZ, Requirement.GENERATE);

        column.isTerrainPopulated = true;
        column.isModified = true;

        try {
            ((ICubicWorldInternal.Server) world).fakeWorldHeight(256);

            primeRNG(columnX, 0, columnZ);
            MinecraftForge.EVENT_BUS.post(new Pre(provider, world, rng, columnX, columnZ, false));

            applyModGenerators(columnX, columnZ);

            primeRNG(columnX, 0, columnZ);
            MinecraftForge.EVENT_BUS.post(new Post(provider, world, rng, columnX, columnZ, false));
        } catch (Throwable t) {
            CubicChunks.LOGGER.error("Could not populate column {},{}", columnX, columnZ, t);
        } finally {
            ((ICubicWorldInternal.Server) world).fakeWorldHeight(0);
        }
    }

    // First provider is the ChunkProviderGenerate/Hell/End/Flat second is the serverChunkProvider
    private void applyModGenerators(int x, int z) {
        List<cpw.mods.fml.common.IWorldGenerator> generators = IGameRegistry.getSortedGeneratorList();

        if (generators == null) {
            IGameRegistry.computeGenerators();
            generators = IGameRegistry.getSortedGeneratorList();
            assert generators != null;
        }

        for (cpw.mods.fml.common.IWorldGenerator generator : generators) {
            primeRNG(x, 0, z);

            try {
                CompatHandler.beforeGenerate(world, generator);
                generator.generate(rng, x, z, world, provider, world.getChunkProvider());
            } finally {
                CompatHandler.afterGenerate(world);
            }
        }
    }

    private void populateLayerChunk(int layer, int columnX, int columnZ) {
        int cubeLayer = layer << 4;
        int blockLayer = cubeLayer << 4;

        int blockX = columnX << 4;
        int blockZ = columnZ << 4;

        primeRNG(columnX, cubeLayer, columnZ);

        // Generate water lakes, if possible
        if (this.rng.nextInt(32) == 0 && TerrainGen.populate(provider, world, rng, columnX, columnZ, false, LAKE)) {
            int x = blockX + this.rng.nextInt(16) + 8;
            int z = blockZ + this.rng.nextInt(16) + 8;

            if (isValidFeatureSpot(blockLayer, x, z)) {
                WATER_LAKES.generate(this.world, this.rng, x, getFloorLevel(cubeLayer, x, z) + blockLayer, z);
            }
        }

        // Generate lava lakes, if possible. More common than water lakes.
        if (this.rng.nextInt(16) == 0 && TerrainGen.populate(provider, world, rng, columnX, columnZ, false, LAVA)) {
            int x = blockX + this.rng.nextInt(16) + 8;
            int z = blockZ + this.rng.nextInt(16) + 8;

            if (isValidFeatureSpot(blockLayer, x, z)) {
                LAVA_LAKES.generate(this.world, this.rng, x, getFloorLevel(cubeLayer, x, z) + blockLayer, z);
            }
        }

        // Try to place a boulder in this chunk, if needed
        if (rng.nextDouble() <= getBoulder(cubeLayer, columnX, columnZ)) {
            for (int tries = 0; tries < 4; tries++) {
                int x = blockX + this.rng.nextInt(16) + 8;
                int z = blockZ + this.rng.nextInt(16) + 8;

                int y = getFloorLevel(cubeLayer, x, z) + 1 + blockLayer;

                if (isValidFeatureSpot(blockLayer, x, z)) {
                    if (boulderGen.generate(world, rng, x, y, z)) break;
                }
            }
        }

        int mushrooms = getMushroom(cubeLayer, columnX, columnZ);

        // Try to place mushrooms in this chunk
        for (int placed = 0, tries = 0; placed <= mushrooms && tries < 10; tries++) {
            int x = blockX + this.rng.nextInt(16) + 8;
            int z = blockZ + this.rng.nextInt(16) + 8;

            if (!isValidFeatureSpot(blockLayer, x, z)) {
                continue;
            }

            int y = getFloorLevel(cubeLayer, x, z) + 1 + blockLayer;

            // If the terrain was excavated by a lake/etc, don't spawn a mushroom here
            if (!validMyceliumSpawnLocation(x, y - 1, z)) {
                continue;
            }

            // bigMushroomGen needs something that mushrooms can spawn on to work, place some temporary mycelium
            world.setBlock(x, y - 1, z, Blocks.mycelium);

            // Fallibly place the mushroom
            if (!this.bigMushroomGen.generate(this.world, this.rng, x, y, z)) {
                // If it failed, revert the cobble to mycelium
                world.setBlock(x, y - 1, z, Blocks.cobblestone);
                continue;
            }

            // Generate some extra mycelium around the base to make it look like a growth on the floor
            spawnMycelium(x, y - 1, z, 13);
            placed++;
        }
    }

    /// Checks if the column is far enough from pillars and holes for populated features to spawn
    private boolean isValidFeatureSpot(int blockLayer, int x, int z) {
        double feature = this.feature.sample(x * 0.025, blockLayer, z * 0.025);
        return feature >= -0.5 && feature <= 0.5;
    }

    @Override
    public void recreateStructures(ICube cube) {
        recreateStructures((Chunk) cube.getColumn());
    }

    @Override
    public void recreateStructures(Chunk column) {

    }

    /// Checks what creatures can spawn on the given block
    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
        BiomeGenBase biome = this.world.getBiomeGenForCoords(x, z);
        return biome.getSpawnableList(creatureType);
    }

    @Override
    public @Nullable ChunkPosition getNearestStructure(String name, int x, int y, int z) {
        return null;
    }

    /// Gets the ceiling block level for the given column (returned value is the lowest non-air terrain block)
    private int getCeilingLevel(int cubeLayer, int x, int z) {
        return CEILING.lerp(this.ceiling.sample(x, cubeLayer, z));
    }

    /// Gets the floor block level for the given column (returned value is the highest non-air terrain block)
    private int getFloorLevel(int cubeLayer, int x, int z) {
        return FLOOR.lerp(this.floor.sample(x, cubeLayer, z));
    }

    private ICubeLoader getCubeLoader() {
        return getCubeProviderServer().getCubeLoader();
    }

    private CubeProviderServer getCubeProviderServer() {
        return ((ICubicWorldInternal.Server) world).getCubeCache();
    }

    /// Gets the chance for big mushrooms in the given chunk. Domain: [0, 2]
    private int getMushroom(int cubeLayer, int chunkX, int chunkZ) {
        return (int) Math.min(2, mushrooms.sample(chunkX, cubeLayer, chunkZ));
    }

    /// Gets the chance for boulders in the given chunk. Domain: [0, 1]
    private double getBoulder(int cubeLayer, int chunkX, int chunkZ) {
        return boulders.sample(chunkX, cubeLayer, chunkZ);
    }

    private static final ForgeDirection[] HORIZONTAL = { ForgeDirection.NORTH, ForgeDirection.SOUTH,
        ForgeDirection.WEST, ForgeDirection.EAST, };

    /// Checks to make sure the location is a valid mycelium spot. Has a special case for the initial block (mushroom
    /// block on top of mycelium)
    private boolean validMyceliumSpawnLocation(int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        Block above = world.getBlock(x, y + 1, z);

        return (block == Blocks.cobblestone || block == Blocks.mycelium)
            && (above.isAir(world, x, y + 1, z) || above == Blocks.brown_mushroom_block
            || above == Blocks.red_mushroom_block);
    }

    /// Spawns mycelium blocks in place of cobble stone randomly, starting from the given coordinate.
    private void spawnMycelium(int x, int y, int z, int amount) {
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();

        queue.enqueue(CoordinatePacker.pack(x, y, z));

        while (!queue.isEmpty() && amount > 0) {
            long curr = queue.dequeueLong();

            int x2 = CoordinatePacker.unpackX(curr);
            int y2 = CoordinatePacker.unpackY(curr);
            int z2 = CoordinatePacker.unpackZ(curr);

            boolean valid = validMyceliumSpawnLocation(x2, y2, z2);

            if (!valid && validMyceliumSpawnLocation(x2, y2 + 1, z2)) {
                y2++;
                valid = true;
            }

            if (!valid && validMyceliumSpawnLocation(x2, y2 - 1, z2)) {
                y2--;
                valid = true;
            }

            if (!valid) continue;

            world.setBlock(x2, y2, z2, Blocks.mycelium);
            amount--;

            int i = rng.nextInt(4);

            ForgeDirection dir = HORIZONTAL[i];
            queue.enqueue(CoordinatePacker.pack(x2 + dir.offsetX, y2 + dir.offsetY, z2 + dir.offsetZ));

            i += rng.nextInt(4);

            dir = HORIZONTAL[i % 4];
            queue.enqueue(CoordinatePacker.pack(x2 + dir.offsetX, y2 + dir.offsetY, z2 + dir.offsetZ));

            i += rng.nextInt(4);

            dir = HORIZONTAL[i % 4];
            queue.enqueue(CoordinatePacker.pack(x2 + dir.offsetX, y2 + dir.offsetY, z2 + dir.offsetZ));
        }
    }

    public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static double clamp(double val, double lo, double hi) {
        return Math.min(hi, Math.max(val, lo));
    }

    public static double linearCurve(double x, double x1, double y1, double x2, double y2) {
        x = clamp(x, Math.min(x1, x2), Math.max(x1, x2));

        return map(x, x1, x2, y1, y2);
    }
}
