package com.fouristhenumber.utilitiesinexcess.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockFilingCabinet;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.TileEntityFilingCabinet;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.TileEntityFilingCabinet.StoredContents;
import com.gtnewhorizon.gtnhlib.client.event.RenderTooltipEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class CabinetTooltipHandler {

    public static final CabinetTooltipHandler INSTANCE = new CabinetTooltipHandler();

    private static final RenderItem ITEM_RENDER = new RenderItem();

    private static final int MAX_ROWS = 9;
    private static final int ICON_SIZE = 16;
    private static final int ICON_GAP = 2;
    private static final int TEXT_PITCH = 10;
    private static final int ICON_PITCH = ICON_SIZE + 2;
    private static final float ZLEVEL = 300.0F;

    private CabinetTooltipHandler() {}

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (!(Block.getBlockFromItem(stack.getItem()) instanceof BlockFilingCabinet)) {
            return;
        }

        StoredContents contents = TileEntityFilingCabinet.readStoredContents(stack);
        if (contents.isEmpty()) {
            return;
        }

        event.alternativeRenderer = textLines -> render(event, textLines, contents);
    }

    private static final class Row {

        final ItemStack icon;
        final String text;

        Row(ItemStack icon, String text) {
            this.icon = icon;
            this.text = text;
        }

        int pitch() {
            return icon != null ? ICON_PITCH : TEXT_PITCH;
        }

        int width(FontRenderer font) {
            return font.getStringWidth(text) + (icon != null ? ICON_SIZE + ICON_GAP : 0);
        }
    }

    private void render(RenderTooltipEvent event, List<String> textLines, StoredContents contents) {
        FontRenderer font = event.font;

        List<Row> rows = new ArrayList<>();
        for (String line : textLines) {
            rows.add(new Row(null, line));
        }

        if (!contents.stacks.isEmpty()) {
            rows.add(new Row(null, EnumChatFormatting.GRAY + StatCollector.translateToLocal("tile.filing_cabinet.tooltip.contents")));
            int shown = Math.min(contents.stacks.size(), MAX_ROWS);
            for (int i = 0; i < shown; i++) {
                ItemStack s = contents.stacks.get(i);
                String text = String.format("%,d", s.stackSize) + " " + s.getDisplayName();
                rows.add(new Row(s, text));
            }
            int remaining = contents.stacks.size() - shown;
            if (remaining > 0) {
                rows.add(
                    new Row(
                        null,
                        EnumChatFormatting.GRAY
                            + StatCollector.translateToLocalFormatted("tile.filing_cabinet.tooltip.more", remaining)));
            }
            rows.add(
                new Row(
                    null,
                    EnumChatFormatting.GRAY + StatCollector
                        .translateToLocalFormatted("tile.filing_cabinet.tooltip.total", contents.totalItems)));
        }
        if (contents.upgrades > 0) {
            rows.add(
                new Row(
                    null,
                    EnumChatFormatting.GRAY + StatCollector
                        .translateToLocalFormatted("tile.filing_cabinet.tooltip.upgrades", contents.upgrades)));
        }

        int width = 0;
        for (Row r : rows) {
            width = Math.max(width, r.width(font));
        }
        int height = 8;
        if (rows.size() > 1) {
            height += 2;
            for (int i = 1; i < rows.size(); i++) {
                height += rows.get(i)
                    .pitch();
            }
        }

        int x = event.x + 12;
        int y = event.y - 12;
        if (x + width > event.gui.width) {
            x -= 28 + width;
        }
        if (y + height + 6 > event.gui.height) {
            y = event.gui.height - height - 6;
        }

        drawBackground(event, x, y, width, height);

        int cy = y;
        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            if (r.icon != null) {
                drawItemIcon(font, r.icon, x, cy);
                font.drawStringWithShadow(r.text, x + ICON_SIZE + ICON_GAP, cy + (ICON_SIZE - 8) / 2, -1);
            } else {
                font.drawStringWithShadow(r.text, x, cy, -1);
            }
            if (i == 0) {
                cy += 2;
            }
            cy += r.pitch();
        }

        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    private static void drawItemIcon(FontRenderer font, ItemStack stack, int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        ITEM_RENDER.zLevel = ZLEVEL;
        ITEM_RENDER.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), stack, x, y);
        ITEM_RENDER.zLevel = 0.0F;
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    private static void drawBackground(RenderTooltipEvent event, int x, int y, int width, int height) {
        int bgStart = event.backgroundStart;
        int bgEnd = event.backgroundEnd;
        int borderStart = event.borderStart;
        int borderEnd = event.borderEnd;

        drawGradientRect(x - 3, y - 4, x + width + 3, y - 3, bgStart, bgStart);
        drawGradientRect(x - 3, y + height + 3, x + width + 3, y + height + 4, bgEnd, bgEnd);
        drawGradientRect(x - 3, y - 3, x + width + 3, y + height + 3, bgStart, bgEnd);
        drawGradientRect(x - 4, y - 3, x - 3, y + height + 3, bgStart, bgEnd);
        drawGradientRect(x + width + 3, y - 3, x + width + 4, y + height + 3, bgStart, bgEnd);
        drawGradientRect(x - 3, y - 2, x - 2, y + height + 2, borderStart, borderEnd);
        drawGradientRect(x + width + 2, y - 2, x + width + 3, y + height + 2, borderStart, borderEnd);
        drawGradientRect(x - 3, y - 3, x + width + 3, y - 2, borderStart, borderStart);
        drawGradientRect(x - 3, y + height + 2, x + width + 3, y + height + 3, borderEnd, borderEnd);
    }

    private static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float startA = (startColor >> 24 & 255) / 255.0F;
        float startR = (startColor >> 16 & 255) / 255.0F;
        float startG = (startColor >> 8 & 255) / 255.0F;
        float startB = (startColor & 255) / 255.0F;
        float endA = (endColor >> 24 & 255) / 255.0F;
        float endR = (endColor >> 16 & 255) / 255.0F;
        float endG = (endColor >> 8 & 255) / 255.0F;
        float endB = (endColor & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(startR, startG, startB, startA);
        tessellator.addVertex(right, top, ZLEVEL);
        tessellator.addVertex(left, top, ZLEVEL);
        tessellator.setColorRGBA_F(endR, endG, endB, endA);
        tessellator.addVertex(left, bottom, ZLEVEL);
        tessellator.addVertex(right, bottom, ZLEVEL);
        tessellator.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}