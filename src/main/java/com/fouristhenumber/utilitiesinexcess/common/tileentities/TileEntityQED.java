package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.factory.inventory.ItemHandler;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.ItemStackItemHandler;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.GenericSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.fouristhenumber.utilitiesinexcess.api.QEDRegistry;
import com.fouristhenumber.utilitiesinexcess.compat.nei.QEDRecipeHandler;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.opengl.GL11;

import static com.cleanroommc.modularui.drawable.GuiTextures.PROGRESS_ARROW;

public class TileEntityQED extends TileEntity implements IInventory, IGuiHolder<PosGuiData> {
    private final InventoryCrafting craftMatrix = new InventoryCrafting(new ContainerDummy(), 3, 3);
    private final InventoryCraftResult craftResult = new InventoryCraftResult();

    private static final ItemStack fakeItem = new ItemStack(new Item());
    private ItemStack preview = fakeItem;


    @Override
    public void updateEntity() {

        super.updateEntity();
    }

    private void updateCraftingResult() {
        ItemStack stack = QEDRegistry.instance().findRecipe(craftMatrix, false);
        preview = stack == null ? fakeItem : stack;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        syncManager.syncValue("preview", GenericSyncValue.forItem(() -> preview, null));

        // Add title
        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal(getInventoryName()))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(5)
                        .marginBottom(-15)));

        IItemHandler itemHandler = new InvWrapper(this);

        // Input slots
        panel.child(SlotGroupWidget.builder()
            .matrix("III", "III", "III")
            .key('I', index -> new ItemSlot().slot(new ModularSlot(itemHandler, index))).build()
            .marginTop(20)
            .marginLeft(37));

        // Progress bar
        panel.child(new ProgressWidget()
            .texture(PROGRESS_ARROW, 20)
            .marginTop(38)
            .marginLeft(97));

        // Preview slot
        panel.child(new ItemDisplayWidget()
            .syncHandler("preview")
            .marginTop(15)
            .marginLeft(97));

        // Output slot
        panel.child(new ItemSlot()
            .slot(new ModularSlot(itemHandler, 9))
            .marginTop(38)
            .marginLeft(121));

        return panel;
    }

    @Override
    public int getSizeInventory() {
        return 10;
    }

    @Override
    public String getInventoryName() {
        return "tile.qed.name";
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index < 9 ? craftMatrix.getStackInSlot(index) : craftResult.getStackInSlot(0);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack;
        if (index < 9) {
            stack = craftMatrix.decrStackSize(index, count);
        } else {
            stack = craftResult.decrStackSize(0, count);
        }
        markDirty();
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return index < 9 ? craftMatrix.getStackInSlotOnClosing(index) : craftResult.getStackInSlotOnClosing(0);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 9) {
            craftMatrix.setInventorySlotContents(index, stack);
            updateCraftingResult();
        } else {
            craftResult.setInventorySlotContents(0, stack);
        }
        markDirty();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player
            .getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D)
            <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int index = 0; index < nbttaglist.tagCount(); index++) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(index);
            int slot = nbttagcompound1.getByte("Slot") & 255;
            if (slot < getSizeInventory()) {
                setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbttagcompound1));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        for (int index = 0; index < getSizeInventory(); index++) {
            ItemStack curStack = getStackInSlot(index);
            if (curStack != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) index);
                curStack.writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        nbttagcompound.setTag("Items", nbttaglist);
    }

    public static class ContainerDummy extends Container {
        public ContainerDummy() {
        }

        public boolean canInteractWith(EntityPlayer var1) {
            return false;
        }
    }
}
