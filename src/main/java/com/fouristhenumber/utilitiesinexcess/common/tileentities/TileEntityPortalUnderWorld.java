package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPortalUnderWorld extends TileEntity {

    /// Only set for outside->underworld portals
    /// Underworld->outside uses [UnderWorldSourceProperty] instead of looking for a specific block.
    public int destX, destY, destZ;
    public boolean hasDest, invulnerable;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.destX = compound.getInteger("destX");
        this.destY = compound.getInteger("destY");
        this.destZ = compound.getInteger("destZ");
        this.hasDest = compound.getBoolean("hasDest");
        this.invulnerable = compound.getBoolean("invulnerable");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("destX", destX);
        compound.setInteger("destY", destY);
        compound.setInteger("destZ", destZ);
        compound.setBoolean("hasDest", hasDest);
        compound.setBoolean("invulnerable", invulnerable);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound data = new NBTTagCompound();

        data.setBoolean("invulnerable", invulnerable);

        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);

        invulnerable = pkt.func_148857_g()
            .getBoolean("invulnerable");
    }
}
