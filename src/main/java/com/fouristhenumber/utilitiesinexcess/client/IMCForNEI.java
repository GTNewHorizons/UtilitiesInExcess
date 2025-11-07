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

        sendInfoPage("<utilitiesinexcess:ethereal_glass>", "nei.infopage.uie.ethereal_glass.0");
        sendInfoPage("<utilitiesinexcess:ethereal_glass:2>", "nei.infopage.uie.ethereal_glass.2");
        sendInfoPage("<utilitiesinexcess:ethereal_glass:3>", "nei.infopage.uie.ethereal_glass.3");
        sendInfoPage("<utilitiesinexcess:ethereal_glass:5>", "nei.infopage.uie.ethereal_glass.5");

        sendInfoPage("<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>", "nei.infopage.uie.inversion_sigil.1");
        sendInfoPage("<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>", "nei.infopage.uie.inversion_sigil.2");
        sendInfoPage("<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>", "nei.infopage.uie.inversion_sigil.3");
        sendInfoPage("<utilitiesinexcess:inversion_sigil_inactive>,<utilitiesinexcess:inversion_sigil_active>", "nei.infopage.uie.inversion_sigil.4");

        sendInfoPage("<utilitiesinexcess:cursed_earth>", "nei.infopage.uie.cursed_earth.1");

        sendInfoPage("utilitiesinexcess:lapis_aetherius", "nei.infopage.uie.lapis_aetherius.1");

        sendInfoPage("<utilitiesinexcess:blackout_curtains>", "nei.infopage.uie.blackout_curtains.1");

        sendInfoPage("<utilitiesinexcess:underworld_portal>", "nei.infopage.uie.underworld_portal.1");
        sendInfoPage("<utilitiesinexcess:underworld_portal>", "nei.infopage.uie.underworld_portal.2");
        sendInfoPage("<utilitiesinexcess:underworld_portal>", "nei.infopage.uie.underworld_portal.3");
    }

    private static void sendInfoPage(String filter, String page) {
        NBTTagCompound NBT = new NBTTagCompound();
        NBT.setString("filter", filter);
        NBT.setString("page", page);
        FMLInterModComms.sendMessage("NotEnoughItems", "addItemInfo", NBT);
    }
}
