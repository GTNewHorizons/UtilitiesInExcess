package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

/**
 * Restores chunk-loading tickets for TEs after a world reload.
 *
 * Forge fires ticketsLoaded before any forced chunk is loaded, so the target TileEntity does not exist yet and reading
 * it here would recurse into a StackOverflow. Instead, each ticket's home chunk is force-loaded
 * and the TileEntity then reclaims it from validate() once its chunk loads.
 */
public class TEChunkLoadingCallback implements ForgeChunkManager.LoadingCallback {

    // Tickets awaiting reclaim by their TileEntity
    private static final Map<String, ForgeChunkManager.Ticket> pendingTickets = new HashMap<>();

    private static String key(int dimensionId, int x, int y, int z) {
        return dimensionId + ":" + x + ":" + y + ":" + z;
    }

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            NBTTagCompound tag = ticket.getModData();
            int x = tag.getInteger("teX");
            int y = tag.getInteger("teY");
            int z = tag.getInteger("teZ");

            // Park the ticket before forcing the chunk so the TE can claim it the moment its chunk loads
            pendingTickets.put(key(world.provider.dimensionId, x, y, z), ticket);
            // Force the home chunk so the TE loads and reclaims its ticket - Forge will drop a ticket that forces
            // nothing
            ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(x >> 4, z >> 4));
        }
    }

    /**
     * Hands a parked ticket back to its TileEntity, or returns null if none was restored for this position.
     */
    @Nullable
    public static ForgeChunkManager.Ticket claimTicket(int dimensionId, int x, int y, int z) {
        return pendingTickets.remove(key(dimensionId, x, y, z));
    }
}
