package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.falsepattern.endlessids.mixin.helpers.SubChunkBlockHook;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockConveyor;
import com.fouristhenumber.utilitiesinexcess.compat.mui.paintroller.PaintRollerColorPickerDialog;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.network.client.PaintRollerColorSelect;
import com.fouristhenumber.utilitiesinexcess.utils.KeybindUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPaintRoller extends Item implements IGuiHolder<PlayerInventoryGuiData> {

    public ItemPaintRoller() {
        setUnlocalizedName("paint_roller");
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0b0_11111_11111_11111));
    }

    public IIcon featherIcon;
    public IIcon handleIcon;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister aIconRegister) {
        super.registerIcons(aIconRegister);
        featherIcon = aIconRegister.registerIcon(UtilitiesInExcess.MODID + ":paint_roller_overlay");
        handleIcon = aIconRegister.registerIcon(UtilitiesInExcess.MODID + ":paint_roller");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float clickX, float clickY, float clickZ) {
        Block block = world.getBlock(x, y, z);
        if (!(block instanceof BlockColored)) return false;

        int color = BlockColored.getEIDMetaFromRGB(getColorFromStack(stack));

        if (player.isSneaking()) {
            paintLine(player, world, x, y, z, color);
        } else {
            paintBlock(world, x, y, z, color);
        }

        return true;
    }

    private void paintBlock(World world, int x, int y, int z, int color) {
        // TODO cubic chunks compat
        ((SubChunkBlockHook) world.getChunkFromBlockCoords(x, z)
            .getBlockStorageArray()[y >> 4]).eid$setMetadata(x & 15, y & 15, z & 15, color);

        world.markBlockForUpdate(x, y, z);
    }

    private void paintLine(EntityPlayer player, World world, int x, int y, int z, int color) {
        ForgeDirection direction = BlockConveyor
            .getFacing((int) ((((player.rotationYaw % 360) + 45f) / 90f + 4f) % 4f));
        Block blockFirst = world.getBlock(x, y, z);

        for (int i = 0; i < 100; i++) {
            Block block = world
                .getBlock(x + (direction.offsetX * i), y + (direction.offsetY * i), z + (direction.offsetZ * i));

            if (block != blockFirst) return;

            paintBlock(
                world,
                x + (direction.offsetX * i),
                y + (direction.offsetY * i),
                z + (direction.offsetZ * i),
                color);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            GuiFactories.playerInventory()
                .openFromMainHand(player);
        }
        return itemStackIn;
    }

    public static int getColorFromStack(ItemStack stack) {
        int color;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("SelectedColor")) {
            color = 0xFFFFFF;
        } else {
            color = tag.getInteger("SelectedColor");
        }

        return color;
    }

    public static void setStackColor(ItemStack stack, int color) {
        NBTTagCompound newTag = stack.getTagCompound();
        if (newTag == null) {
            newTag = new NBTTagCompound();
        }
        newTag.setInteger("SelectedColor", color);
        stack.setTagCompound(newTag);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ItemStack stack = data.getUsedItemStack();

        PaintRollerColorPickerDialog colorPickerDialog = new PaintRollerColorPickerDialog(
            newColor -> PacketHandler.INSTANCE.sendToServer(new PaintRollerColorSelect(newColor)),
            getColorFromStack(stack));

        return colorPickerDialog;
    }

    @Override
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        String rightClickName = KeybindUtils
            .getKeyDisplayNameWithMouse(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode());
        String middleClickName = KeybindUtils
            .getKeyDisplayNameWithMouse(Minecraft.getMinecraft().gameSettings.keyBindPickBlock.getKeyCode());
        tooltip.add(StatCollector.translateToLocalFormatted("item.paint_roller.desc.0", rightClickName));
        tooltip.add(StatCollector.translateToLocalFormatted("item.paint_roller.desc.1", rightClickName));
        tooltip.add(StatCollector.translateToLocalFormatted("item.paint_roller.desc.2", rightClickName));
        tooltip.add(StatCollector.translateToLocalFormatted("item.paint_roller.desc.3", middleClickName));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }
}
