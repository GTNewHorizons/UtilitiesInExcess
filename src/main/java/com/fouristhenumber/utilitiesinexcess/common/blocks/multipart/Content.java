package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.data.MCDataInput;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


// This is basically the part factory.
public class Content implements MultiPartRegistry.IPartFactory2
{
    public static final String[] partNames = new String[]{ FencePart.name, WallPart.name, SpherePart.name };
    public static final Map<String, Integer> partMap = new HashMap<>();

    public static final Set<String> sidedParts = new HashSet<>();

    public void init() {
        for (int i = 0; i < partNames.length; i++)
        {
            partMap.put(partNames[i], i);
        }
        sidedParts.add(WallPart.name);
        sidedParts.add(FencePart.name);
        MultiPartRegistry.registerParts(this, partNames);
    }

    public UEMultiPart createUEMultiPart(boolean isClient, int material, int side, String name)
    {
        switch(name)
        {
            case ("ue_fence"):
            {
                return new FencePart(material, side);
            }
            case ("ue_wall"):
            {
                return new WallPart(material, side);
            }
            case ("ue_sphere"):
            {
                return new SpherePart(material);
            }
        }
        return null;
    }

    // Called on the server
    @Override
    public TMultiPart createPart(String name, NBTTagCompound nbt) {
        return createUEMultiPart(false,
            MicroMaterialRegistry.materialID(nbt.getString("material")),
            nbt.getInteger("side"),
            name);
    }

    // Called on the client
    @Override
    public TMultiPart createPart(String name, MCDataInput packet) {
        if (sidedParts.contains(name))
        {
            return createUEMultiPart(true,
                MicroMaterialRegistry.readMaterialID(packet),
                packet.readInt(),
                name);
        }
        return createUEMultiPart(true,
            MicroMaterialRegistry.readMaterialID(packet),
            0,
            name);
    }
}
