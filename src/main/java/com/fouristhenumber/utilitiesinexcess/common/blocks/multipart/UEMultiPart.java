package com.fouristhenumber.utilitiesinexcess.common.blocks.multipart;

import codechicken.lib.vec.Vector3;
import codechicken.multipart.IconHitEffects;
import codechicken.multipart.JIconHitEffects;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import java.util.Collections;

public abstract class UEMultiPart extends TMultiPart implements JIconHitEffects, JNormalOcclusion
{

    public abstract void render(Vector3 position, int pass);

    @Override
    public Iterable<ItemStack> getDrops()
    {
        return Collections.singletonList(UEMultiPartItem.createStack(this));
    }

    @Override
    public void addDestroyEffects(EffectRenderer renderer)
    {
        IconHitEffects.addDestroyEffects(this, renderer);
    }

    @Override
    public void addHitEffects(MovingObjectPosition movingObjectPosition, EffectRenderer renderer)
    {
        IconHitEffects.addHitEffects(this, movingObjectPosition, renderer);
    }

    @Override
    public boolean occlusionTest(TMultiPart part)
    {
        return NormalOcclusionTest.apply(this, part) && super.occlusionTest(part);
    }
}
