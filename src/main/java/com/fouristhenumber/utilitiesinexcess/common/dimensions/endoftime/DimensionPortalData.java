package com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class DimensionPortalData extends WorldSavedData {

    public static final String DATA_NAME = "PortalTarget";
    private int x, y, z;
    static DimensionPortalData INSTANCE;

    public DimensionPortalData() {
        this(DATA_NAME);
    }

    public DimensionPortalData(String tagName) {
        super(tagName);
    }

    public static DimensionPortalData get(World world) {
        MapStorage storage = world.perWorldStorage;
        DimensionPortalData.INSTANCE = (DimensionPortalData) storage
            .loadData(DimensionPortalData.class, DimensionPortalData.DATA_NAME);
        if (INSTANCE == null) {
            INSTANCE = new DimensionPortalData(DATA_NAME);
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
