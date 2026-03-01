package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import java.util.Collections;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import codechicken.lib.vec.Vector3;
import codechicken.multipart.IconHitEffects;
import codechicken.multipart.JIconHitEffects;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;

public abstract class UEMultipart extends TMultiPart implements JIconHitEffects, JNormalOcclusion {

    public abstract void render(Vector3 position, int pass);

    @Override
    public Iterable<ItemStack> getDrops() {
        return Collections.singletonList(UEMultipartItem.createStack(this));
    }

    @Override
    public void addDestroyEffects(EffectRenderer renderer) {
        IconHitEffects.addDestroyEffects(this, renderer);
    }

    @Override
    public void addHitEffects(MovingObjectPosition movingObjectPosition, EffectRenderer renderer) {
        IconHitEffects.addHitEffects(this, movingObjectPosition, renderer);
    }

    @Override
    public boolean occlusionTest(TMultiPart part) {
        return NormalOcclusionTest.apply(this, part) && super.occlusionTest(part);
    }
}
