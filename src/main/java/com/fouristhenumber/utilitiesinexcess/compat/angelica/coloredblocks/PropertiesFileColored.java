package com.fouristhenumber.utilitiesinexcess.compat.angelica.coloredblocks;

import java.util.Properties;

import net.minecraft.util.ResourceLocation;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.prupe.mcpatcher.MCLogger;
import com.prupe.mcpatcher.mal.resource.PropertiesFile;
import com.prupe.mcpatcher.mal.resource.ResourceLocationWithSource;

public class PropertiesFileColored extends PropertiesFile {

    public PropertiesFileColored(MCLogger logger, ResourceLocation resource, Properties properties) {
        super(logger, resource, properties);
    }

    private BlockColored blockColored;

    static PropertiesFileColored from(PropertiesFile propertiesFile, BlockColored bc) {
        Properties properties = propertiesFile.getProperties();
        properties.setProperty("matchBlocks", UtilitiesInExcess.MODID + ":" + bc.getRegistryName());
        properties.setProperty("metadata", "");

        ResourceLocation resource = propertiesFile.getResource();
        String location = resource.getResourcePath();
        int i = location.lastIndexOf('/');
        location = location.substring(0, i) + "/___UIE_COLORED___" + location.substring(i);

        if (resource instanceof ResourceLocationWithSource old) {
            resource = new ResourceLocationWithSource(
                old.getSource(),
                new ResourceLocation(resource.getResourceDomain(), location));
        } else {
            resource = new ResourceLocation(resource.getResourceDomain(), location);
        }

        PropertiesFileColored pfc = new PropertiesFileColored(propertiesFile.getLogger(), resource, properties);
        pfc.setBlockColored(bc);
        return pfc;
    }

    public BlockColored getBlockColored() {
        return blockColored;
    }

    private void setBlockColored(BlockColored blockColored) {
        this.blockColored = blockColored;
    }
}
