package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;
import java.util.List;

import com.fouristhenumber.utilitiesinexcess.config.blocks.DrumConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
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

        TileEntity te = world.getTileEntity(x, y, z);

        // all the drum things in variables so i can just ref them later xd
        if (!(te instanceof TileEntityDrum drum)) return false;
        FluidTank drumTank = drum.tank;
        FluidStack fluid = drumTank.getFluid();
        int capacity = drumTank.getCapacity();

        ItemStack itemStack = player.inventory.getCurrentItem();
        if (itemStack == null) {
            if (world.isRemote) {
                return false;
            }

            // Print drum capacity to player chat
                player.addChatComponentMessage(
                    new ChatComponentTranslation(
                        "%s %s",
                             NumberFormat.DEFAULT.format(drumTank.getFluidAmount()),
                        DrumConfig.unitToDisplay ? "mB" : "L"
                    )
                );

            return true;
        }
        Item item = itemStack.getItem();

        if (fluid == null) {
            // empty drum cases
            if (item instanceof IFluidContainerItem fluidTank) {
                FluidStack playerFluid = fluidTank.getFluid(itemStack);
                int playerCapacity = fluidTank.getCapacity(itemStack);

                if (playerFluid == null) {
                    // both the tank and the drum are false
                    return true;
                } else {
                    // either the capacity of the drum, or the amount in the player hand
                    int fillAmount = Math.min(capacity, playerFluid.amount);
                    // copy the fluid add hte amount and fill the drum and empty the hand
                    FluidStack fillFluid = playerFluid.copy();
                    fillFluid.amount = fillAmount;
                    int filedAmount = drumTank.fill(fillFluid, true);
                    fluidTank.drain(itemStack, filedAmount, true);

                    // todo: pull this to its own helper method?
                    if (!world.isRemote) {
                        player.addChatComponentMessage(
                            new ChatComponentTranslation(
                                "message.drum.filled",
                                filedAmount,
                                playerFluid.getLocalizedName()));
                    }

                    world.markBlockForUpdate(x, y, z);
                    return true;
                }

            }
            // empty and full bucket, maybe can pull this out to a helper method as well?
            else if (item instanceof ItemBucket) {

                FluidStack bucketFluid = FluidContainerRegistry.getFluidForFilledItem(itemStack);
                if (bucketFluid == null) {
                    return false;
                }
                // this SHOULD be 1000 i THINK
                int fillAmount = Math.min(capacity, bucketFluid.amount);
                FluidStack fillFluid = bucketFluid.copy();
                fillFluid.amount = fillAmount;
                int filedAmount = drumTank.fill(fillFluid, true);

                // if the drum works?
                if (filedAmount > 0) {

                    if (!world.isRemote) {

                        consumeItemStack(player, itemStack, new ItemStack(Items.bucket));

                        player.addChatComponentMessage(
                            new ChatComponentTranslation(
                                "message.drum.filled",
                                fillAmount,
                                fillFluid.getLocalizedName()));
                    }
                    world.markBlockForUpdate(x, y, z);

                }

            }
        }
        // if the drum has any fluid at all
        else {
            if (item instanceof IFluidContainerItem fluidItem) {

                FluidStack itemFluid = fluidItem.getFluid(itemStack);

                if (itemFluid != null && !itemFluid.isFluidEqual(fluid)) {
                    return false;
                }

                int space = fluidItem.getCapacity(itemStack) - (itemFluid == null ? 0 : itemFluid.amount);

                if (space <= 0) return false;
                int transfer = Math.min(space, fluid.amount);
                FluidStack transferFluid = fluid.copy();
                transferFluid.amount = transfer;
                ItemStack single = itemStack.copy();
                single.stackSize = 1;

                int filled = fluidItem.fill(single, transferFluid, true);
                if (filled <= 0) return false;

                drumTank.drain(filled, true);

                if (!world.isRemote) {
                    consumeItemStack(player, itemStack, single);

                    player.addChatComponentMessage(
                        new ChatComponentTranslation("message.drum.drained", filled, fluid.getLocalizedName()));
                }

                world.markBlockForUpdate(x, y, z);
                return true;

            }

            else if (item instanceof ItemBucket) {

                FluidStack bucketFluid = FluidContainerRegistry.getFluidForFilledItem(itemStack);
                if (bucketFluid != null) {
                    if (fluid != null && !fluid.isFluidEqual(bucketFluid)) {
                        return false;
                    }
                    int fill = drumTank.fill(bucketFluid.copy(), false);

                    if (fill < FluidContainerRegistry.BUCKET_VOLUME) {
                        return false;
                    }

                    drumTank.fill(bucketFluid.copy(), true);

                    if (!world.isRemote) {
                        player.inventory
                            .setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));

                        player.addChatComponentMessage(
                            new ChatComponentTranslation(
                                "message.drum.filled",
                                FluidContainerRegistry.BUCKET_VOLUME,
                                bucketFluid.getLocalizedName()));
                    }

                    world.markBlockForUpdate(x, y, z);
                    return true;
                }
                if (fluid == null || fluid.amount < FluidContainerRegistry.BUCKET_VOLUME) {
                    return true;
                }

                FluidStack take = fluid.copy();
                take.amount = FluidContainerRegistry.BUCKET_VOLUME;

                ItemStack filledBucket = FluidContainerRegistry.fillFluidContainer(take, itemStack);
                if (filledBucket == null) return true;

                if (!world.isRemote) {
                    consumeItemStack(player, itemStack, filledBucket);

                    player.addChatComponentMessage(
                        new ChatComponentTranslation(
                            "message.drum.drained",
                            FluidContainerRegistry.BUCKET_VOLUME,
                            fluid.getLocalizedName()));
                }

                drumTank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);
                world.markBlockForUpdate(x, y, z);
                return true;

            }

        }
        return true;
    }

    private static void consumeItemStack(EntityPlayer player, ItemStack hand, ItemStack result) {
        if (hand.stackSize == 1) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, result);
        } else {
            hand.stackSize--;
            if (!player.inventory.addItemStackToInventory(result)) {
                player.dropPlayerItemWithRandomChoice(result, false);
            }
        }
        player.inventoryContainer.detectAndSendChanges();
        player.inventory.markDirty();

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
