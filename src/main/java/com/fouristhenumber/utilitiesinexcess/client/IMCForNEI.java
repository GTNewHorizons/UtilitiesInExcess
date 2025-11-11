package com.fouristhenumber.utilitiesinexcess.client;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;

public class IMCForNEI {

    public static void IMCSender() {
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.1");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.2");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.3");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.4");
        sendInfoPage("<utilitiesinexcess:temporal_gate>", "nei.infopage.uie.temporal_gate.5");
    }

    private static void sendInfoPage(String filter, String page) {
        NBTTagCompound NBT = new NBTTagCompound();
        NBT.setString("filter", filter);
        NBT.setString("page", page);
        FMLInterModComms.sendMessage("NotEnoughItems", "addItemInfo", NBT);
    }
}
