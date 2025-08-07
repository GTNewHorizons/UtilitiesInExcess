package com.fouristhenumber.utilitiesinexcess.mixins;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.mixin.IMixins;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class UIEMixinLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {

    static {
        // Register config here because then it's available during mixins.
        try {
            ItemConfig.registerConfig();
            BlockConfig.registerConfig();
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getMixinConfig() {
        return "mixins.utilitiesinexcess.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(Mixins.class, loadedCoreMods);
    }
}
