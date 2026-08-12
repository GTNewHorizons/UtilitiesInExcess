package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import java.util.Collections;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import codechicken.lib.vec.Vector3;
import codechicken.multipart.IconHitEffects;
import codechicken.multipart.JIconHitEffects;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;
import net.minecraft.world.IBlockAccess;

public abstract class UEMultipart extends TMultiPart implements JIconHitEffects, JNormalOcclusion {

    private static final ThreadLocal<RenderBlocks> UE_RENDER_BLOCKS =
        ThreadLocal.withInitial(() -> new RenderBlocks());

    protected static RenderBlocks getRenderBlocks(IBlockAccess world) {
        RenderBlocks renderer = UE_RENDER_BLOCKS.get();

        if (renderer.blockAccess != world) {
            renderer.blockAccess = world;
        }

        return renderer;
    }


    public abstract void render(Vector3 position, int pass);

    @Override
    public Iterable<ItemStack> getDrops() {
        return Collections.singletonList(UiEMultipartMaterialItem.createStack(this));
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
