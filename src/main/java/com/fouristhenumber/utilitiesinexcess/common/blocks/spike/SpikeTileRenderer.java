package com.fouristhenumber.utilitiesinexcess.common.blocks.spike;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class SpikeTileRenderer extends TileEntitySpecialRenderer {

    ModelSpike model = new ModelSpike();

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

        GL11.glPushMatrix();

        if (!(tileentity instanceof TileEntitySpike spikeTile)) return;

        switch (spikeTile.getSpikeType()) {
            case WOOD -> this.bindTexture(new ResourceLocation("utilitiesinexcess:textures/models/woodSpike.png"));
            case IRON -> this.bindTexture(new ResourceLocation("utilitiesinexcess:textures/models/ironSpike.png"));
            case GOLD -> this.bindTexture(new ResourceLocation("utilitiesinexcess:textures/models/goldSpike.png"));
            case DIAMOND -> this
                .bindTexture(new ResourceLocation("utilitiesinexcess:textures/models/diamondSpike.png"));
        }

        GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
        GL11.glRotatef(180F, 0F, 0F, 1F);
        GL11.glScalef(1.0F, 1.0F, 1.0F);

        model.render(null, 0, 0, 0, 0, 0, 0.0625F);

        GL11.glPopMatrix();
    }
}
