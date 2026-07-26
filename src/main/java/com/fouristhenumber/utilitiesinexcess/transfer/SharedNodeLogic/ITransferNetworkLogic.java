package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface ITransferNetworkLogic
{
    void separateWorld(World world);

    void tryJoinWorld(World world);

    Packet getDescriptionPacket();
    void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt);

    void writeSyncData(NBTTagCompound tag);
    void readSyncData(NBTTagCompound tag);

    void updateExternalConnections();

    void addExternal(ForgeDirection direction, Connection neighbor);
    void removeExternal(ForgeDirection direction);

    void removeNeighbor(ForgeDirection direction);
    void addNeighbor(ForgeDirection direction, ITransferNetworkComponent neighbor);

    // I really don't like this method, but I have no idea what is the best way to fix the inheritance chain here.
    MaskedArrayView<ITransferNetworkComponent> getWalkableDirs(TransportType transPortType, ForgeDirection direction, IWalkingComponent<?> walkingComponent);

    int getNetworkMask();
    int getExternalMask();

    void setNetworkMask(int mask);
    void setExternalMask(int mask);

    boolean canConnectEnergy();
    boolean canConnectFluid();
    boolean canConnectItem();

    Connection[] getValidExternalConnections(ForgeDirection fromDirection, IWalkingComponent<?> walker);
    ITransferNetworkComponent[] getNetworkConnections();

    int getMaxInsertable();
}
