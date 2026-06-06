package com.fouristhenumber.utilitiesinexcess.common.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemFireBattery;
import com.fouristhenumber.utilitiesinexcess.config.items.FireBatteryConfig;
import com.gtnewhorizon.gtnhlib.util.ItemRenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FireBatteryRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(final ItemStack item, final ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(final ItemRenderType type, final ItemStack item,
        final ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY && helper == ItemRendererHelper.ENTITY_BOBBING
            || (helper == ItemRendererHelper.ENTITY_ROTATION && Minecraft.getMinecraft().gameSettings.fancyGraphics);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (stack == null) return;

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);

        NBTTagCompound tag = stack.getTagCompound();

        int energy = 0;
        if (tag != null) {
            energy = tag.getInteger("Energy");
        }
        float progress = energy / (float) FireBatteryConfig.fireBatteryRFStorage;

        ItemRenderUtil.applyStandardItemTransform(type);

        GL11.glColor3f(progress, 0F, .2F);
        ItemRenderUtil.renderItem(type, ItemFireBattery.background);
        GL11.glColor3f(1f, 1f, 1f);

        int overlayCount = ItemFireBattery.overlays.length;
        int index = MathHelper.clamp_int((int) (progress * overlayCount), 0, overlayCount - 1);
        ItemRenderUtil.renderItem(type, ItemFireBattery.overlays[index]);

        GL11.glPopAttrib();
    }
}
