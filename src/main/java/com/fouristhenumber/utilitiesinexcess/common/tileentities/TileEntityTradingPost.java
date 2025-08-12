package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import static com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTradingPost.TradeSyncHandler.tradeHash;
import static com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTradingPost.TradeWidget.sortTradesList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
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
import com.cleanroommc.modularui.value.sync.SyncHandler;
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTradingPost extends TileEntity implements IGuiHolder<PosGuiData> {

    private static class TradingPostModularContainer extends ModularContainer {

        @Override
        public ItemStack slotClick(int slotId, int mouseButton, int mode, EntityPlayer player) {
            if (this.inventorySlots.size() <= slotId) return null;
            return super.slotClick(slotId, mouseButton, mode, player);
        }
    }

    public TileEntityTradingPost(World world) {
        super();
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
                if (o1.uie$getFavorite() == o2.uie$getFavorite()) {
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
                    return o1.uie$getFavorite() ? -1 : 1;
                }
            });
        }


        @Override
        public @NotNull Result onMousePressed(int mouseButton) {
            if (mouseButton == 0 && (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU))) {
                TradeSyncHandler handler = (TradeSyncHandler) this.optionalGrid.sync.getSyncHandler("trades:0");
                handler.syncToServer(1, buffer -> {
                    buffer.writeStringToBuffer(tradeHash(this.getTrade().getTrade()));
                    buffer.writeBoolean(!getTrade().uie$getFavorite());
                });
                sortTradesList(trades);
                return Result.SUCCESS;
            }
            else if(mouseButton==0)
            {
                TradeSyncHandler handler = (TradeSyncHandler) this.optionalGrid.sync.getSyncHandler("trades:0");
                handler.syncToServer(2, buffer -> {
                    buffer.writeStringToBuffer(tradeHash(this.getTrade().getTrade()));
                    buffer.writeBoolean(GuiScreen.isShiftKeyDown());
                });
                sortTradesList(trades);
                return Result.SUCCESS;
            }
            return Result.ACCEPT;
        }

        public EntityLivingBase getMerchantEntity() {
            return (EntityLivingBase) getTrade().getMerchant();
        }

        public CachedMerchantTrade getTrade() {
            if (trades == null || trades.size() <= index) return null;
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
                if (owner.getTrade() == null) return null;
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
                    .setEnabledIf(w -> getTrade() != null && getTrade().uie$getFavorite()));

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
                    return 1 - (trade.getTradeAccessor()
                        .getCurrentUses()
                        / (double) trade.getTradeAccessor()
                            .getMaxUses());
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

        private ArrayList<CachedMerchantTrade> trades;
        private final Supplier<ArrayList<CachedMerchantTrade>> _trades;
        private final boolean client;
        private final PanelSyncManager sync;

        private final HashMap<Integer, TradeWidget> widgets = new HashMap<>();

        @Override
        public void onUpdate() {
            super.onUpdate();
            trades.clear();
            trades.addAll(_trades.get());
            sortTradesList(trades);
        }

        public TradeGrid(Supplier<ArrayList<CachedMerchantTrade>> trades, boolean client, PanelSyncManager sync) {
            _trades = trades;
            this.client = client;
            this.sync = sync;
            this.trades = new ArrayList<>();
            this.mapTo(
                1,
                256,
                i -> new TradeWidget(this.trades, i, this).setEnabledIf(a -> a.trades.size() > i)
                    .padding(5, 5));

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

    @SideOnly(Side.CLIENT)
    public static EntityLivingBase clientVillager;
    @SideOnly(Side.CLIENT)
    public HashSet<IMerchant> nearbyMerchants;

    public static class TradeSyncHandler extends SyncHandler {

        private final ArrayList<MerchantRecipeList> trades;
        private final TileEntityTradingPost post;
        private final EntityPlayer player;

        public TradeSyncHandler(ArrayList<MerchantRecipeList> trades, TileEntityTradingPost post, EntityPlayer p) {
            this.trades = trades;
            this.post = post;
            this.player = p;
        }

        public void setTrades(ArrayList<MerchantRecipeList> _trades) {
            this.trades.clear();
            this.trades.addAll(_trades);
            syncToClient(0, buffer -> {
                NBTTagList l = new NBTTagList();
                for (MerchantRecipeList trade : trades) {
                    l.appendTag(trade.getRecipiesAsTags());
                }
                var t = new NBTTagCompound();
                t.setTag("list", l);
                buffer.writeNBTTagCompoundToBuffer(t);
            });
        }

        @Override
        public void init(String key, PanelSyncManager syncManager) {
            super.init(key, syncManager);
            if (player instanceof EntityPlayerMP) detectAndSendChanges(true);
        }

        @Override
        public void detectAndSendChanges(boolean init) {
            super.detectAndSendChanges(init);
            ArrayList<MerchantRecipeList> l = new ArrayList<>();
            for (IMerchant m : post.nearbyMerchants) {
                var t=m.getRecipes(player);
                    l.add(t);
            }
            this.setTrades(l);
        }

        @Override
        public void readOnClient(int id, PacketBuffer buf) throws IOException {
            if (id == 0) {
                trades.clear();
                NBTTagCompound t = buf.readNBTTagCompoundFromBuffer();
                var l = (NBTTagList) t.getTag("list");
                for (int i = 0; i < l.tagCount(); i++) {
                    var tag = l.getCompoundTagAt(i);
                    trades.add(new MerchantRecipeList(tag));
                }
            }
        }

        public static String tradeHash(MerchantRecipe recipe)
        {
            return recipe.writeToTags().toString();
        }

        @Override
        public void readOnServer(int id, PacketBuffer buf) throws IOException {
            // Do nothing if server
            if (id == 1) {
                String hash = buf.readStringFromBuffer(4096);
                boolean val = buf.readBoolean();
                for (MerchantRecipeList trade : this.trades) {
                    for (Object o : trade) {
                        MerchantRecipe r=(MerchantRecipe) o;
                        String curHash=tradeHash(r);
                        if(curHash.equals(hash))
                        {
                            ((IFavoritable) r).uie$setFavorite(val);
                            break;
                        }
                    }
                }
            }
            if (id == 2) {
                String hash = buf.readStringFromBuffer(4096);
                boolean isMaximum = buf.readBoolean();
                for (MerchantRecipeList trade : this.trades) {
                    for (Object o : trade) {
                        MerchantRecipe r=(MerchantRecipe) o;
                        String curHash=tradeHash(r);
                        if(curHash.equals(hash))
                        {
                            //Handle the buying

                            r.incrementToolUses();
                            break;
                        }
                    }
                }
            }
            detectAndSendChanges(false);
        }
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        settings.customContainer(TradingPostModularContainer::new);
        int w = 176;
        int h = 166;
        ModularPanel panel = ModularPanel.defaultPanel("trading_post", w, h);
        panel.bindPlayerInventory();
        nearbyMerchants = new HashSet<>(
            data.getWorld()
                .getEntitiesWithinAABB(
                    IMerchant.class,
                    data.getTileEntity()
                        .getRenderBoundingBox()
                        .expand(32, 32, 32)));
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
        ArrayList<MerchantRecipeList> trades = new ArrayList<>();
        syncManager.syncValue("trades", new TradeSyncHandler(trades, this, data.getPlayer()));

        // TradeSyncHandler handler=(TradeSyncHandler)syncManager.getSyncHandler("trades:0");
        // handler.detectAndSendChanges(false);

        panel.child(
            new TradeGrid(
                () -> getTradesServer(syncManager, data.getPlayer(), data.isClient()),
                data.isClient(),
                syncManager).marginTop(12)
                    .size(w - 8 - 77, h - 12 - 8 - 80)
                    .scrollable()
                    .minRowHeight(0)
                    .collapseDisabledChild());
        panel.child(
            new EntityDisplay(() -> clientVillager, true, 20).size(36, 36 * 2)
                .margin(115, 10));
        return panel;
    }

    public ArrayList<CachedMerchantTrade> getTradesServer(PanelSyncManager syncManager, EntityPlayer player,
        boolean isClient) {
        TradeSyncHandler handler = (TradeSyncHandler) syncManager.getSyncHandler("trades:0");
        var allLists = handler.trades;
        if (!isClient) {
            ArrayList<MerchantRecipeList> l = new ArrayList<>();
            for (IMerchant m : nearbyMerchants) {
                l.add(m.getRecipes(player));
            }
            handler.setTrades(l);
        }
        ArrayList<CachedMerchantTrade> trades = new ArrayList<>();
        for (MerchantRecipeList l : allLists) {
            for (int i = 0; i < l.size(); i++) {
                var t = new CachedMerchantTrade(i, null, l);
                trades.add(t);
            }
        }
        return trades;
    }

    public final static class CachedMerchantTrade implements IFavoritable {

        public final IMerchant merchant;
        public final int tradeIndex;
        public final MerchantRecipeList list;

        public CachedMerchantTrade(int tradeIndex, IMerchant merchant, MerchantRecipeList l) {
            this.tradeIndex = tradeIndex;
            this.merchant = merchant;
            list = l;
        }

        @Override
        public String toString() {
            return "CachedMerchantTrade{" + "merchant="
                + merchant
                + ", tradeIndex="
                + tradeIndex
                + ", list="
                + list
                + ", i1="
                + getInput1()
                + ", i2="
                + getInput2()
                + ", o="
                + getOutput()
                + '}';
        }

        public MerchantRecipe getTrade() {
            return (MerchantRecipe) list.get(tradeIndex);
        }

        public AccessorMerchantRecipe getTradeAccessor() {
            return (AccessorMerchantRecipe) getTrade();
        }

        public ItemStack getOutput() {
            return getTrade().getItemToSell();
        }

        public ItemStack getInput1() {
            return getTrade().getItemToBuy();
        }

        public ItemStack getInput2() {
            return getTrade().getSecondItemToBuy();
        }

        public IMerchant getMerchant() {
            return this.merchant;
        }

        @Override
        public boolean uie$getFavorite() {
            return ((IFavoritable) getTrade()).uie$getFavorite();
        }

        @Override
        public void uie$setFavorite(boolean value) {
            ((IFavoritable) getTrade()).uie$setFavorite(value);
        }

        public void toggleFavorite() {
            uie$setFavorite(!uie$getFavorite());
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
