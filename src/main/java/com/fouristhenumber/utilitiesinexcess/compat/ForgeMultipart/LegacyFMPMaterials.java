package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart;

import codechicken.microblock.MicroMaterialRegistry;

/** Only loaded on ForgeMultipart older than 2.0.0, which lacks {@code materialCount()}. */
final class LegacyFMPMaterials {

    private LegacyFMPMaterials() {}

    @SuppressWarnings("deprecation")
    static int materialCount() {
        return MicroMaterialRegistry.getIdMap().length;
    }
}
