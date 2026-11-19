package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart;

import codechicken.microblock.MicroMaterialRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.VersionParser;

/**
 * Material enumeration that works on ForgeMultipart before and after 2.0.0. The version check runs once, on first use.
 */
public final class FMPMaterials {

    private static final boolean HAS_JAVA_API = detectJavaApi();

    private FMPMaterials() {}

    public static int materialCount() {
        return HAS_JAVA_API ? MicroMaterialRegistry.materialCount() : LegacyFMPMaterials.materialCount();
    }

    private static boolean detectJavaApi() {
        ModContainer fmp = Loader.instance()
            .getIndexedModList()
            .get("ForgeMicroblock");
        return fmp != null && VersionParser.parseRange("[2.0.0,)")
            .containsVersion(fmp.getProcessedVersion());
    }
}
