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
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.chromatictooltips.ColorChromaticTooltip;
import com.fouristhenumber.utilitiesinexcess.compat.endlessids.EIDsHelper;
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
        setContainerItem(this);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0b0_11111_11111_11111));
    }

    public IIcon handleIcon;
    public IIcon featherIcon;
    public IIcon starIcon;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister aIconRegister) {
        handleIcon = aIconRegister.registerIcon(UtilitiesInExcess.MODID + ":paint_roller");
        featherIcon = aIconRegister.registerIcon(UtilitiesInExcess.MODID + ":paint_roller_overlay");
        starIcon = aIconRegister.registerIcon(UtilitiesInExcess.MODID + ":paint_roller_overlay_star");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float clickX, float clickY, float clickZ) {
        if (!BlockColored.allowDyingBlocks()) return false;

        boolean paintStripper = getPaintStripperFromStack(stack);
        Block block = world.getBlock(x, y, z);
        int currentMeta = world.getBlockMetadata(x, y, z);
        BlockColored.BaseBlock newBlock = getNewBlock(paintStripper, block, currentMeta);
        if (newBlock == null || newBlock.getBlock() == null) return false;
        BlockColored blockColored = newBlock.getBlock() instanceof BlockColored ? (BlockColored) newBlock.getBlock()
            : BlockColored.getColoredVersion(newBlock.getBlock(), newBlock.getMeta());
        if (blockColored == null) return false;
        boolean needsIDChange = newBlock.getBlock() != block;

        int color = blockColored.usesExtraBit() ? BlockColored.getEIDMetaFromRGBWithExtraBit(getColorFromStack(stack))
            : BlockColored.getEIDMetaFromRGB(getColorFromStack(stack));

        if (player.isSneaking()) {
            paintLine(player, world, block, currentMeta, x, y, z, paintStripper, color);
        } else {
            paintBlock(
                world,
                needsIDChange ? Block.getIdFromBlock(newBlock.getBlock()) : -1,
                x,
                y,
                z,
                paintStripper ? newBlock.getMeta() : color);
        }

        return true;
    }

    private BlockColored.BaseBlock getNewBlock(boolean paintStripper, Block currentBlock, int currentMeta) {
        if (paintStripper) {
            if (currentBlock instanceof BlockColored cbc) {
                return cbc.getBase();
            } else {
                return BlockColored.baseOf(currentBlock, currentMeta);
            }
        } else {
            if (currentBlock instanceof BlockColored) {
                return BlockColored.baseOf(currentBlock, currentMeta);
            } else {
                return BlockColored.baseOf(BlockColored.getColoredVersion(currentBlock, currentMeta), currentMeta);
            }
        }
    }

    private void paintBlock(World world, int setBlockID, int x, int y, int z, int color) {
        // There isn't exactly a reason why we have to do this, but I don't want to fire block updates twice by using
        // the vanilla method for setting the block. So set the id directly, then set the meta directly, then fire a
        // block update
        if (setBlockID != -1) {
            EIDsHelper.setBlockID(world, x, y, z, setBlockID);
        }
        EIDsHelper.setBlockMeta(world, x, y, z, color);

        world.markBlockForUpdate(x, y, z);
    }

    private void paintLine(EntityPlayer player, World world, Block startBlock, int startMeta, int x, int y, int z,
        boolean paintStripper, int color) {
        ForgeDirection lookSide;
        Vec3 look = player.getLookVec();
        double absX = Math.abs(look.xCoord);
        double absY = Math.abs(look.yCoord);
        double absZ = Math.abs(look.zCoord);
        if (absX > absY && absX > absZ) {
            lookSide = look.xCoord > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
        } else if (absY > absX && absY > absZ) {
            lookSide = look.yCoord > 0 ? ForgeDirection.UP : ForgeDirection.DOWN;
        } else {
            lookSide = look.zCoord > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
        }

        int blockID = -1;
        BlockColored.BaseBlock newBlock = getNewBlock(paintStripper, startBlock, startMeta);
        for (int i = 0; i < 100; i++) {
            Block block = world
                .getBlock(x + (lookSide.offsetX * i), y + (lookSide.offsetY * i), z + (lookSide.offsetZ * i));

            if (block != startBlock && !(block instanceof BlockColored bc && bc.getBase()
                .getBlock() == startBlock)
                && !(startBlock instanceof BlockColored bc2 && bc2.getBase()
                    .getBlock() == block)) {
                return;
            }

            int blockIDTemp = -1;
            if (block != newBlock.getBlock()) {
                if (blockID == -1) {
                    blockID = Block.getIdFromBlock(newBlock.getBlock());
                }
                blockIDTemp = blockID;
            }
            paintBlock(
                world,
                blockIDTemp,
                x + (lookSide.offsetX * i),
                y + (lookSide.offsetY * i),
                z + (lookSide.offsetZ * i),
                paintStripper ? newBlock.getMeta() : color);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!BlockColored.allowDyingBlocks()) return itemStackIn;

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

    public static boolean getPaintStripperFromStack(ItemStack stack) {
        boolean paintStripper;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("PaintStripper")) {
            paintStripper = false;
        } else {
            paintStripper = tag.getBoolean("PaintStripper");
        }

        return paintStripper;
    }

    public static void setStackColor(ItemStack stack, int color, boolean paintStripper) {
        NBTTagCompound newTag = stack.getTagCompound();
        if (newTag == null) {
            newTag = new NBTTagCompound();
        }
        newTag.setInteger("SelectedColor", color);
        newTag.setBoolean("PaintStripper", paintStripper);
        stack.setTagCompound(newTag);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ItemStack stack = data.getUsedItemStack();

        PaintRollerColorPickerDialog colorPickerDialog = new PaintRollerColorPickerDialog(
            (newColor, paintStripper) -> PacketHandler.INSTANCE
                .sendToServer(new PaintRollerColorSelect(newColor & 0xFFFFFF, paintStripper)),
            getColorFromStack(stack),
            getPaintStripperFromStack(stack));

        return colorPickerDialog;
    }

    @Override
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (BlockColored.allowDyingBlocks()) {
            if (getPaintStripperFromStack(stack)) {
                tooltip.add(StatCollector.translateToLocal("uie.gui.text.paint_roller.paintstripper"));
            } else {
                int color = getColorFromStack(stack);
                if (color != 0xF8F8F8) {
                    if (Mods.ChromaticTooltips.isLoaded()) {
                        tooltip.add(ColorChromaticTooltip.makeTooltip(color));
                    } else {
                        tooltip.add("#" + Color.rgbToFullHexString(color));
                    }
                }
            }
            String rightClickName = KeybindUtils
                .getKeyDisplayNameWithMouse(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode());
            String middleClickName = KeybindUtils
                .getKeyDisplayNameWithMouse(Minecraft.getMinecraft().gameSettings.keyBindPickBlock.getKeyCode());
            tooltip.add(StatCollector.translateToLocalFormatted("uie.desc.item.paint_roller.0", rightClickName));
            tooltip.add(StatCollector.translateToLocalFormatted("uie.desc.item.paint_roller.1", rightClickName));
            tooltip.add(StatCollector.translateToLocalFormatted("uie.desc.item.paint_roller.2", rightClickName));
            tooltip.add(StatCollector.translateToLocalFormatted("uie.desc.item.paint_roller.3", middleClickName));
        }
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    // For crafting
    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack;
    }
}
