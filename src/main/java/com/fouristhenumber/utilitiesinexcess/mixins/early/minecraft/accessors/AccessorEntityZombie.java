package com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors;

import net.minecraft.entity.monster.EntityZombie;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityZombie.class)
public interface AccessorEntityZombie {

    // public net.minecraft.entity.monster.EntityZombie func_82232_p()V
    @Invoker(value = "convertToVillager")
    public void convertToVillager_uie();
}
