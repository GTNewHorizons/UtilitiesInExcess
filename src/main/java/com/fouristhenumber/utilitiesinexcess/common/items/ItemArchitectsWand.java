package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig.damageTrowelWithArchitectsWand;
import static com.fouristhenumber.utilitiesinexcess.utils.ArchitectsWandUtils.damageBackhand;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.renderers.WireframeRenderer;
import com.fouristhenumber.utilitiesinexcess.utils.ArchitectsSelection;
import com.fouristhenumber.utilitiesinexcess.utils.ArchitectsWandUtils;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArchitectsWand extends Item implements ITranslucentItem {

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

    @SideOnly(Side.CLIENT)
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!world.isRemote || !(entity instanceof EntityPlayer player)) {
            return;
        }

        if (!isSelected) {
            return;
        }

        // I'm pretty sure this will never determine whether we render or not but I'm not certain
        MovingObjectPosition movingObjectPosition = Minecraft.getMinecraft().objectMouseOver;

        // Check if player is looking at a block.
        if (movingObjectPosition == null
            || movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            WireframeRenderer.clearCandidatePositions();
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
        ArchitectsSelection selection = new ArchitectsSelection(player, world, movingObjectPosition);
        List<ItemStack> itemStackToPlace = selection.blockToPlace(player);

        // 3. Total amount to place
        int placeCount = selection.maxPlaceCount(player, buildLimit);

        Set<BlockPos> blocksToPlace = ArchitectsWandUtils.findAdjacentBlocks(
            world,
            itemStackToPlace,
            placeCount,
            forgeSide,
            target,
            movingObjectPosition,
            player,
            selection);
        WireframeRenderer.clearCandidatePositions();
        for (BlockPos pos : blocksToPlace) {
            WireframeRenderer.addCandidatePosition(pos.offset(forgeSide.offsetX, forgeSide.offsetY, forgeSide.offsetZ));
        }
    }

    private void placeBlock(World world, EntityPlayer player, @NotNull ItemStack itemStack, BlockPos pos, int side,
        float hitX, float hitY, float hitZ, ForgeDirection forgeSide) {

        // This block is here because some mods want to use TEs to
        ItemStack itemCopy = itemStack.copy();
        itemCopy.stackSize = 1;
        Block comparisonBlock = world.getBlock(pos.x, pos.y, pos.z);
        int comparisonMeta = world.getBlockMetadata(pos.x, pos.y, pos.z);
        ItemStack comparisonItemStack = new ItemStack(comparisonBlock, 1, comparisonMeta);

        boolean useCompatPlacement = !ItemStack.areItemStacksEqual(itemCopy, comparisonItemStack);
        if (useCompatPlacement) {
            itemStack.getItem()
                .onItemUse(itemCopy, player, world, pos.x, pos.y, pos.z, side, hitX, hitY, hitZ);
        } else {
            Block block = Block.getBlockFromItem(itemCopy.getItem());
            world.setBlock(
                pos.x + forgeSide.offsetX,
                pos.y + forgeSide.offsetY,
                pos.z + forgeSide.offsetZ,
                block,
                comparisonMeta,
                3);

            world.playSoundEffect(
                pos.x + forgeSide.offsetX,
                pos.y + forgeSide.offsetY,
                pos.z + forgeSide.offsetZ,
                block.stepSound.func_150496_b(),
                (block.stepSound.getVolume() + 1.0F) / 2.0F,
                block.stepSound.getPitch() * 0.8F);
        }
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        // TODO: Prevent player from placing blocks into themself / other entities?
        ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
        if (forgeSide == ForgeDirection.UNKNOWN) {
            UtilitiesInExcess.LOG
                .warn("Architects wand onItemUse was called with invalid facing direction: {}", forgeSide);
            return false;
        }

        BlockPos target = new BlockPos(x, y, z);
        MovingObjectPosition mop = new MovingObjectPosition(x, y, z, side, Vec3.createVectorHelper(hitX, hitY, hitZ));
        ArchitectsSelection selection = new ArchitectsSelection(player, world, mop);

        List<ItemStack> itemStackToPlace = selection.blockToPlace(player);

        int placeCount = selection.maxPlaceCount(player, buildLimit);

        Set<BlockPos> blocksToPlace = ArchitectsWandUtils
            .findAdjacentBlocks(world, itemStackToPlace, placeCount, forgeSide, target, mop, player, selection);

        ItemStack nowPlacing;
        int backhandDamage = 0;
        for (BlockPos pos : blocksToPlace) {
            List<ItemStack> candidates = selection.blockToPlace(player);
            if (candidates.size() == 1) {
                nowPlacing = candidates.get(0);
            } else {
                nowPlacing = candidates.get(
                    ThreadLocalRandom.current()
                        .nextInt(candidates.size()));
            }

            if (player.capabilities.isCreativeMode || (ArchitectsWandUtils.decreaseFromInventory(player, nowPlacing)
                && damageBackhand(damageTrowelWithArchitectsWand, player))) {
                placeBlock(world, player, nowPlacing, pos, side, hitX, hitY, hitZ, forgeSide);
            }
        }
        player.inventoryContainer.detectAndSendChanges();
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
