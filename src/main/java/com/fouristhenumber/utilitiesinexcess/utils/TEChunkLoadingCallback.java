package com.fouristhenumber.utilitiesinexcess.utils;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.utils.LoadableTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.List;

public class TEChunkLoadingCallback implements ForgeChunkManager.LoadingCallback {
    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            NBTTagCompound tag = ticket.getModData();
            int x = tag.getInteger("teX");
            int y = tag.getInteger("teY");
            int z = tag.getInteger("teZ");

            if (world.getTileEntity(x, y, z) instanceof LoadableTE te) {
                te.receiveTicketOnLoad(ticket);
            }
        }
    }
}
