package com.fouristhenumber.utilitiesinexcess.compat.endlessids;

import net.minecraft.world.World;

import com.falsepattern.endlessids.mixin.helpers.SubChunkBlockHook;

public class EIDsHelper {

    public static void setBlockID(World world, int x, int y, int z, int id) {
        // TODO cubic chunks compat
        ((SubChunkBlockHook) world.getChunkFromBlockCoords(x, z)
            .getBlockStorageArray()[y >> 4]).eid$setID(x & 15, y & 15, z & 15, id);
    }

    public static void setBlockMeta(World world, int x, int y, int z, int meta) {
        // TODO cubic chunks compat
        ((SubChunkBlockHook) world.getChunkFromBlockCoords(x, z)
            .getBlockStorageArray()[y >> 4]).eid$setMetadata(x & 15, y & 15, z & 15, meta);
    }
}
