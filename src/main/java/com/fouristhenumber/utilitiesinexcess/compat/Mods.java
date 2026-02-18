package com.fouristhenumber.utilitiesinexcess.compat;

import java.util.function.Supplier;

import cpw.mods.fml.common.Loader;

public enum Mods {

    // spotless:off
    Baubles("Baubles"),
    Dreamcraft("dreamcraft"),
    Thaumcraft("Thaumcraft"),
    NEI("NotEnoughItems"),
    CraftTweaker("MineTweaker3"),
    FindIt("findit"),
    Tinkers("TConstruct"),
    Backhand("backhand"),
    GT("gregtech_nh")
    ;
    // spotless:on

    public final String modid;
    private final Supplier<Boolean> supplier;
    private Boolean loaded;

    Mods(String modid) {
        this.modid = modid;
        this.supplier = null;
    }

    Mods(Supplier<Boolean> supplier) {
        this.supplier = supplier;
        this.modid = null;
    }

    public boolean isLoaded() {
        if (loaded == null) {
            if (supplier != null) {
                loaded = supplier.get();
            } else if (modid != null) {
                loaded = Loader.isModLoaded(modid);
            } else loaded = false;
        }
        return loaded;
    }
}
