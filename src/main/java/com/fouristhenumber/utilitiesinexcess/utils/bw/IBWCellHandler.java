package com.fouristhenumber.utilitiesinexcess.utils.bw;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import org.jetbrains.annotations.Nullable;

/**
 * Teaches the Builder's Wand to extend one kind of cell.
 *
 * <h3>What one use of the wand does</h3>
 * <ol>
 * <li>flood fills across the blocks that are already there, in the plane of the clicked face: the source cells
 * <li>offers every source cell to the handlers in registration order, first claim wins, the rest go to
 * {@link DefaultBWCellHandler}
 * <li>the handler identifies the source cell and, unless the mode paints over anything, compares it with the
 * clicked cell
 * <li>the handler builds onto the clicked face of that source cell, into the empty cell in front of it
 * </ol>
 *
 * <h3>Which cell is which</h3>
 * <ul>
 * <li>{@code mop}: the source cell being handled, the block that gets built onto
 * <li>{@code MovingObjectPositionUtil.targetPos(mop)}: the target cell in front of it, where the new block lands
 * <li>{@code ctx.originalMop}: the cell the player clicked, the same one all use long
 * <li>{@code ctx.clickedBlockStack}: what that original clicked cell hands over when picked
 * </ul>
 *
 * <h3>Registering</h3> During init, behind your own mod check:
 *
 * <pre>
 * if (Mods.YourMod.isLoaded()) BWCellHandlers.register(new YourCellHandler());
 * </pre>
 *
 * <h3>Example</h3> A block whose tile entity holds state the item does not carry:
 *
 * <pre>
 * public final class MyCellHandler extends DefaultBWCellHandler {
 *
 *     &#64;Override
 *     public boolean handles(BWContext ctx, int x, int y, int z) {
 *         return ctx.world.getTileEntity(x, y, z) instanceof MyTile;
 *     }
 *
 *     &#64;Override
 *     public boolean build(BWContext ctx, MovingObjectPosition mop, boolean place) {
 *         if (!super.build(ctx, mop, place)) return false; // ordinary placement into the target cell
 *         if (place) copyMyState(ctx.world, blockPos(mop), targetPos(mop));
 *         return true;
 *     }
 * }
 * </pre>
 *
 * Real examples:
 * <ul>
 * <li>ArchitectureCraftBWCellHandler: the example above, plus the cladding it costs declared in {@code extraCosts}
 * <li>MultipartBWCellHandler: replaces {@link #build} with one branch per mode, because a cell holding a bag of
 * parts is no single block with a single item
 * </ul>
 *
 * <h3>Rules</h3>
 * <ul>
 * <li>handlers are registered once and shared by the client and the server thread, so they must be stateless
 * <li>everything belonging to one use lives on {@link BWContext}: world, player, mode, budget, clicked cell
 * <li>reserve materials through {@code ctx.budget} and pay through {@code ctx.spend}, or the wireframe promises
 * cells the player cannot afford
 * <li>place through {@code tryPlaceItemIntoWorld()} so Forge can veto a cell in a forbidden area,
 * rather than circumventing it with direct {@code setBlock()}
 * </ul>
 *
 * @see DefaultBWCellHandler which lists what to override when extending it
 */
public interface IBWCellHandler {

    /**
     * Whether this handler wants the cell at the given position. The mode may be part of the answer: declining
     * hands the cell to the next handler, and finally to {@link DefaultBWCellHandler}.
     */
    boolean handles(BWContext ctx, int x, int y, int z);

    /**
     * The stack that stands for this cell: what the wand looks for in the inventory, what it places, and what the
     * clicked cell is compared against. Null when the cell cannot be expressed as one stack.
     * Usually {@code block.getPickBlock()}.
     */
    @Nullable
    ItemStack pickBlockAt(BWContext ctx, MovingObjectPosition mop);

    /**
     * Report what this cell costs and, when asked, build it.
     * <ul>
     * <li>runs once to work out the fill, every tick on the client for the wireframe, and once to place it
     * <li>both passes must answer alike, so the player gets what the wireframe showed
     * <li>while {@code place} is false nothing in the world may change
     * <li>Reserve materials through {@code ctx.budget}
     * <li>Pay for the materials through {@code ctx.spend}
     * </ul>
     *
     * @param mop   the source cell to build onto
     * @param place false to only reserve and report, true to also build and pay for the materials
     * @return true if
     *         this cell is part of the fill,
     *         block can be placed and materials were reserved,
     *         && (!place || build successfully)
     */
    boolean build(BWContext ctx, MovingObjectPosition mop, boolean place);
}
