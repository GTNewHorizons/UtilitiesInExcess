package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.value.ObjectValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.sizer.Area;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;

public class TileEntityTradingPost extends TileEntity implements IGuiHolder<PosGuiData> {

    public static class TradeWidget extends ParentWidget<TradeWidget> {

        private final ArrayList<CachedMerchantTrade> trades;
        private final int index;

        @Override
        public void onUpdate() {
            super.onUpdate();
            // this.getContext().isMouseAbove(this);
            if (isBelowMouse()) clientVillager = getMerchantEntity();

        }

        public EntityLivingBase getMerchantEntity() {
            return (EntityLivingBase) getTrade().getMerchant();
        }

        public CachedMerchantTrade getTrade() {
            return trades.get(index);
        }

        public TradeWidget(ArrayList<CachedMerchantTrade> trades, int index) {
            this.trades = trades;
            this.index = index;
            this.coverChildren();

            Flow inputItems = new Row().childPadding(5)
                .coverChildren()
                .child(
                    new ItemDisplayWidget().displayAmount(true)
                        .item(new ObjectValue.Dynamic<>(() -> getTrade().getInput1(), v -> {})))
                .child(
                    new ItemDisplayWidget().displayAmount(true)
                        .item(new ObjectValue.Dynamic<>(() -> getTrade().getInput2(), v -> {})));

            ProgressWidget progress = new ProgressWidget().direction(ProgressWidget.Direction.RIGHT)
                .texture(GuiTextures.PROGRESS_ARROW, 20)
                .progress(() -> {
                    var trade = getTrade();
                    return 1 - (trade.currentUses / (double) trade.maxUses);
                });

            Flow wholeRow = new Row().coverChildren()
                .childPadding(2)
                .child(inputItems)
                .child(progress)
                .child(
                    new ItemDisplayWidget().displayAmount(true)
                        .item(new ObjectValue.Dynamic<>(() -> getTrade().getOutput(), v -> {})));;

            this.child(wholeRow);
        }
    }

    public static class TradeGrid extends Grid {

        private final ArrayList<CachedMerchantTrade> trades;

        public TradeGrid(ArrayList<CachedMerchantTrade> trades) {
            this.trades = trades;
            this.mapTo(1, trades.size(), index -> new TradeWidget(this.trades, index).padding(5, 5));
            // this.row(n);
        }
    }

    public static class EntityDisplay extends Widget<EntityDisplay> {

        private final Supplier<EntityLivingBase> entitySupplier;
        private final boolean lookAtMouse;
        private final float mouseFollowStrength;

        public EntityDisplay(Supplier<EntityLivingBase> e, boolean lookAtMouse, float mouseFollowStrength) {
            this.entitySupplier = e;
            this.lookAtMouse = lookAtMouse;
            this.mouseFollowStrength = mouseFollowStrength;
            this.size(18, 36);
        }

        public static void drawEntity(int x, int y, int scale, EntityLivingBase e, boolean lookAtMouse, float mouseX,
            float mouseY, float mouseFollowStrength) {
            if (e == null) return;
            GL11.glColor4f(1, 1, 1, 1);
            Platform.setupDrawItem();
            float yawX = 0;
            float pitchY = 0;
            if (lookAtMouse) {
                yawX = -mouseX;
                pitchY -= mouseY;
                yawX /= scale;
                pitchY /= scale;
                yawX *= mouseFollowStrength;
                pitchY *= mouseFollowStrength;
            }
            // TODO: figure out if u can somehow turn off shadows without mixins
            GuiInventory.func_147046_a(x, y, scale, yawX, pitchY, e);
            Platform.endDrawItem();
        }

        @Override
        public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
            EntityLivingBase e = entitySupplier.get();
            Area area = this.getArea();
            drawEntity(
                +area.width / 2,
                area.height,
                area.width,
                e,
                this.lookAtMouse,
                context.getMouseX(),
                context.getMouseY(),
                mouseFollowStrength);
        }
    }

    public static EntityLivingBase clientVillager;

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        int w = 176;
        int h = 166;
        ModularPanel panel = ModularPanel.defaultPanel("trading_post", w, h);
        panel.bindPlayerInventory();
        // Add title
        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal("tile.trading_post.name"))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(5)
                        .marginBottom(-15)));
        panel.child(
            new TradeGrid(clientCachedTrades).marginTop(12)
                .size(w - 8 - 77, h - 12 - 8 - 80)
                .scrollable());
        panel.child(
            new EntityDisplay(() -> clientVillager, true, 20).size(36, 36 * 2)
                .margin(115, 10));
        return panel;
    }

    public final static class CachedMerchantTrade {

        private final MerchantRecipe recipe;
        private final IMerchant merchant;
        private ItemStack input1;
        private ItemStack input2;
        private ItemStack output;
        private int currentUses;
        private int maxUses;

        public IMerchant getMerchant() {
            return merchant;
        }

        public ItemStack getInput1() {
            updateCache();
            return input1;
        }

        public ItemStack getInput2() {
            updateCache();
            return input2;
        }

        public ItemStack getOutput() {
            updateCache();
            return output;
        }

        public int getCurrentUses() {
            updateCache();
            return currentUses;
        }

        public int getMaxUses() {
            updateCache();
            return maxUses;
        }

        public boolean isRecipeDisabled() {
            updateCache();
            return getCurrentUses() >= getMaxUses() || merchant == null;
        }

        public final void updateCache() {
            if (recipe == null) return;
            AccessorMerchantRecipe accessorTrade = (AccessorMerchantRecipe) recipe;
            input1 = recipe.getItemToBuy();
            input2 = recipe.getSecondItemToBuy();
            output = recipe.getItemToSell();
            currentUses = accessorTrade.getCurrentUses();
            maxUses = accessorTrade.getMaxUses();
        }

        public CachedMerchantTrade(MerchantRecipe trade, IMerchant merchant) {
            recipe = trade;
            this.merchant = merchant;
            updateCache();
        }
    }

    public final ArrayList<CachedMerchantTrade> clientCachedTrades = new ArrayList<>();

    public void updateCachedTrades(EntityPlayer player) {
        if (!getWorldObj().isRemote) return;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            xCoord - 32,
            yCoord - getWorldObj().getHeight(),
            zCoord - 32,
            xCoord + getWorldObj().getHeight(),
            yCoord + 32,
            zCoord + 32);
        List<IMerchant> foundMerchants = worldObj.getEntitiesWithinAABB(IMerchant.class, box);
        clientCachedTrades.clear();
        for (IMerchant merchant : foundMerchants) {
            MerchantRecipeList allTrades = merchant.getRecipes(player);
            for (Object _trade : allTrades) {
                MerchantRecipe trade = (MerchantRecipe) _trade;
                CachedMerchantTrade cachedTrade = new CachedMerchantTrade(trade, merchant);
                clientCachedTrades.add(cachedTrade);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

}
