package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;

import com.cleanroommc.modularui.utils.NumberFormat;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityDrum;

public class BlockDrum extends BlockContainer {

    final int capacity;

    public BlockDrum(int capacity) {
        super(Material.iron);
        this.capacity = capacity;
        setBlockName("drum");
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        setBlockTextureName("utilitiesinexcess:drum");
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
        FluidTank tank = null;
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) {
            return false;
        }

        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityDrum drum)) {
            return false;
        }

        if (!world.isRemote) {
            tank = drum.tank;
        }

        if (tank == null) {
            return true;
        }

        if (FluidContainerRegistry.isEmptyContainer(heldItem)) {
            FluidStack stored = tank.getFluid();
            if (stored == null || stored.amount <= 0) {
                return false;
            }

            FluidStack available = new FluidStack(stored.getFluid(), Math.min(1000, stored.amount));
            ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(available, heldItem);
            if (filledContainer == null) {
                return false;
            }

            FluidStack fluidInContainer = FluidContainerRegistry.getFluidForFilledItem(filledContainer);
            if (fluidInContainer == null) return false;

            tank.drain(fluidInContainer.amount, true);

            if (!player.capabilities.isCreativeMode) {
                if (heldItem.stackSize == 1) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, filledContainer);
                } else {
                    heldItem.stackSize--;
                    if (!player.inventory.addItemStackToInventory(filledContainer)) {
                        player.dropPlayerItemWithRandomChoice(filledContainer, false);
                    }

                }
                if (!world.isRemote) {
                    if (drum.tank.getFluid() == null) {
                        player.addChatMessage(new ChatComponentTranslation("tile.drum.chat.empty"));
                    } else {
                        player.addChatMessage(
                            new ChatComponentTranslation(
                                "tile.drum.chat.filled",
                                drum.tank.getFluid()
                                    .getLocalizedName(),
                                NumberFormat.DEFAULT.format(drum.tank.getFluid().amount)));
                    }
                }

                player.inventory.markDirty();
            }

            drum.markDirty();
            world.markBlockForUpdate(x, y, z);
            return true;
        }

        FluidStack fluid = null;
        Item item = heldItem.getItem();

        if (item instanceof IFluidContainerItem fluidContainer) {
            fluid = fluidContainer.getFluid(heldItem);
        } else {
            fluid = FluidContainerRegistry.getFluidForFilledItem(heldItem);
        }

        if (fluid != null && fluid.getFluid() != null) {
            int filled = tank.fill(fluid, true);
            if (filled > 0) {
                ItemStack emptyContainer = null;

                if (item instanceof IFluidContainerItem fluidContainer) {
                    fluidContainer.drain(heldItem, filled, true);
                    emptyContainer = heldItem;
                } else {
                    emptyContainer = FluidContainerRegistry.drainFluidContainer(heldItem);
                }

                if (!player.capabilities.isCreativeMode) {
                    if (heldItem.stackSize == 1) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, emptyContainer);
                    } else {
                        heldItem.stackSize--;
                        if (emptyContainer != null && !player.inventory.addItemStackToInventory(emptyContainer)) {
                            player.dropPlayerItemWithRandomChoice(emptyContainer, false);
                        }

                    }
                    if (world.isRemote) {
                        player.addChatMessage(
                            new ChatComponentTranslation(
                                "tile.drum.chat.filled",
                                drum.tank.getFluid()
                                    .getLocalizedName(),
                                NumberFormat.DEFAULT.format(drum.tank.getFluid().amount)));
                    }

                    player.inventory.markDirty();
                }

                drum.markDirty();
                world.markBlockForUpdate(x, y, z);
                return true;
            }
        }

        return false;
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
        // We spawn the correct stack on breakBlock(), make it not drop normally.
        return new ArrayList<>();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {

        ItemStack drop = new ItemStack(this, 1, meta);

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityDrum drum) {
            if (drum.tank.getFluid() != null) {
                ItemBlockDrum.setFluid(
                    drop,
                    drum.tank.getFluid()
                        .copy());
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
        world.removeTileEntity(x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    public static class ItemBlockDrum extends ItemBlock implements IFluidContainerItem {

        public final int capacity;

        public ItemBlockDrum(Block block) {
            super(block);
            this.setMaxStackSize(1);
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
            if (resource == null) {
                return 0;
            }

            FluidStack currentFluid = getFluid(stack);

            if (currentFluid == null) {
                int fillAmount = Math.min(capacity, resource.amount);
                if (doFill && fillAmount > 0) {
                    FluidStack newFluid = resource.copy();
                    newFluid.amount = fillAmount;
                    setFluid(stack, newFluid);
                }
                return fillAmount;
            } else {
                if (!currentFluid.isFluidEqual(resource)) {
                    return 0;
                }

                int space = capacity - currentFluid.amount;
                if (space <= 0) {
                    return 0;
                }

                int fillAmount = Math.min(space, resource.amount);
                if (doFill && fillAmount > 0) {
                    currentFluid.amount += fillAmount;
                    setFluid(stack, currentFluid);
                }
                return fillAmount;
            }
        }

        @Override
        public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
            FluidStack currentFluid = getFluid(stack);
            if (currentFluid == null) {
                return null;
            }

            int drained = Math.min(maxDrain, currentFluid.amount);
            FluidStack drainedFluid = currentFluid.copy();
            drainedFluid.amount = drained;

            if (doDrain) {
                currentFluid.amount -= drained;
                if (currentFluid.amount <= 0) {
                    stack.setTagCompound(null);
                } else {
                    setFluid(stack, currentFluid);
                }
            }
            return drainedFluid;
        }

        // Helper functions to abstract away the NBT layer
        public static void setFluid(ItemStack stack, FluidStack fluid) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
            }
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
                NBTTagCompound fluidTag = stack.getTagCompound()
                    .getCompoundTag("Fluid");
                return FluidStack.loadFluidStackFromNBT(fluidTag);
            }
            return null;
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip
                .add(StatCollector.translateToLocalFormatted("tile.drum.desc", NumberFormat.DEFAULT.format(capacity)));
            FluidStack fluid = getFluid(stack);
            if (fluid != null) {
                String formatted = StatCollector.translateToLocalFormatted(
                    "tile.drum.desc.fluid",
                    fluid.getLocalizedName(),
                    NumberFormat.DEFAULT.format(fluid.amount));
                tooltip.add(formatted);
            }
        }
    }

}
