package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventoryContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.TransferUpgrade;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import static com.fouristhenumber.utilitiesinexcess.transfer.upgrade.AdvancedFilterMode.getAdvFilterMode;

public class ItemUpgrade extends Item implements IGuiHolder<PlayerInventoryGuiData>, ItemStackInventoryContainer
{
    public enum FilterMode {
        INVERTED("Inverted"),
        FUZZYNBT("Fuzzy - Ignores NBT"),
        FUZZYMETA("Fuzzy - Ignores Metadata");

        private final String label;

        FilterMode(String label)
        {
            this.label = label;
        }

        public String getLabel()
        {
            return label;
        }

        public static boolean isInverted(ItemStack stack)
        {
            if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Mode"))
            {
                int mode = stack.stackTagCompound.getInteger("Mode");
                return (mode & (1 << INVERTED.ordinal())) != 0;
            }
            return false;
        }

        public static boolean isInverted(int mode)
        {
            return (mode & (1 << INVERTED.ordinal())) != 0;
        }

        public static boolean isIgnoringMeta(ItemStack stack)
        {
            if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Mode"))
            {
                int mode = stack.stackTagCompound.getInteger("Mode");
                return (mode & (1 << FUZZYMETA.ordinal())) != 0;
            }
            return false;
        }

        public static boolean isIgnoringMeta(int mode)
        {
            return (mode & (1 << FUZZYMETA.ordinal())) != 0;
        }

        public static boolean isIgnoringNBT(ItemStack stack)
        {
            if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Mode"))
            {
                int mode = stack.stackTagCompound.getInteger("Mode");
                return (mode & (1 << FUZZYNBT.ordinal())) != 0;
            }
            return false;
        }

        public static boolean isIgnoringNBT(int mode)
        {
            return (mode & (1 << FUZZYNBT.ordinal())) != 0;
        }

        public static int getModesFromStack(ItemStack stack)
        {
            if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Mode"))
            {
                return stack.stackTagCompound.getInteger("Mode");
            }
            return 0;
        }

    }

    private static final IIcon[] ICONS = new IIcon[TransferUpgrade.VALUES.length];

    public ItemUpgrade() {
        setUnlocalizedName("upgrade");
        setHasSubtypes(true);
    }

    public String getInventoryName()
    {
        return "gui.title.filter.name";
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
            ICONS[upgrade.ordinal()] = register.registerIcon(UtilitiesInExcess.MODID + ":upgrade_" + upgrade.getName());
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
        if (!world.isRemote)
        {
            TransferUpgrade upgrade = TransferUpgrade.getUpgrade(stack);
            if (upgrade == TransferUpgrade.FILTER || upgrade == TransferUpgrade.ENDER_TRANSMITTER || upgrade == TransferUpgrade.ENDER_RECEIVER) // SERVER side only
            {
                GuiFactories.playerInventory().open(player, InventoryTypes.PLAYER, player.inventory.currentItem);
            }
            else if (upgrade == TransferUpgrade.ADV_FILTER)
            {
                AdvancedFilterMode mode;
                if (player.isSneaking())
                {
                    mode = cycleAdvancedFilter(stack, false);
                }
                else
                {
                    mode = cycleAdvancedFilter(stack, true);
                }
                player.addChatMessage(new ChatComponentText("Filter Mode: " + mode.getLabel()));
            }
        }
        return stack;
    }

    private AdvancedFilterMode cycleAdvancedFilter(ItemStack stack, boolean up)
    {
        if (stack.hasTagCompound())
        {
            if (stack.stackTagCompound.hasKey("AdvMode"))
            {
                if (up)
                {
                    stack.stackTagCompound.setByte("AdvMode", (byte) ((stack.stackTagCompound.getByte("AdvMode") + 1) % AdvancedFilterMode.values().length));
                }
                else
                {
                    byte mode = stack.stackTagCompound.getByte("AdvMode");
                    if (mode == 0)
                    {
                        stack.stackTagCompound.setByte("AdvMode", (byte) (AdvancedFilterMode.values().length - 1));
                    }
                    else
                    {
                        stack.stackTagCompound.setByte("AdvMode", (byte) (mode - 1));
                    }
                }
            }
            else
            {
                stack.stackTagCompound.setByte("AdvMode", (byte) 1);
            }
        }
        else
        {
            stack.stackTagCompound = new NBTTagCompound();
            stack.stackTagCompound.setByte("AdvMode", (byte) 0);
        }
        return AdvancedFilterMode.values()[stack.stackTagCompound.getByte("AdvMode")];
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        if (TransferUpgrade.getUpgrade(data.getPlayer().getHeldItem()) == TransferUpgrade.FILTER)
        {
            return buildFilterUI(data, syncManager, settings);
        }
        return buildWirelessUI(data, syncManager, settings);
    }

    private ModularPanel buildFilterUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        ModularPanel panel = new ModularPanel("panel").height(125);
        panel.bindPlayerInventory();

        int usedSlot = data.getPlayer().inventory.currentItem;
        if (data.getInventoryType() == InventoryTypes.PLAYER)
        {
            syncManager.bindPlayerInventory(data.getPlayer(),
                (inv, index) ->
                    index == usedSlot ? new ModularSlot(inv, index).accessibility(false, false) : new ModularSlot(inv, index));
        }

        SlotGroup filterGroup = new SlotGroup("filter_slots", 1);
        ItemStackInventory itemInventory;

        ItemStack heldStack = data.getPlayer().getHeldItem();
        itemInventory = new ItemStackInventory(heldStack, 9, 1);

        IItemHandler handler = new InvWrapper(itemInventory);

        panel.child(
            IKey.str(StatCollector.translateToLocal(getInventoryName()))
                .asWidget()
                .marginLeft(5)
                .marginRight(5)
                .marginTop(5)
                .marginBottom(-15)
        );

        Flow flow = Flow.row().size(18*9, 18).horizontalCenter().marginTop(16);

        for (int i = 0; i < this.getInventorySize(); i++)
        {
            flow.child(new PhantomItemSlot().slot(new ModularSlot(handler,i).slotGroup(filterGroup)));
        }

        panel.child(flow);
        syncManager.addCloseListener(itemInventory::writeInventoryToHeldStack);

        return panel;
    }

    private ModularPanel buildWirelessUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        ModularPanel panel = new ModularPanel("panel").height(125);
        panel.size(175, 50);
        Flow flow = Flow.row().width(160).height(18).center();
        flow.child(IKey.str("Frequency:").asWidget());

        ItemStack heldItem = data.getPlayer().getHeldItem();

        String initValue = "";
        if (heldItem != null && heldItem.hasTagCompound())
        {
            if (heldItem.getTagCompound().hasKey("Frequency"))
            {
                initValue =  heldItem.getTagCompound().getString("Frequency");
            }
        }

        StringValue stringValue = new StringValue(initValue);

        TextFieldWidget frequencyBox = new TextFieldWidget().width(80).value(stringValue);

        flow.child(frequencyBox);
        ButtonWidget<?> buttonWidget = new ButtonWidget<>();
        flow.child(
            buttonWidget
                .overlay(GuiTextures.CHECKMARK)
                .onMousePressed(mouseButton -> {
                    buttonWidget.playClickSound();
                    if (mouseButton == 0 || mouseButton == 1)
                    {
                        ItemStack itemToWriteTo = data.getPlayer().getHeldItem();
                        {
                            if (itemToWriteTo != null)
                            {
                                if (itemToWriteTo.hasTagCompound())
                                {
                                    itemToWriteTo.getTagCompound().setString("Frequency", frequencyBox.getText());
                                }
                                else
                                {
                                    NBTTagCompound newCompound = new NBTTagCompound();
                                    newCompound.setString("Frequency", frequencyBox.getText());
                                    itemToWriteTo.setTagCompound(newCompound);
                                }
                            }
                        }
                        return true;
                    }
                    return false;
                }));
        flow.childPadding(2);
        panel.child(flow);
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

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced)
    {
        addInformationHelper(stack, list, "", true);
    }

    @SuppressWarnings("unchecked")
    public void addInformationHelper(ItemStack stack, List list, String indent, boolean root)
    {
        if (TransferUpgrade.getUpgrade(stack) == TransferUpgrade.FILTER)
        {
            if (stack.hasTagCompound())
            {
                int filterMode = FilterMode.getModesFromStack(stack);
                if (filterMode != 0)
                {
                    for (int i = 0; i < 3; i++)
                    {
                        if ((filterMode & (1 << i)) != 0)
                        {
                            if (root)
                            {
                                list.add(indent + FilterMode.values()[i].getLabel());
                            }
                            else
                            {
                                list.add(indent + "  " + FilterMode.values()[i].getLabel());
                            }
                        }
                    }
                    if (!root)
                    {
                        indent += "  ";
                    }
                }

                if (stack.stackTagCompound.hasKey("Items"))
                {
                    NBTTagList tagList = stack.stackTagCompound.getTagList("Items", 10);
                    for (int i = 0; i < tagList.tagCount(); i++)
                    {
                        NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
                        ItemStack filteredStack = ItemStack.loadItemStackFromNBT(itemTag);
                        if (filteredStack != null) {
                            if (filteredStack.getItem() instanceof ItemUpgrade)
                            {
                                if (filteredStack.getItemDamage() == TransferUpgrade.FILTER.ordinal())
                                {
                                    list.add(indent + "  Item Filter");
                                    addInformationHelper(filteredStack, list, indent + "  ", false);
                                }
                                else if (filteredStack.getItemDamage() == TransferUpgrade.ADV_FILTER.ordinal())
                                {
                                    list.add(indent + "  AdvancedFilter: " + AdvancedFilterMode.values()[getAdvFilterMode(filteredStack)].getLabel());
                                }
                            }
                            else
                            {
                                list.add(indent + "  " + filteredStack.getDisplayName());
                            }
                        }
                    }
                }
            }
        }
        else if (TransferUpgrade.getUpgrade(stack) == TransferUpgrade.ADV_FILTER)
        {
            if (stack.hasTagCompound())
            {
                if (stack.stackTagCompound.hasKey("AdvMode"))
                {
                    byte advFilterMode = stack.stackTagCompound.getByte("AdvMode");
                    if (advFilterMode >= 0 && advFilterMode < AdvancedFilterMode.values().length)
                    {
                        list.add(AdvancedFilterMode.values()[advFilterMode].getLabel());
                        list.add(AdvancedFilterMode.values()[advFilterMode].getDescription());
                    }
                }
            }
        }
        else if (TransferUpgrade.getUpgrade(stack) == TransferUpgrade.ENDER_RECEIVER || TransferUpgrade.getUpgrade(stack) == TransferUpgrade.ENDER_TRANSMITTER)
        {
            if (stack.hasTagCompound())
            {
                if (stack.stackTagCompound.hasKey("Frequency"))
                {
                    list.add(stack.stackTagCompound.getString("Frequency"));
                }
            }
        }
    }
}


