package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.data.MCDataInput;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;


// This is basically the part factory.
public class Content implements MultiPartRegistry.IPartFactory2
{
    public static final String[] partNames = new String[]{ FencePart.name };
    public static final Map<String, Integer> partMap = new HashMap<>();

    public void init() {
        for (int i = 0; i < partNames.length; i++)
        {
            partMap.put(partNames[i], i);
        }
        MultiPartRegistry.registerParts(this, partNames);
    }

    public UEMultiPart createUEMultiPart(boolean isClient, int material, String name)
    {
        switch(name)
        {
            case ("ue_fence"):
            {
                return new FencePart(material);
            }
        }
        return null;
    }

    // Called on the server
    @Override
    public TMultiPart createPart(String name, NBTTagCompound nbt) {
        return createUEMultiPart(false,
            MicroMaterialRegistry.materialID(nbt.getString("material")),
            name);
    }

    // Called on the client
    @Override
    public TMultiPart createPart(String name, MCDataInput packet) {
        return createUEMultiPart(true, MicroMaterialRegistry.readMaterialID(packet), name);
    }
}
