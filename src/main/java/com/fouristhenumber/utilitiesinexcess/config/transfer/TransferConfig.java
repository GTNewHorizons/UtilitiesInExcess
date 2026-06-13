package com.fouristhenumber.utilitiesinexcess.config.transfer;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "transfer")
public class TransferConfig {

    @Config.Comment("Should the transfer system be enabled.")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean EnableTransferSystem;
}
