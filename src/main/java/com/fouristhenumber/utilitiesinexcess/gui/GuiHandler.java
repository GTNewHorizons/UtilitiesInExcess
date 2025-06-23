package com.fouristhenumber.utilitiesinexcess.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySignificantlyShrunkChest;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static int GUI_SIGNIFICANTLY_SHRUNK_CHEST = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_SIGNIFICANTLY_SHRUNK_CHEST) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntitySignificantlyShrunkChest chest) {
                return new ContainerSignificantlyShrunkChest(player.inventory, chest);
            }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_SIGNIFICANTLY_SHRUNK_CHEST) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntitySignificantlyShrunkChest chest) {
                return new GuiSignificantlyShrunkChest(player.inventory, chest);
            }
        }

        return null;
    }
}
