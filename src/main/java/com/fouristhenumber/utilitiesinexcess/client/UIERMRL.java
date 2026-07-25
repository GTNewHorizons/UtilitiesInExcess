package com.fouristhenumber.utilitiesinexcess.client;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.nei.NEIConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UIERMRL implements IResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(IResourceManager p_110549_1_) {
        if (Mods.NEI.isLoaded()) {
            NEIConfig.registerAliases();
        }
    }
}
