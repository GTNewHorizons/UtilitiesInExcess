package com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.base;

import java.util.function.Predicate;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockFilingCabinet;
import com.fouristhenumber.utilitiesinexcess.config.blocks.FilingCabinetsConfig;
import com.fouristhenumber.utilitiesinexcess.render.IRotatableTile;

public abstract class TileFilingCabinetBase extends TileEntity implements IGuiHolder<PosGuiData>, IRotatableTile {

    protected final Predicate<ItemStack> upgradeMatcher;
    private int upgradeCount;

    private ForgeDirection yaw = ForgeDirection.NORTH; // 4-way spin
    private ForgeDirection facing = ForgeDirection.NORTH; // 6-way placement

    TileFilingCabinetBase(Predicate<ItemStack> upgradeMatcher) {
        this.upgradeMatcher = upgradeMatcher;

    }

    public abstract BlockFilingCabinet.Type getCabinetType();

    public abstract Predicate<ItemStack> extractMatcher(ItemStack stack);

    @Override
    public void setFacing(ForgeDirection facing) {
        if (facing != null && facing != ForgeDirection.UNKNOWN) {
            this.facing = facing;
        }
    }

    @Override
    public ForgeDirection getFacing() {
        return this.facing;
    }

    @Override
    public void setYaw(ForgeDirection yaw) {
        if (yaw != null && yaw.offsetY == 0) {
            this.yaw = yaw;
        }
    }

    @Override
    public ForgeDirection getYaw() {
        return this.yaw;
    }

    public int getNumberOfUpgrades() {
        return upgradeCount;
    }

    public boolean installCapacityUpgrade(World world, EntityPlayer player, ItemStack stack) {
        if (stack != null && upgradeMatcher.test(stack)) {
            IChatComponent msg;
            if (upgradeCount < FilingCabinetsConfig.upgradeCountMax) {
                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize -= 1;
                }
                upgradeCount += 1;
                markDirty();
                world.playSoundAtEntity(player, "random.levelup", 1F, 1F);
                msg = new ChatComponentTranslation("tile.filing_cabinet.capacity_upgrade.success");
                msg.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN));
            } else {
                world.playSoundAtEntity(player, "random.levelup", 1F, 1F);
                msg = new ChatComponentTranslation("tile.filing_cabinet.capacity_upgrade.maxed");
                msg.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED));
            }
            player.addChatComponentMessage(msg);
            return true;
        }
        return false;
    }

    public void dropInventory(World world, int x, int y, int z) {
        if (this instanceof TileFilingCabinetBaseItem item) for (int i = 0; i < item.inventory.getSlots(); i++) {
            ItemStack stack = item.inventory.getStackInSlot(i);
            if (stack != null) {
                int maxSize = stack.getMaxStackSize();
                while (stack.stackSize > maxSize) {
                    dropItemStack(world, x, y, z, ItemHandlerHelper.copyStackWithSize(stack, maxSize));
                    stack.stackSize -= maxSize;
                }
                dropItemStack(world, x, y, z, stack);
            }
        }
        int upgrades = upgradeCount;
        if (upgrades > 0) {
            // WorldUtils.dropItem(world, pos, ModItems.Type.CAPACITY_UPGRADE.newStack(upgrades));
            // todo change to use a proper upgrade
            dropItemStack(world, x, y, z, ModItems.INVERTED_INGOT.newItemStack(upgrades));
        }
    }

    public static void dropItemStack(World world, int x, int y, int z, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0 || world.isRemote) return;

        float offsetX = world.rand.nextFloat() * 0.6F + 0.1F;
        float offsetY = world.rand.nextFloat() * 0.6F + 0.1F;
        float offsetZ = world.rand.nextFloat() * 0.6F + 0.1F;

        EntityItem entityItem = new EntityItem(world, x + offsetX, y + offsetY, z + offsetZ, stack.copy());

        if (stack.hasTagCompound()) {
            entityItem.getEntityItem()
                .setTagCompound(
                    (NBTTagCompound) stack.getTagCompound()
                        .copy());
        }

        float factor = 0.025F;
        entityItem.motionX = world.rand.nextGaussian() * factor;
        entityItem.motionY = world.rand.nextGaussian() * factor + 0.1F;
        entityItem.motionZ = world.rand.nextGaussian() * factor;

        world.spawnEntityInWorld(entityItem);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return buildUICabinet(data, syncManager, settings);
    }

    public abstract ModularPanel buildUICabinet(PosGuiData data, PanelSyncManager syncManager, UISettings settings);

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("UpgradeCount", upgradeCount);
        compound.setByte("Yaw", (byte) this.yaw.ordinal());
        compound.setByte("Facing", (byte) this.facing.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        upgradeCount = compound.getInteger("UpgradeCount");
        this.yaw = ForgeDirection.getOrientation(compound.getByte("Yaw"));
        this.facing = ForgeDirection.getOrientation(compound.getByte("Facing"));
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

}
