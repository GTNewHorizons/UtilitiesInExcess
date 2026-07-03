package com.fouristhenumber.utilitiesinexcess.render;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.gtnewhorizon.gtnhlib.client.event.RenderTooltipEvent;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.gtnhlib.util.numberformatting.options.CompactOptions;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT)
public final class ItemGridTooltip {

    public interface Provider {

        /**
         * The stacks to lay out in the grid, parsed from {@code stack}'s NBT. Return an empty list to suppress the
         * grid (a non-empty {@link #getGridFooter} still shows).
         */
        @NotNull
        List<ItemStack> getGridContents(ItemStack stack);

        default List<String> getGridHeader(ItemStack stack, List<String> vanillaLines) {
            return vanillaLines;
        }

        @NotNull
        default List<String> getGridFooter(ItemStack stack) {
            return Collections.emptyList();
        }

        default int getGridColumns() {
            return 3;
        }

        default int getGridRows() {
            return 2;
        }
    }

    private static final RenderItem ITEM_RENDER = new RenderItem();

    private static final int ICON_SIZE = 16;
    private static final int CELL = ICON_SIZE + 5;
    private static final int TEXT_PITCH = 10;
    private static final int BLOCK_GAP = 2;
    private static final float ZLEVEL = 300.0F;

    @SubscribeEvent
    public static void onRenderTooltip(RenderTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (!(stack.getItem() instanceof Provider provider)) {
            return;
        }

        List<ItemStack> contents = provider.getGridContents(stack);
        List<String> footer = provider.getGridFooter(stack);
        if (contents.isEmpty() && footer.isEmpty()) {
            return;
        }

        event.alternativeRenderer = lines -> render(
            event,
            provider.getGridHeader(stack, lines),
            footer,
            contents,
            provider.getGridColumns(),
            provider.getGridRows());
    }

    public static void render(RenderTooltipEvent event, List<String> header, List<String> footer,
        List<ItemStack> stacks, int columns, int rows) {
        FontRenderer font = event.font;

        int cells = columns * rows;
        boolean overflow = stacks.size() > cells;
        int filledCells = stacks.isEmpty() ? 0 : (overflow ? cells : stacks.size());
        int usedRows = filledCells == 0 ? 0 : (int) Math.ceil((double) filledCells / columns);
        int usedCols = usedRows > 1 ? columns : filledCells;
        int gridWidth = usedCols * CELL;
        int gridHeight = usedRows * CELL;

        int width = gridWidth;
        for (String line : header) {
            width = Math.max(width, font.getStringWidth(line));
        }
        for (String line : footer) {
            width = Math.max(width, font.getStringWidth(line));
        }

        int height = layoutHeight(header, footer, gridHeight, usedRows > 0);

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
        for (String line : header) {
            font.drawStringWithShadow(line, x, cy, -1);
            cy += TEXT_PITCH;
        }

        if (usedRows > 0) {
            if (!header.isEmpty()) {
                cy += BLOCK_GAP;
            }
            drawGrid(font, stacks, x, cy, columns, cells, overflow);
            cy += gridHeight;
        }

        if (!footer.isEmpty()) {
            cy += BLOCK_GAP;
            for (String line : footer) {
                font.drawStringWithShadow(line, x, cy, -1);
                cy += TEXT_PITCH;
            }
        }

        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    private static int layoutHeight(List<String> header, List<String> footer, int gridHeight, boolean hasGrid) {
        int height = 0;
        if (!header.isEmpty()) {
            height += header.size() * TEXT_PITCH;
        }
        if (hasGrid) {
            if (height > 0) height += BLOCK_GAP;
            height += gridHeight;
        }
        if (!footer.isEmpty()) {
            if (height > 0) height += BLOCK_GAP;
            height += footer.size() * TEXT_PITCH;
        }
        // Text pitch over-counts the last line's descenders by ~2px; trim so the border hugs the content.
        height -= 2;
        return Math.max(height, 8);
    }

    private static void drawGrid(FontRenderer font, List<ItemStack> stacks, int gridX, int gridY, int columns,
        int cells, boolean overflow) {
        int iconCells = overflow ? cells - 1 : Math.min(stacks.size(), cells);
        for (int i = 0; i < iconCells; i++) {
            int cellX = gridX + (i % columns) * CELL;
            int cellY = gridY + (i / columns) * CELL;
            drawCell(font, stacks.get(i), cellX, cellY);
        }
        if (overflow) {
            int i = cells - 1;
            int cellX = gridX + (i % columns) * CELL;
            int cellY = gridY + (i / columns) * CELL;
            String badge = EnumChatFormatting.YELLOW + "+" + (stacks.size() - iconCells);
            font.drawStringWithShadow(
                badge,
                cellX + (ICON_SIZE - font.getStringWidth(badge)) / 2,
                cellY + (ICON_SIZE - 8) / 2,
                -1);
        }
    }

    private static void drawCell(FontRenderer font, ItemStack stack, int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        ITEM_RENDER.zLevel = ZLEVEL;
        ITEM_RENDER.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), stack, x, y);
        ITEM_RENDER.renderItemOverlayIntoGUI(
            font,
            mc.getTextureManager(),
            stack,
            x,
            y,
            NumberFormatUtil.formatNumberCompact(
                stack.stackSize,
                new CompactOptions().setDecimalPlaces(1)
                    .setRoundingMode(RoundingMode.FLOOR)));
        ITEM_RENDER.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
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
