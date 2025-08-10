package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.sizer.Area;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTradingPost extends TileEntity implements IGuiHolder<PosGuiData> {

    public TileEntityTradingPost() {
        super();
        if (!this.worldObj.isRemote) clientCachedTrades = new HashMap<>();
    }

    private static class TradingPostModularContainer extends ModularContainer {

        // Handle client-side only slots this hacky way :P
        @Override
        public ItemStack slotClick(int slotId, int mouseButton, int mode, EntityPlayer player) {
            if (this.inventorySlots.size() <= slotId) return null;
            return super.slotClick(slotId, mouseButton, mode, player);
        }
    }

    public static class TradeWidget extends ParentWidget<TradeWidget> implements Interactable {

        private final ArrayList<CachedMerchantTrade> trades;
        private final int index;
        private final TradeGrid optionalGrid;

        @Override
        public void onUpdate() {
            super.onUpdate();
            // this.getContext().isMouseAbove(this);
            if (isBelowMouse()) clientVillager = getMerchantEntity();
        }

        public static void sortTradesList(ArrayList<CachedMerchantTrade> trades) {
            // First sort alphabetically, then favoritably
            trades.sort((o1, o2) -> {
                if (o1.isFavorited == o2.isFavorited) {
                    // Alphabetical
                    String o1d = o1.getOutput()
                        .getDisplayName();
                    String o2d = o2.getOutput()
                        .getDisplayName();
                    int output = o1d.compareTo(o2d);
                    if (output != 0) return output;

                    o1d = o1.getInput1()
                        .getDisplayName();
                    o2d = o2.getInput1()
                        .getDisplayName();
                    output = o1d.compareTo(o2d);
                    if (output != 0) return output;

                    o1d = o1.getInput2() == null ? ""
                        : o1.getInput2()
                            .getDisplayName();
                    o2d = o2.getInput2() == null ? ""
                        : o1.getInput2()
                            .getDisplayName();
                    output = o1d.compareTo(o2d);
                    if (output != 0) return output;

                    return 0;
                } else {
                    return o1.isFavorited ? -1 : 1;
                }
            });
        }

        @Override
        public @NotNull Result onMousePressed(int mouseButton) {
            if (mouseButton == 0
                && (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU))) {
                getTrade().isFavorited = !getTrade().isFavorited;
                sortTradesList(trades);
                return Result.SUCCESS;
            }
            return Result.ACCEPT;
        }

        public EntityLivingBase getMerchantEntity() {
            return (EntityLivingBase) getTrade().getMerchant();
        }

        public CachedMerchantTrade getTrade() {
            return trades.get(index);
        }

        private static class TradeItemHandler implements IItemHandlerModifiable {

            private final TradeWidget owner;

            public TradeItemHandler(TradeWidget owner) {
                this.owner = owner;
            }

            @Override
            public void setStackInSlot(int slot, @Nullable ItemStack stack) {}

            @Override
            public int getSlots() {
                return 3;
            }

            @Override
            public @Nullable ItemStack getStackInSlot(int slot) {
                if (slot == 0) return owner.getTrade()
                    .getInput1();
                if (slot == 1) return owner.getTrade()
                    .getInput2();
                if (slot == 2) return owner.getTrade()
                    .getOutput();
                return null;
            }

            @Override
            public @Nullable ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
                return null;
            }

            @Override
            public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
                return null;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }
        }

        public TradeWidget(ArrayList<CachedMerchantTrade> trades, int index, TradeGrid optionalGird) {
            this.trades = trades;
            this.index = index;
            this.optionalGrid = optionalGird;
            this.coverChildren();

            var handler = new TradeItemHandler(this);

            this.child(
                IKey.str("*")
                    .asWidget()
                    .margin(0, 0, -7, 0)
                    .setEnabledIf(w -> getTrade().isFavorited));

            // new ItemDisplayWidget().displayAmount(true)
            // .item(new ObjectValue.Dynamic<>(() -> getTrade().getInput1(), v -> {}))
            Flow inputItems = new Row().childPadding(5)
                .coverChildren()
                .child(new ItemSlot().slot(new ModularSlot(handler, 0).accessibility(false, false)))
                .child(new ItemSlot().slot(new ModularSlot(handler, 1).accessibility(false, false)));
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
                .child(new ItemSlot().slot(new ModularSlot(handler, 2).accessibility(false, false)));
            this.child(wholeRow);
        }
    }

    public static class TradeGrid extends Grid {

        private final ArrayList<CachedMerchantTrade> trades;

        public TradeGrid(ArrayList<CachedMerchantTrade> trades) {
            this.trades = trades;
            TradeWidget.sortTradesList(trades);
            this.mapTo(1, trades.size(), index -> new TradeWidget(this.trades, index, this).padding(5, 5));
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
        settings.customContainer(TradingPostModularContainer::new);
        int w = 176;
        int h = 166;
        if (!clientCachedTrades.containsKey(
            data.getPlayer()
                .getGameProfile()))
            clientCachedTrades.put(
                data.getPlayer()
                    .getGameProfile(),
                new ArrayList<>());
        ModularPanel panel = ModularPanel.defaultPanel("trading_post", w, h);
        panel.bindPlayerInventory();
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
            new TradeGrid(
                clientCachedTrades.get(
                    data.getPlayer()
                        .getGameProfile())).marginTop(12)
                            .size(w - 8 - 77, h - 12 - 8 - 80)
                            .scrollable());
        panel.child(
            new EntityDisplay(() -> clientVillager, true, 20).size(36, 36 * 2)
                .margin(115, 10));
        return panel;
    }

    public final static class CachedMerchantTrade {

        @Override
        public String toString() {
            return "CachedMerchantTrade{" + "output=" + output + ", input2=" + input2 + ", input1=" + input1 + '}';
        }

        private final MerchantRecipe recipe;
        private final IMerchant merchant;
        private ItemStack input1;
        private ItemStack input2;
        private ItemStack output;
        private int currentUses;
        private int maxUses;
        private boolean isFavorited;

        private boolean mostlyEquals(CachedMerchantTrade t) {
            return t.merchant == this.merchant && ItemStack.areItemStacksEqual(t.getOutput(), this.getOutput())
                && ItemStack.areItemStacksEqual(t.getInput2(), this.getInput2())
                && ItemStack.areItemStacksEqual(t.getInput1(), this.getInput1());
        }

        public boolean getIsFavorited() {
            return isFavorited;
        }

        public boolean setIsFavorited(boolean v) {
            return isFavorited = v;
        }

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

    @SideOnly(Side.SERVER)
    public Map<GameProfile, ArrayList<CachedMerchantTrade>> clientCachedTrades;

    private void addIfMissing(CachedMerchantTrade t, EntityPlayer player) {
        GameProfile p = player.getGameProfile();
        if (!clientCachedTrades.containsKey(p)) clientCachedTrades.put(p, new ArrayList<>());
        if (clientCachedTrades.get(p)
            .stream()
            .anyMatch(a -> a.mostlyEquals(t))) return;
        clientCachedTrades.get(p)
            .add(t);
    }

    public void updateCachedTrades(EntityPlayer player) {
        this.markDirty();
        if (!getWorldObj().isRemote) return;
        AxisAlignedBB box = this.getRenderBoundingBox()
            .expand(32, getWorldObj().getHeight(), 32);
        List<IMerchant> foundMerchants = worldObj.getEntitiesWithinAABB(IMerchant.class, box);
        for (IMerchant merchant : foundMerchants) {
            MerchantRecipeList allTrades = merchant.getRecipes(player);
            for (Object _trade : allTrades) {
                MerchantRecipe trade = (MerchantRecipe) _trade;
                CachedMerchantTrade cachedTrade = new CachedMerchantTrade(trade, merchant);
                addIfMissing(cachedTrade, player);
            }
        }
    }

    public static EntityPlayer getPlayer(GameProfile profile, World world) {
        return world.getPlayerEntityByName(profile.getName());
    }

    public void updateAllCachedTrades(World world) {
        clientCachedTrades
            .forEach((gameProfile, cachedMerchantTrades) -> { updateCachedTrades(getPlayer(gameProfile, world)); });
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        updateAllCachedTrades(this.getWorldObj());
        // write clientCachedTrades
        NBTTagList profileList = new NBTTagList();
        compound.setTag("profileList", profileList);
        clientCachedTrades.forEach((gameProfile, cachedMerchantTrades) -> {
            NBTTagCompound profile = new NBTTagCompound();
            profile.setString(
                "uuid",
                gameProfile.getId()
                    .toString());
            profile.setString("name", gameProfile.getName());
            NBTTagList list = new NBTTagList();
            profile.setTag("list", list);
            for (CachedMerchantTrade trade : cachedMerchantTrades) {
                NBTTagCompound tradeTag = new NBTTagCompound();
                tradeTag.setTag(
                    "input1",
                    trade.getInput1()
                        .writeToNBT(new NBTTagCompound()));
                if (trade.getInput2() != null) tradeTag.setTag(
                    "input2",
                    trade.getInput2()
                        .writeToNBT(new NBTTagCompound()));
                tradeTag.setTag(
                    "input3",
                    trade.getOutput()
                        .writeToNBT(new NBTTagCompound()));
                tradeTag.setInteger("uses", trade.getCurrentUses());
                tradeTag.setInteger("max", trade.getMaxUses());
                if (trade.getMerchant() instanceof EntityLivingBase b) {
                    var entity = new NBTTagCompound();
                    b.writeToNBT(entity);
                    tradeTag.setTag("merchant", entity);
                }
                tradeTag.setBoolean("favorite", trade.getIsFavorited());
                list.appendTag(tradeTag);
            }
            profileList.appendTag(profile);
        });

    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        clientCachedTrades.clear();

        updateAllCachedTrades(this.getWorldObj());
    }

}
