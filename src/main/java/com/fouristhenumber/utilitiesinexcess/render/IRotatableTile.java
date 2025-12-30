package com.fouristhenumber.utilitiesinexcess.render;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

//remove when GTNHLib models are ready
public interface IRotatableTile {

    /**
     * Sets the primary block-facing direction.
     * This is a full 6-way direction using ForgeDirection:
     * UP, DOWN, NORTH, SOUTH, EAST, WEST.
     */
    void setFacing(ForgeDirection facing);

    /**
     * @return The primary 6-way facing direction.
     */
    ForgeDirection getFacing();

    /**
     * Sets the secondary yaw direction.
     * This must be one of the 4 horizontal ForgeDirections:
     * NORTH, SOUTH, EAST, WEST.
     *
     * NOTE:
     * These are NOT encoded as 0–3 like vanilla block rotations.
     * ForgeDirection uses:
     * NORTH = 2
     * SOUTH = 3
     * WEST = 4
     * EAST = 5
     *
     * This interface converts yaw → 0–3 manually using convertYawToRotation().
     */
    void setYaw(ForgeDirection yaw);

    /**
     * Gets the 4-way yaw direction (N, S, E, W).
     */
    ForgeDirection getYaw();

    /**
     * Returns the Block icon for the given side.
     * Override if the tile needs per-side icons.
     */
    default IIcon getIconForSide(Block block, ForgeDirection side, int meta) {
        return (side == this.getFacing()) ? block.getIcon(3, meta) : block.getIcon(0, meta);
    }

    /**
     * Computes the texture rotation for the given side.
     *
     * Only UP and DOWN faces are rotated, and only when the block-facing
     * is vertical (UP or DOWN). When on the ceiling the yaw is inverted.
     *
     * The returned rotation is a 0–3 value suitable for texture rotation:
     * 0 = no rotation
     * 1 = 90° clockwise
     * 2 = 180°
     * 3 = 270°
     *
     * This rotation value is NOT based on ForgeDirection.ordinal().
     */
    default ForgeDirection getRotationForSide(ForgeDirection side, int meta) {
        if (side != ForgeDirection.UP && side != ForgeDirection.DOWN) {
            return ForgeDirection.NORTH;
        }

        ForgeDirection facing = this.getFacing();
        ForgeDirection yaw = this.getYaw();

        if (facing == ForgeDirection.UP || facing == ForgeDirection.DOWN) {

            ForgeDirection effectiveYaw = yaw;

            if (facing == ForgeDirection.UP) {
                effectiveYaw = yaw.getOpposite();
            }

            return effectiveYaw;
        }

        return ForgeDirection.NORTH;
    }
}
