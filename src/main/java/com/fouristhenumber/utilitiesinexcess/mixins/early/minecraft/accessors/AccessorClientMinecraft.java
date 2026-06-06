package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface AccessorClientMinecraft {

    @Accessor("timer")
    Timer timer();
}
