package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

import codechicken.lib.data.MCDataInput;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;

// This is basically the part factory.
public class Content implements MultiPartRegistry.IPartFactory2 {

    public static final String[] partNames = new String[] { FencePart.name, WallPart.name, SpherePart.name };
    public static final Map<String, Integer> partMap = new HashMap<>();

    public static final Set<String> sidedParts = new HashSet<>();

    public static final Map<String, String> legacyAliases = new HashMap<>();

    public void init() {
        for (int i = 0; i < partNames.length; i++) {
            partMap.put(partNames[i], i);
        }
        sidedParts.add(WallPart.name);
        sidedParts.add(FencePart.name);

        legacyAliases.put("extrautils:sphere", SpherePart.name);
        legacyAliases.put("extrautils:fence", FencePart.name);
        legacyAliases.put("extrautils:wall", WallPart.name);

        Set<String> namesToRegister = new HashSet<>();
        namesToRegister.addAll(Arrays.asList(partNames));
        namesToRegister.addAll(legacyAliases.keySet());

        MultiPartRegistry.registerParts(this, namesToRegister.toArray(new String[0]));
    }

    private String translateName(String name) {
        return legacyAliases.getOrDefault(name, name);
    }

    public UEMultipart createUEMultiPart(boolean isClient, int material, int side, String name) {
        switch (name) {
            case ("ue_fence"): {
                return new FencePart(material, side);
            }
            case ("ue_wall"): {
                return new WallPart(material, side);
            }
            case ("ue_sphere"): {
                return new SpherePart(material);
            }
        }
        return null;
    }

    // Called on the server
    @Override
    public TMultiPart createPart(String name, NBTTagCompound nbt) {
        String actualName = translateName(name);

        return createUEMultiPart(
            false,
            MicroMaterialRegistry.materialID(nbt.getString("material")),
            nbt.getInteger("side"),
            actualName);
    }

    // Called on the client
    @Override
    public TMultiPart createPart(String name, MCDataInput packet) {
        String actualName = translateName(name);

        if (sidedParts.contains(actualName)) {
            return createUEMultiPart(true, MicroMaterialRegistry.readMaterialID(packet), packet.readInt(), actualName);
        }
        return createUEMultiPart(true, MicroMaterialRegistry.readMaterialID(packet), 0, actualName);
    }
}
