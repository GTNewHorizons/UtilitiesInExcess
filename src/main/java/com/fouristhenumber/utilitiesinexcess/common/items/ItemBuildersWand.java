package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;
import java.util.Set;

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

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.renderers.WireframeRenderer;
import com.fouristhenumber.utilitiesinexcess.utils.ChickenLibRayTracer;
import com.fouristhenumber.utilitiesinexcess.utils.InventoryBudget;
import com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil;
import com.fouristhenumber.utilitiesinexcess.utils.bw.BWCellHandlers;
import com.fouristhenumber.utilitiesinexcess.utils.bw.BWContext;
import com.fouristhenumber.utilitiesinexcess.utils.bw.BWMode;
import com.fouristhenumber.utilitiesinexcess.utils.bw.BWRegion;
import com.fouristhenumber.utilitiesinexcess.utils.bw.BWRegion.WandAxisLock;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBuildersWand extends Item implements ITranslucentItem {

    private final int buildLimit;

    public ItemBuildersWand(int buildLimit) {
        super();
        this.buildLimit = buildLimit;
        setUnlocalizedName("builders_wand");
        setMaxDamage(0);
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(
            EnumChatFormatting.AQUA
                + StatCollector.translateToLocalFormatted("uie.desc.item.builders_wand.1", this.buildLimit));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!world.isRemote || !(entity instanceof EntityPlayer player)) return;

        if (!isSelected) return;

        // adventure mode
        if (!player.capabilities.allowEdit) {
            WireframeRenderer.clearCandidatePositions();
            return;
        }

        MovingObjectPosition movingObjectPosition = Minecraft.getMinecraft().objectMouseOver;

        // Check if player is looking at a block.
        if (movingObjectPosition == null
            || movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            WireframeRenderer.clearCandidatePositions();
            return;
        }

        ForgeDirection forgeSide = ForgeDirection.getOrientation(movingObjectPosition.sideHit);

        WandAxisLock axisLock = axisLock(player);
        Set<BlockPos> blocksToPlace = BWRegion.findAdjacentBlocksToBuildOn(
            newContext(world, player, movingObjectPosition),
            buildLimit,
            movingObjectPosition,
            axisLock);

        WireframeRenderer.clearCandidatePositions();
        for (BlockPos pos : blocksToPlace)
            WireframeRenderer.addCandidatePosition(pos.offset(forgeSide.offsetX, forgeSide.offsetY, forgeSide.offsetZ));
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if (!player.capabilities.allowEdit) return false;

        MovingObjectPosition mop = new MovingObjectPosition(
            x,
            y,
            z,
            side,
            Vec3.createVectorHelper((double) x + hitX, (double) y + hitY, (double) z + hitZ));

        // Sanity check
        ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
        if (forgeSide == ForgeDirection.UNKNOWN) {
            UtilitiesInExcess.LOG.warn("Builder's wand onItemUse was called with invalid facing direction: {}", side);
            return true;
        }

        WandAxisLock axisLock = axisLock(player);

        // the scan pass spends the budget working out what fits, so placement starts from a fresh one
        BWContext scanCtx = newContext(world, player, mop);
        Set<BlockPos> blocksToPlace = BWRegion.findAdjacentBlocksToBuildOn(scanCtx, buildLimit, mop, axisLock);

        // every cell is protection-checked on its own, Forge recording the whole use at once would muddle that
        boolean capturing = world.captureBlockSnapshots;
        world.captureBlockSnapshots = false;
        try {
            BWContext buildCtx = newContext(world, player, mop);
            for (BlockPos srcPos : blocksToPlace) {
                MovingObjectPositionUtil.TranslateMovingObjectPositionToLocation(mop, srcPos);
                BWCellHandlers.build(buildCtx, mop, true);
            }
        } finally {
            world.captureBlockSnapshots = capturing;
        }
        player.inventoryContainer.detectAndSendChanges();
        return true;
    }

    /** Mode, budget and picker for one pass over the fill, seeded from the clicked cell. */
    private static BWContext newContext(World world, EntityPlayer player, MovingObjectPosition mop) {
        var budget = new InventoryBudget(player.inventory, player.capabilities.isCreativeMode);

        // null when the ray misses the clicked block, then handlers just get the plain position
        MovingObjectPosition tracedMop = ChickenLibRayTracer
            .retraceBlock(world, player, mop.blockX, mop.blockY, mop.blockZ);
        return new BWContext(
            world,
            tracedMop != null ? tracedMop : MovingObjectPositionUtil.copy(mop),
            player,
            BWMode.of(player),
            budget);
    }

    private static WandAxisLock axisLock(EntityPlayer player) {
        if (UtilitiesInExcess.proxy.BUILDERS_KEYBIND_H.isKeyDown(player)) return WandAxisLock.HORIZONTAL;
        if (UtilitiesInExcess.proxy.BUILDERS_KEYBIND_V.isKeyDown(player)) return WandAxisLock.VERTICAL;

        return WandAxisLock.FREE;
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
