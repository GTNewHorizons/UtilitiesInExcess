package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.entity.monster.EntityZombie;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityZombie.class)
public interface AccessorEntityZombie {

    @Invoker(value = "convertToVillager")
    void convertToVillager();
}
