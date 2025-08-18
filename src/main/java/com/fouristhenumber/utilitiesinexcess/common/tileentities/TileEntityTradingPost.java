package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IParentWidget;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.value.sync.ModularSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SortableListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.fouristhenumber.utilitiesinexcess.mixins.early.minecraft.accessors.AccessorMerchantRecipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.jetbrains.annotations.ApiStatus;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import javax.annotation.Nullable;

public class TileEntityTradingPost extends TileEntity implements IGuiHolder<PosGuiData> {

    public interface IMerchantRecipeExtension {

        boolean uie$getFavorite();

        void uie$setFavorite(boolean value);

        EntityPlayer uie$getPlayer();

        void uie$setPlayer(EntityPlayer player);

        IMerchant uie$getMerchant();

        void uie$setMerchant(IMerchant merchant);

        int uie$getListIndex();

        void uie$setListIndex(int index);

        default MerchantRecipeList uie$getList() {
            return this.uie$getMerchant()
                .getRecipes(uie$getPlayer());
        }

        default MerchantRecipe uie$getSelf() {
            return (MerchantRecipe) this.uie$getList()
                .get(this.uie$getListIndex());
        }
    }

    public static class TradeItemHandler implements IItemHandlerModifiable {

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
            if (owner.getRecipe() == null) return null;
            if (slot == 0) return owner.getRecipe()
                    .getItemToBuy();
            if (slot == 1) return owner.getRecipe()
                    .getSecondItemToBuy();
            if (slot == 2) return owner.getRecipe()
                    .getItemToSell();
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

    public static class TradeWidget extends ParentWidget<TradeWidget> implements Interactable
    {
        public TradeWidget()
        {
            super();
            this.coverChildren();

            var handler = new TradeItemHandler(this);

            this.child(
                    IKey.str("*")
                            .asWidget()
                            .margin(0, 0, -7, 0)
                            .setEnabledIf(w -> getRecipe() != null && ((IMerchantRecipeExtension)getRecipe()).uie$getFavorite()));
            // new ItemDisplayWidget().displayAmount(true)
            // .item(new ObjectValue.Dynamic<>(() -> getTrade().getInput1(), v -> {}))
            Flow inputItems = new Row().childPadding(5)
                    .coverChildren()
                    .child(new ItemSlot().slot(new ModularSlot(handler, 0).accessibility(false, false)))
                    .child(new ItemSlot().slot(new ModularSlot(handler, 1).accessibility(false, false)));
            ProgressWidget progress = new ProgressWidget().direction(ProgressWidget.Direction.RIGHT)
                    .texture(GuiTextures.PROGRESS_ARROW, 20)
                    .progress(() -> {
                        var trade = getRecipe();
                        return 1 - (((AccessorMerchantRecipe)trade)
                                .getCurrentUses()
                                / (double) (((AccessorMerchantRecipe)trade)
                                .getMaxUses() ));
                    });
            Flow wholeRow = new Row().coverChildren()
                    .childPadding(2)
                    .child(inputItems)
                    .child(progress)
                    .child(new ItemSlot().slot(new ModularSlot(handler, 2).accessibility(false, false)));
            this.child(wholeRow);
        }

        @Override
        public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
            super.draw(context, widgetTheme);
        }

        public MerchantRecipe recipe;
        public void setRecipe(MerchantRecipe r)
        {
            recipe=r;
        }
        public MerchantRecipe getRecipe()
        {
            return recipe;
        }

    }
    public static class TradeListSH extends SyncHandler
    {
        private final PosGuiData data;

        public TradeListSH(PosGuiData data)
        {
            super();
            this.data = data;
        }

        public List<IMerchant> getMerchants()
        {
            // Baby villagers shouldn't trade :P
            AxisAlignedBB boundingBox = data.getTileEntity()
                    .getRenderBoundingBox()
                    .expand(
                            32,
                            data.getWorld()
                                    .getHeight(),
                            32);
            return data.getWorld()
                    .selectEntitiesWithinAABB(IMerchant.class, boundingBox, entity -> {
                        if (entity instanceof EntityAgeable ageable) return ageable.getGrowingAge() >= 0;
                        return true;
                    });
        }


        public HashSet<MerchantRecipe> recipes=new HashSet<>();

        public void sendMerchantTrade(MerchantRecipe trade)
        {
            if(!recipes.contains(trade))
            {
                recipes.add(trade);
                syncToClient(0,buffer -> {
                    buffer.writeNBTTagCompoundToBuffer(trade.writeToTags());
                });
            }

        }
        @Override
        public void detectAndSendChanges(boolean init) {
            for (IMerchant merchant : getMerchants()) {
                for (Object _recipe : merchant.getRecipes(data.getPlayer())) {
                    MerchantRecipe recipe=(MerchantRecipe)_recipe;
                    sendMerchantTrade(recipe);
                }
            }
        }

        @Override
        public void readOnClient(int id, PacketBuffer buf) throws IOException {
            if(id==0)
            {
                NBTTagCompound recipeTag=buf.readNBTTagCompoundFromBuffer();
                MerchantRecipe recipe=new MerchantRecipe(recipeTag);
                recipes.add(recipe);
                syncChildren();
                return;
            }
        }
        public void syncChildren()
        {

        }

        @Override
        public void readOnServer(int id, PacketBuffer buf) throws IOException {

        }
    }
    public static class TradeList extends SortableListWidget<TradeWidget>
    {
        private final PosGuiData data;

        public void haveExactlyNChildren(int amount)
        {
            int cur=this.getChildren().size();
            int need=amount;
            int toDo=need-cur;
            if(toDo==0)return;
            if(toDo>0)
            {
                for (int i = 0; i < toDo; i++) {
                    var t=new TradeWidget();
                    child(new Item<>(t).child(t));
                }
            }
            else {
                for (int i = 0; i < -toDo; i++) {
                    remove(0);
                }
            }
        }


        public void syncChildren(HashSet<MerchantRecipe> recipes)
        {
            haveExactlyNChildren(recipes.size());
            var children=getChildren();
            int i=0;
            for (MerchantRecipe recipe : recipes) {

                Item<TradeWidget> iWidget = (Item<TradeWidget>)children.get(i);
                iWidget.getWidgetValue().setRecipe(recipe);
                i++;
            }
        }


        @Override
        public boolean isValidSyncHandler(SyncHandler syncHandler) {
            return syncHandler instanceof TradeListSH;
        }

        public TradeList(PosGuiData data)
        {
            super();
            this.data = data;
            this.syncHandler("tradelist",0);
        }

    }

    private static class TradingPostModularContainer extends ModularContainer {

        @Override
        public ItemStack slotClick(int slotId, int mouseButton, int mode, EntityPlayer player) {
            if (this.inventorySlots.size() <= slotId) return null;
            return super.slotClick(slotId, mouseButton, mode, player);
        }
    }
    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = ModularPanel.defaultPanel("trading_post");
        panel.bindPlayerInventory();
        settings.customContainer(()->new TradingPostModularContainer());

        var list=new TradeList(data);
        syncManager.syncValue("tradelist",0,new TradeListSH(data)
        {
            @Override
            public void syncChildren() {
                list.syncChildren(this.recipes);
            }
        });
        panel.child(list.margin(5,5));

        return panel;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @ApiStatus.Internal
    public static void handleMerchant(MerchantRecipeList list, IMerchant merchant, EntityPlayer player) {
        for (int i = 0; i < list.size(); i++) {
            MerchantRecipe recipe = (MerchantRecipe) list.get(i);
            IMerchantRecipeExtension er = (IMerchantRecipeExtension) recipe;
            er.uie$setPlayer(player);
            er.uie$setMerchant(merchant);
            er.uie$setListIndex(i);
        }
    }
}
