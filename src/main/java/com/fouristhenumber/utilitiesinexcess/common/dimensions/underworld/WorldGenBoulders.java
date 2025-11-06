package com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.joml.Vector3ic;

import com.fouristhenumber.utilitiesinexcess.common.worldgen.util.Box;

/// Generates large boulders
public class WorldGenBoulders extends WorldGenerator {

    private final List<Block> whitelist;
    private final Block block;

    public WorldGenBoulders(List<Block> whitelist, Block block) {
        this.whitelist = whitelist;
        this.block = block;
    }

    @Override
    public boolean generate(World world, Random rng, int x, int y, int z) {
        float r = rng.nextFloat() * 3 + 2;
        int ri = MathHelper.ceiling_float_int(r);
        float invR = 1f / r;

        Box box = new Box(x - ri, y - ri, z - ri, x + ri, y + ri, z + ri);

        // Check if the boulder can spawn here without clipping into something it shouldn't be
        for (Vector3ic v : box) {
            float dX = (x - v.x() + 0.5f) * invR;
            float dY = (y - v.y() + 0.5f) * invR;
            float dZ = (z - v.z() + 0.5f) * invR;

            float r2 = dX * dX + dY * dY + dZ * dZ;

            if (r2 > 1) continue;

            if (!whitelist.contains(world.getBlock(v.x(), v.y(), v.z()))) {
                return false;
            }
        }

        // Spawn the boulder if the location is valid
        for (Vector3ic v : box) {
            float dX = (x - v.x()) * invR;
            float dY = (y - v.y()) * invR;
            float dZ = (z - v.z()) * invR;

            float r2 = dX * dX + dY * dY + dZ * dZ;

            if (r2 + rng.nextFloat() * 0.2 > 1) continue;

            world.setBlock(v.x(), v.y(), v.z(), block);
        }

        return true;
    }
}
