package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.utils.BuilderWandUtils;
import com.fouristhenumber.utilitiesinexcess.utils.BuilderWandUtils.WandBlockPos;

public class ItemBuilderWand extends Item {

    public int buildLimit = -1;

    public ItemBuilderWand(int buildLimit) {
        super();
        this.buildLimit = buildLimit;
        setTextureName("utilitiesinexcess:builderWand");
        setUnlocalizedName("builderWand");
        setMaxDamage(0);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + "Assists in placing many blocks");
        tooltip.add(EnumChatFormatting.AQUA + "Can place up to " + this.buildLimit + " blocks at once");
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // TODO: Prevent player from placing blocks into themself / other entities?
        ForgeDirection forgeSide = ForgeDirection.getOrientation(side);
        if (forgeSide == ForgeDirection.UNKNOWN) {
            UtilitiesInExcess.LOG.warn("Builder wand was called with invalid facing direction: {}", forgeSide);
            return false;
        }

        Block blockToPlace = world.getBlock(x, y, z);
        int metaToPlace = world.getBlockMetadata(x, y, z);
        ItemStack itemStack = new ItemStack(Item.getItemFromBlock(blockToPlace), 1, metaToPlace);
        if (blockToPlace == null) return false;

        int inventoryBlockCount = BuilderWandUtils.countItemInInventory(player, itemStack);
        int placeCount = player.capabilities.isCreativeMode ? this.buildLimit
            : Math.min(inventoryBlockCount, this.buildLimit);

        Set<WandBlockPos> blocksToPlace = BuilderWandUtils
            .findAdjacentBlocks(world, blockToPlace, metaToPlace, placeCount, forgeSide, new WandBlockPos(x, y, z));

        for (WandBlockPos pos : blocksToPlace) {
            // TODO: Group these by a bigger number instead of decreasing by 1 every time.
            if (player.capabilities.isCreativeMode || BuilderWandUtils.decreaseFromInventory(player, itemStack)) {
                world.setBlock(
                    pos.x + forgeSide.offsetX,
                    pos.y + forgeSide.offsetY,
                    pos.z + forgeSide.offsetZ,
                    blockToPlace,
                    metaToPlace,
                    3);
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
