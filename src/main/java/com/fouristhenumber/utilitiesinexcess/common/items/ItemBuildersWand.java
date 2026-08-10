package com.fouristhenumber.utilitiesinexcess.common.items;

import static com.fouristhenumber.utilitiesinexcess.utils.BuildersWandUtils.damageBackhand;

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
import com.fouristhenumber.utilitiesinexcess.config.items.BuildersWandsConfig;
import com.fouristhenumber.utilitiesinexcess.utils.BuildersBlockPicker;
import com.fouristhenumber.utilitiesinexcess.utils.BuildersBlockSelectionFilter;
import com.fouristhenumber.utilitiesinexcess.utils.BuildersMaterialBudget;
import com.fouristhenumber.utilitiesinexcess.utils.BuildersWandUtils;
import com.fouristhenumber.utilitiesinexcess.utils.BuildersWandUtils.WandAxisMode;
import com.fouristhenumber.utilitiesinexcess.utils.MovingObjectPositionUtil;
import com.gtnewhorizon.gtnhlib.api.ITranslucentItem;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBuildersWand extends Item implements ITranslucentItem {

    public int buildLimit;

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

        // I'm pretty sure this will never determine whether we render or not but I'm not certain
        MovingObjectPosition movingObjectPosition = Minecraft.getMinecraft().objectMouseOver;

        // Check if player is looking at a block.
        if (movingObjectPosition == null
            || movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            WireframeRenderer.clearCandidatePositions();
            return;
        }

        ForgeDirection forgeSide = ForgeDirection.getOrientation(movingObjectPosition.sideHit);

        WandAxisMode axisMode;
        if (UtilitiesInExcess.proxy.BUILDERS_KEYBIND_H.isKeyDown(player)) {
            axisMode = WandAxisMode.HORIZONTAL;
        } else if (UtilitiesInExcess.proxy.BUILDERS_KEYBIND_V.isKeyDown(player)) {
            axisMode = WandAxisMode.VERTICAL;
        } else {
            axisMode = WandAxisMode.FREE;
        }

        // selection filter
        var filter = new BuildersBlockSelectionFilter(player, world, movingObjectPosition);

        // keep track of potentially used blocks in inventory
        var itemBudget = new BuildersMaterialBudget(player.inventory, player.capabilities.isCreativeMode);

        // block picker
        var blockPicker = BuildersBlockPicker.create(world, player, filter, itemBudget);

        Set<BlockPos> blocksToPlace = BuildersWandUtils
            .findAdjacentBlocks(world, buildLimit, movingObjectPosition, player, filter, blockPicker, axisMode);

        WireframeRenderer.clearCandidatePositions();
        for (BlockPos pos : blocksToPlace)
            WireframeRenderer.addCandidatePosition(pos.offset(forgeSide.offsetX, forgeSide.offsetY, forgeSide.offsetZ));
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        MovingObjectPosition mop = new MovingObjectPosition(
            x,
            y,
            z,
            side,
            Vec3.createVectorHelper(x + hitX, y + hitY, z + hitZ));

        // Sanity check
        ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
        if (forgeSide == ForgeDirection.UNKNOWN) {
            UtilitiesInExcess.LOG.warn("Builder's wand onItemUse was called with invalid facing direction: {}", side);
            return true;
        }

        WandAxisMode axisMode;
        if (UtilitiesInExcess.proxy.BUILDERS_KEYBIND_H.isKeyDown(player)) {
            axisMode = WandAxisMode.HORIZONTAL;
        } else if (UtilitiesInExcess.proxy.BUILDERS_KEYBIND_V.isKeyDown(player)) {
            axisMode = WandAxisMode.VERTICAL;
        } else {
            axisMode = WandAxisMode.FREE;
        }

        // selection filter
        var filter = new BuildersBlockSelectionFilter(player, world, mop);

        // keep track of potentially used blocks in inventory
        var itemBudget = new BuildersMaterialBudget(player.inventory, player.capabilities.isCreativeMode);

        // block picker
        var blockPicker = BuildersBlockPicker.create(world, player, filter, itemBudget);

        // potential block positions
        Set<BlockPos> blocksToPlace = BuildersWandUtils
            .findAdjacentBlocks(world, buildLimit, mop, player, filter, blockPicker, axisMode);

        // reset the blockPicker with new budget
        itemBudget = new BuildersMaterialBudget(player.inventory, player.capabilities.isCreativeMode);
        blockPicker = BuildersBlockPicker.create(world, player, filter, itemBudget);

        for (BlockPos pos : blocksToPlace) {
            MovingObjectPositionUtil.TranslateMovingObjectPositionToLocation(mop, pos);
            ItemStack toPlace = blockPicker
                .pickBlock(mop, BuildersBlockSelectionFilter.getBlockByLocation(world, mop, player));

            if (toPlace == null) continue;

            if (damageBackhand(BuildersWandsConfig.INSTANCE.damageTrowelWithBuildersWand, player)) {

                ItemStack itemCopy = toPlace.copy();
                itemCopy.stackSize = 1;

                // uses ItemBlock to place the block with all the checks
                // sets stackSize to 0 on success
                itemCopy.tryPlaceItemIntoWorld(player, world, pos.x, pos.y, pos.z, side, hitX, hitY, hitZ);

                // Don't forget to take the spent item from the inventory
                if (itemCopy.stackSize == 0 && !player.capabilities.isCreativeMode)
                    BuildersWandUtils.decreaseFromInventory(player, toPlace);
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
