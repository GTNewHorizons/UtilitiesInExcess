package com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class PlatformAnchorData extends WorldSavedData {

    public static final String DATA_NAME = "PlatformAnchor";
    private int x, y, z;
    static PlatformAnchorData INSTANCE;

    public PlatformAnchorData() {
        this(DATA_NAME);
    }

    public PlatformAnchorData(String tagName) {
        super(tagName);
    }

    public static PlatformAnchorData get(World world) {
        MapStorage storage = world.perWorldStorage;
        PlatformAnchorData.INSTANCE = (PlatformAnchorData) storage
            .loadData(PlatformAnchorData.class, PlatformAnchorData.DATA_NAME);
        if (INSTANCE == null) {
            INSTANCE = new PlatformAnchorData(DATA_NAME);
            storage.setData(DATA_NAME, INSTANCE);
        }
        return INSTANCE;
    }

    public void setTarget(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        markDirty();
    }

    public ChunkCoordinates getTarget() {
        return new ChunkCoordinates(x, y, z);
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        x = nbt.getInteger("X");
        y = nbt.getInteger("Y");
        z = nbt.getInteger("Z");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("X", x);
        nbt.setInteger("Y", y);
        nbt.setInteger("Z", z);
    }
}
