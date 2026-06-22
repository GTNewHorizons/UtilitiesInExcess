package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import com.cleanroommc.modularui.utils.NumberFormat;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityDrum;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

public class BlockDrum extends BlockContainer {

    final int capacity;

    public BlockDrum(int capacity, String blockname) {
        super(Material.iron);
        this.capacity = capacity;
        setBlockName(blockname);
        this.setHardness(1.5F);
        this.setHarvestLevel("pickaxe", 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityDrum(capacity);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityDrum drum)) {
            return false;
        }

        ItemStack heldItem = player.getCurrentEquippedItem();

        if (heldItem != null) {
            // weird modded containers like universal cells
            if (heldItem.getItem() instanceof IFluidContainerItem item) {
                return handleIFluidContainerItem(drum, item, heldItem);
            }
            // fixed size containers
            if (FluidContainerRegistry.isFilledContainer(heldItem)) {
                return handleFilledRegistryContainer(drum, player, heldItem);
            }
            // empty containers
            if (FluidContainerRegistry.isEmptyContainer(heldItem)) {
                return handleEmptyRegistryContainer(drum, player, heldItem);
            }
        }

        FluidStack fluid = drum.tank.getFluid();
        player.addChatMessage(
            new ChatComponentTranslation(
                "tile.drum.desc",
                fluid == null ? StatCollector.translateToLocalFormatted("tile.drum.desc.empty")
                    : fluid.getLocalizedName(),
                fluid == null ? 0 : NumberFormat.DEFAULT.format(fluid.amount),
                NumberFormat.DEFAULT.format(capacity)));

        return false;
    }

    private boolean handleIFluidContainerItem(TileEntityDrum drum, IFluidContainerItem heldItem,
        ItemStack heldItemStack) {
        FluidStack containerFluid = heldItem.getFluid(heldItemStack);

        if (containerFluid != null && containerFluid.amount > 0) {
            int accepted = drum.fill(ForgeDirection.UP, containerFluid, false);
            if (accepted <= 0) return false;

            FluidStack toTransfer = containerFluid.copy();
            toTransfer.amount = accepted;

            FluidStack drained = heldItem.drain(heldItemStack, accepted, false);
            if (drained == null || drained.amount != accepted) return false;

            heldItem.drain(heldItemStack, accepted, true);
            drum.fill(ForgeDirection.UP, toTransfer, true);

        } else {
            FluidStack inTank = drum.tank.getFluid();
            if (inTank == null || inTank.amount <= 0) return false;

            FluidStack simDrain = drum.drain(ForgeDirection.UP, heldItem.getCapacity(heldItemStack), false);
            if (simDrain == null || simDrain.amount <= 0) return false;

            int accepted = heldItem.fill(heldItemStack, simDrain, false);
            if (accepted <= 0) return false;

            FluidStack toTransfer = simDrain.copy();
            toTransfer.amount = accepted;

            drum.drain(ForgeDirection.UP, accepted, true);
            heldItem.fill(heldItemStack, toTransfer, true);

        }
        return true;
    }

    private boolean handleFilledRegistryContainer(TileEntityDrum drum, EntityPlayer player, ItemStack heldItem) {
        FluidStack containerFluid = FluidContainerRegistry.getFluidForFilledItem(heldItem);
        if (containerFluid == null) return false;

        int accepted = drum.fill(ForgeDirection.UP, containerFluid, false);
        if (accepted != containerFluid.amount) return false;

        ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(heldItem);
        if (emptyContainer == null) return false;

        drum.fill(ForgeDirection.UP, containerFluid, true);

        giveResultStack(player, heldItem, emptyContainer);

        return true;
    }

    private boolean handleEmptyRegistryContainer(TileEntityDrum drum, EntityPlayer player, ItemStack heldItem) {
        FluidStack inTank = drum.tank.getFluid();
        if (inTank == null || inTank.amount <= 0) return false;

        int containerCapacity = FluidContainerRegistry.getContainerCapacity(inTank, heldItem);
        if (containerCapacity <= 0) return false;

        FluidStack simDrain = drum.drain(ForgeDirection.UP, containerCapacity, false);
        if (simDrain == null || simDrain.amount < containerCapacity) return false;

        ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(simDrain, heldItem);
        if (filledContainer == null) return false;

        drum.drain(ForgeDirection.UP, simDrain.amount, true);

        giveResultStack(player, heldItem, filledContainer);

        return true;
    }

    private void giveResultStack(EntityPlayer player, ItemStack sourceStack, ItemStack result) {
        int heldSlot = player.inventory.currentItem;

        if (sourceStack.stackSize == 1) {
            player.inventory.setInventorySlotContents(heldSlot, result);
        } else {
            player.inventory.decrStackSize(heldSlot, 1);
            if (!player.inventory.addItemStackToInventory(result)) {
                player.worldObj
                    .spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, result));
            }
        }
        // fix desyncs
        if (player instanceof EntityPlayerMP playerMP) {
            playerMP.mcServer.getConfigurationManager()
                .syncPlayerInventory(playerMP);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityDrum drum) {
            FluidStack fluid = ItemBlockDrum.getFluidFromStack(stack);
            drum.setFluid(fluid);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        ItemStack drop = new ItemStack(this, 1, meta);

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityDrum drum) {
            FluidStack fluid = drum.tank.getFluid();
            if (fluid != null) {
                ItemBlockDrum.setFluid(drop, fluid.copy());
            } else {
                ItemBlockDrum.clearFluid(drop);
            }
        }

        float f = 0.7F;
        double dx = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        double dy = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        double dz = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;

        EntityItem entityItem = new EntityItem(world, x + dx, y + dy, z + dz, drop);
        world.spawnEntityInWorld(entityItem);

        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    public static class ItemBlockDrum extends ItemBlock implements IFluidContainerItem {

        public final int capacity;

        public ItemBlockDrum(Block block) {
            super(block);
            this.capacity = ((BlockDrum) block).capacity;
        }

        @Override
        public FluidStack getFluid(ItemStack stack) {
            return getFluidFromStack(stack);
        }

        @Override
        public int getCapacity(ItemStack stack) {
            return capacity;
        }

        @Override
        public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
            if (resource == null || resource.amount <= 0) return 0;

            FluidStack currentFluid = getFluid(stack);

            if (currentFluid == null) {
                int fillAmount = Math.min(capacity, resource.amount);
                if (doFill && fillAmount > 0) {
                    FluidStack newFluid = resource.copy();
                    newFluid.amount = fillAmount;
                    setFluid(stack, newFluid);
                }
                return fillAmount;
            }

            if (!currentFluid.isFluidEqual(resource)) return 0;

            int space = capacity - currentFluid.amount;
            if (space <= 0) return 0;

            int fillAmount = Math.min(space, resource.amount);
            if (doFill && fillAmount > 0) {
                currentFluid.amount += fillAmount;
                setFluid(stack, currentFluid);
            }
            return fillAmount;
        }

        @Override
        public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
            FluidStack currentFluid = getFluid(stack);
            if (currentFluid == null || currentFluid.amount <= 0) return null;

            int drained = Math.min(maxDrain, currentFluid.amount);
            if (drained <= 0) return null;

            FluidStack drainedFluid = currentFluid.copy();
            drainedFluid.amount = drained;

            if (doDrain) {
                currentFluid.amount -= drained;
                if (currentFluid.amount <= 0) {
                    clearFluid(stack);
                } else {
                    setFluid(stack, currentFluid);
                }
            }
            return drainedFluid;
        }

        public static void setFluid(ItemStack stack, FluidStack fluid) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) tag = new NBTTagCompound();
            NBTTagCompound fluidTag = new NBTTagCompound();
            fluid.writeToNBT(fluidTag);
            tag.setTag("Fluid", fluidTag);
            stack.setTagCompound(tag);
        }

        public static void clearFluid(ItemStack stack) {
            stack.setTagCompound(null);
        }

        public static FluidStack getFluidFromStack(ItemStack stack) {
            if (stack.hasTagCompound() && stack.getTagCompound()
                .hasKey("Fluid")) {
                return FluidStack.loadFluidStackFromNBT(
                    stack.getTagCompound()
                        .getCompoundTag("Fluid"));
            }
            return null;
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            FluidStack fluid = getFluid(stack);
            String fluidName = fluid == null ? StatCollector.translateToLocalFormatted("tile.drum.desc.empty")
                : fluid.getLocalizedName();
            tooltip.add(
                StatCollector.translateToLocalFormatted(
                    "tile.drum.desc",
                    fluidName,
                    fluid == null ? 0 : NumberFormat.DEFAULT.format(fluid.amount),
                    NumberFormat.DEFAULT.format(capacity)));
        }
    }
}
