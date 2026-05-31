package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import com.cleanroommc.modularui.ModularUI;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventoryContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.TransferUpgrade;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemUpgrade extends Item implements IGuiHolder<PlayerInventoryGuiData>, ItemStackInventoryContainer
{

    private static final IIcon[] ICONS = new IIcon[TransferUpgrade.VALUES.length];

    public ItemUpgrade() {
        setUnlocalizedName("upgrade");
        setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {
        return ICONS[meta];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "_" + TransferUpgrade.VALUES[stack.getItemDamage()].getName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        for (TransferUpgrade upgrade : TransferUpgrade.VALUES) {
            ICONS[upgrade.ordinal()] = register.registerIcon(UtilitiesInExcess.MODID + ":" + upgrade.getName());
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        for (TransferUpgrade upgrade : TransferUpgrade.VALUES) {
            items.add(new ItemStack(item, 1, upgrade.ordinal()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote && TransferUpgrade.getUpgrade(stack) == TransferUpgrade.FILTER) // SERVER side only
        {
            GuiFactories.playerInventory().open(player, InventoryTypes.PLAYER, player.inventory.currentItem);
        }
        return stack;
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("panel").height(125);
        panel.bindPlayerInventory();

        SlotGroup filterGroup = new SlotGroup("filter_slots", 1);
        ItemStackInventory itemInventory;

        ItemStack heldStack = data.getPlayer().getHeldItem();
        if (heldStack.hasTagCompound())
        {
            itemInventory = new ItemStackInventory(heldStack);
        }
        else
        {
            itemInventory = ItemStackInventory.BlankInventory(heldStack);
        }

        IItemHandler handler = new InvWrapper(itemInventory);

        panel.child(
            IKey.str(StatCollector.translateToLocal("Item Filter"))
                .asWidget()
                .marginLeft(5)
                .marginRight(5)
                .marginTop(5)
                .marginBottom(-15)
        );

        Flow flow = Flow.row().size(18*9, 18).horizontalCenter().marginTop(16);

        for (int i = 0; i < this.getInventorySize(); i++)
        {
            flow.child(new ItemSlot().slot(new ModularSlot(handler,i).slotGroup(filterGroup)));
        }

        panel.child(flow);
        syncManager.addCloseListener(itemInventory::writeInventoryToHeldStack);

        return panel;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }

    @Override
    public int getInventorySize() {
        return 9;
    }
}
