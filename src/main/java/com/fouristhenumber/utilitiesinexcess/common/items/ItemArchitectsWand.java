package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.utils.ArchitectsWandUtils.getItemStackToPlace;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.renderers.WireframeRenderer;
import com.fouristhenumber.utilitiesinexcess.utils.ArchitectsWandUtils;
import com.github.bsideup.jabel.Desugar;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArchitectsWand extends Item {

    public int buildLimit;

    public ItemArchitectsWand(int buildLimit) {
        super();
        this.buildLimit = buildLimit;
        setUnlocalizedName("architects_wand");
        setMaxDamage(0);
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(
            EnumChatFormatting.AQUA
                + StatCollector.translateToLocalFormatted("tooltip.architects_wand.1", this.buildLimit));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @Desugar
    @SideOnly(Side.CLIENT)
    record RenderContext(BlockPos target, ForgeDirection side, ItemStack stack, int placeCount, Vec3 playerPosition) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RenderContext other)) return false;

            return placeCount == other.placeCount && target.equals(other.target)
                && side == other.side
                && ItemStack.areItemStacksEqual(stack, other.stack)
                && playerPosition.xCoord == other.playerPosition.xCoord
                && playerPosition.yCoord == other.playerPosition.yCoord
                && playerPosition.zCoord == other.playerPosition.zCoord;
        }

    }

    private RenderContext oldContext;

    @SideOnly(Side.CLIENT)
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!world.isRemote || !(entity instanceof EntityPlayer player)) {
            return;
        }

        if (!isSelected) {
            oldContext = null;
            return;
        }

        // I'm pretty sure this will never determine whether we render or not but I'm not certain
        MovingObjectPosition movingObjectPosition = Minecraft.getMinecraft().objectMouseOver;

        // Check if player is looking at a block.
        if (movingObjectPosition == null
            || movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            WireframeRenderer.clearCandidatePositions();
            oldContext = null;
            return;
        }

        // 1. Target block location
        BlockPos target = new BlockPos(
            movingObjectPosition.blockX,
            movingObjectPosition.blockY,
            movingObjectPosition.blockZ);

        // 2. Side
        int side = movingObjectPosition.sideHit;
        ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
        if (forgeSide == ForgeDirection.UNKNOWN) {
            UtilitiesInExcess.LOG
                .warn("Architect wand onUpdate was called with invalid facing direction: {}", forgeSide);
            return;
        }

        // 4. Target block to place
        ItemStack itemStackToPlace = getItemStackToPlace(world, target, movingObjectPosition, player);
        if (itemStackToPlace == null || !(itemStackToPlace.getItem() instanceof ItemBlock)) {
            WireframeRenderer.clearCandidatePositions();
            oldContext = null;
            return;
        }

        // 3. Total amount to place
        int placeCount = player.capabilities.isCreativeMode ? this.buildLimit
            : Math.min(ArchitectsWandUtils.countItemInInventory(player, itemStackToPlace), this.buildLimit);

        // Check the context to see if we really do need to recompute. Don't do it every frame.
        // Reasons to recompute:
        // 1. Target block location is different
        // 2. Side is different
        // 3. Total Amount we can place is different
        // 4. Target block is different
        // 5. Player location is different (comes from function args)
        RenderContext newContext = new RenderContext(
            target,
            forgeSide,
            itemStackToPlace,
            placeCount,
            player.getPosition(1.0F));
        if (newContext.equals(oldContext)) {
            return;
        }
        oldContext = newContext;

        Set<BlockPos> blocksToPlace = ArchitectsWandUtils
            .findAdjacentBlocks(world, itemStackToPlace, placeCount, forgeSide, target, movingObjectPosition, player);
        WireframeRenderer.clearCandidatePositions();
        for (BlockPos pos : blocksToPlace) {
            WireframeRenderer.addCandidatePosition(pos.offset(forgeSide.offsetX, forgeSide.offsetY, forgeSide.offsetZ));
        }
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // TODO: Prevent player from placing blocks into themself / other entities?
        ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
        if (forgeSide == ForgeDirection.UNKNOWN) {
            UtilitiesInExcess.LOG
                .warn("Architects wand onItemUse was called with invalid facing direction: {}", forgeSide);
            return false;
        }

        BlockPos target = new BlockPos(x, y, z);
        MovingObjectPosition mop = new MovingObjectPosition(x, y, z, side, Vec3.createVectorHelper(hitX, hitY, hitZ));
        ItemStack itemStackToPlace = getItemStackToPlace(world, target, mop, player);
        if (itemStackToPlace == null || !(itemStackToPlace.getItem() instanceof ItemBlock)) return false;

        // This block is here because some mods want to use TEs to
        Block comparisonBlock = world.getBlock(x, y, z);
        int comparisonMeta = world.getBlockMetadata(x, y, z);
        ItemStack comparisonItemStack = new ItemStack(comparisonBlock, 1, comparisonMeta);

        boolean useCompatPlacement = !ItemStack.areItemStacksEqual(itemStackToPlace, comparisonItemStack);

        int inventoryBlockCount = ArchitectsWandUtils.countItemInInventory(player, itemStackToPlace);
        if (!player.capabilities.isCreativeMode && inventoryBlockCount == 0) return false;
        int placeCount = player.capabilities.isCreativeMode ? this.buildLimit
            : Math.min(inventoryBlockCount, this.buildLimit);

        Set<BlockPos> blocksToPlace = ArchitectsWandUtils
            .findAdjacentBlocks(world, itemStackToPlace, placeCount, forgeSide, target, mop, player);
        itemStackToPlace.stackSize = blocksToPlace.size(); // Since now, we actually create a stack we have to set the
                                                           // size. Strange kinda...

        for (BlockPos pos : blocksToPlace) {
            // TODO: Group these by a bigger number instead of decreasing by 1 every time.
            if (player.capabilities.isCreativeMode
                || ArchitectsWandUtils.decreaseFromInventory(player, itemStackToPlace)) {
                if (useCompatPlacement) {
                    itemStackToPlace.getItem()
                        .onItemUse(itemStackToPlace, player, world, pos.x, pos.y, pos.z, side, hitX, hitY, hitZ);
                } else {
                    world.setBlock(
                        pos.x + forgeSide.offsetX,
                        pos.y + forgeSide.offsetY,
                        pos.z + forgeSide.offsetZ,
                        comparisonBlock,
                        comparisonMeta,
                        3);
                }
            }
        }
        return true;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

}
