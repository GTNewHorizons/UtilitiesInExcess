package com.fouristhenumber.utilitiesinexcess.common.tileentities.utils;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nullable;

/**
 * Base class for TileEntities that need to keep chunks loaded.
 * Handles chunk loading tickets and manages forced chunk loading through Forge's ChunkManager.
 */
public class LoadableTE extends TileEntity {
    /** The chunk loading ticket used to force chunks to stay loaded */
    @Nullable
    ForgeChunkManager.Ticket ticket = null;
    private final int selfChunkX = xCoord >> 4;
    private final int selfChunkZ = zCoord >> 4;
    protected boolean selfIsLoaded = false;

    public LoadableTE() {
        // TODO: Check why loads 0, 0 sometimes
        if (keepsItselfLoaded()) {
            loadSelf();
        }
    }

    /**
     * Forces a chunk to stay loaded.
     * Skips loading if attempting to load own chunk and keepsItselfLoaded() returns true.
     *
     * @param chunkCoord The chunk coordinates to load
     */
    public void loadChunk(ChunkCoordIntPair chunkCoord) {
        if (chunkCoord.chunkXPos == selfChunkX && chunkCoord.chunkZPos == selfChunkZ && keepsItselfLoaded()) return;
        if (requestTicket()) {
            ForgeChunkManager.forceChunk(ticket, chunkCoord);
        } else {
            UtilitiesInExcess.LOG.error("Failed to get ticket to load chunk at {} {} for Mod {}", chunkCoord.chunkXPos, chunkCoord.chunkZPos, UtilitiesInExcess.MODID);
        }
    }

    /**
     * Forces a chunk to stay loaded using chunk coordinates.
     *
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     */
    public void loadChunkShifted(int x, int z) {
        loadChunk(new ChunkCoordIntPair(x, z));
    }

    /**
     * Forces a chunk to stay loaded using block coordinates.
     *
     * @param x Block X coordinate
     * @param z Block Z coordinate
     */
    public void loadChunk(int x, int z) {
         loadChunk(new ChunkCoordIntPair(x >> 4, z >> 4));
    }

    /**
     * Stops forcing a chunk to stay loaded.
     * Skips unloading if attempting to unload own chunk and keepsItselfLoaded() returns true.
     *
     * @param chunkCoord The chunk coordinates to unload
     */
    void unloadChunk(ChunkCoordIntPair chunkCoord) {
        if (chunkCoord.chunkXPos == selfChunkX && chunkCoord.chunkZPos == selfChunkZ && keepsItselfLoaded()) return;
        if (requestTicket()) {
            ForgeChunkManager.unforceChunk(ticket, chunkCoord);
        } else {
            UtilitiesInExcess.LOG.error("Failed to get ticket to unload? chunk at {} {} for Mod {}", chunkCoord.chunkXPos, chunkCoord.chunkZPos, UtilitiesInExcess.MODID);
        }
    }

    /**
     * Stops forcing a chunk to stay loaded using chunk coordinates.
     *
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     */
    public void unloadChunkShifted(int x, int z) {
        unloadChunk(new ChunkCoordIntPair(x, z));
    }

    /**
     * Stops forcing a chunk to stay loaded using block coordinates.
     *
     * @param x Block X coordinate
     * @param z Block Z coordinate
     */
    public void unloadChunk(int x, int z) {
        unloadChunk(new ChunkCoordIntPair(x >> 4, z >> 4));
    }

    /**
     * Forces the chunk containing this TileEntity to stay loaded.
     */
    public void loadSelf() {
        if (!selfIsLoaded) {
            loadChunk(selfChunkX, selfChunkZ);
            selfIsLoaded = true;
        }
    }

    /**
     * Stops forcing the chunk containing this TileEntity to stay loaded.
     */
    public void unloadSelf() {
        if (selfIsLoaded) {
            unloadChunk(selfChunkX, selfChunkZ);
            selfIsLoaded = false;
        }
    }

    /**
     * Releases the chunk loading ticket and cleans up all forced chunks.
     * Called when the TileEntity is invalidated or the chunk is unloaded.
     */
    public void invalidateTicket() {
        if (ticket != null) {
            ForgeChunkManager.releaseTicket(ticket);
            ticket = null;
        }
    }

    /**
     * Requests a chunk loading ticket from Forge's ChunkManager if one doesn't exist.
     *
     * @return true if a ticket is available (either existing or newly acquired), false otherwise
     */
    private boolean requestTicket() {
        if (ticket != null) return true;
        ticket = ForgeChunkManager.requestTicket(UtilitiesInExcess.uieInstance, worldObj, ForgeChunkManager.Type.NORMAL);

        if (ticket != null) {
            // Store TileEntity coordinates in ticket data for reloading
            NBTTagCompound tag = ticket.getModData();
            tag.setInteger("teX", xCoord);
            tag.setInteger("teY", yCoord);
            tag.setInteger("teZ", zCoord);
        }
        return ticket != null;
    }

    /**
     * Called when the world reloads to restore the chunk loading ticket.
     * If keepsItselfLoaded() returns true, automatically reloads the chunk containing this TileEntity.
     *
     * @param ticket The restored chunk loading ticket
     */
    public void receiveTicketOnLoad(ForgeChunkManager.Ticket ticket) {
        this.ticket = ticket;
        if (keepsItselfLoaded()) {
            loadSelf();
        }
    }

    /**
     * Override this method to determine if this TileEntity should keep its own chunk loaded.
     *
     * @return true if this TileEntity should keep its own chunk loaded, false otherwise
     */
    public boolean keepsItselfLoaded() {
        return false;
    }

    /**
     * Called when the chunk containing this TileEntity is unloaded.
     * Releases the chunk loading ticket to prevent memory leaks.
     */
    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        invalidateTicket();
    }

    /**
     * Called when this TileEntity is removed from the world.
     * Releases the chunk loading ticket to prevent memory leaks.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        invalidateTicket();
    }
}
