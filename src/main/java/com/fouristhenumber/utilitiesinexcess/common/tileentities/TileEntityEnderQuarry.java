package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.joml.Vector2i;

public class TileEntityEnderQuarry extends TileEntity {

    public static int STEPS_PER_TICK = 40;
    public ForgeDirection facing;
    private Area2d workArea;
    public QuarryWorkState state;
    private int dx;
    private int dy;
    private int dz;
    private int chunkX;
    private int chunkZ;
    private int brokenBlocksTotal;

    public ForgeDirection getFacing() {
        return this.facing;
    }

    public void setFacing(ForgeDirection facing) {
        this.facing = facing;
    }

    public Area2d getWorkArea() {
        return this.workArea;
    }

    public void resetState() {
        brokenBlocksTotal = 0;
        this.state = QuarryWorkState.STOPPED;
    }

    public String getState() {
        return switch (state) {
            case RUNNING -> String.format("Quarry is currently mining at %d %d %d, has already mined %d", dx, dy, dz, brokenBlocksTotal);
            case STOPPED -> "Quarry is stopped.";
            case FINISHED -> String.format("Quarry has finished after mining %d blocks", brokenBlocksTotal);
        };
    }

    public void setWorkArea(Area2d area) {
        workArea = area;
        dx = area.low.x;
        dy = yCoord + 5;
        dz = area.low.y;
        chunkX = dx >> 4;
        chunkZ = dz >> 4;
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;
        if (facing == ForgeDirection.UNKNOWN) return;
        if (state != QuarryWorkState.RUNNING) return;

        int brokenBlocksTick = 0;

        while (brokenBlocksTick < STEPS_PER_TICK && stepPos()) {
            if (!isInBounds() || this.chunkX > 1000) {
                FMLLog.getLogger().error("Tried to quarry outside of work area at {} {} {}", dx, dy, dz);
                state = QuarryWorkState.FINISHED;
                return;
            }
            if (worldObj.isAirBlock(dx, dy, dz) || worldObj.getBlock(dx, dy, dz).getMaterial() == Material.air) {
                continue;
            }
            worldObj.setBlock(dx, dy, dz, Blocks.air);
            brokenBlocksTick++;
            // breakBlock()
        }
        if (brokenBlocksTick < STEPS_PER_TICK) {
            state = QuarryWorkState.FINISHED;
        }

        this.brokenBlocksTotal += brokenBlocksTick;
    }

    /**
     * Are the current dx & dy & dz in bounds
     */
    public boolean isInBounds() {
        return dy > 1 && this.workArea.isInBounds(dx, dz);
    }

    /**
     * Are the provided dx & dz in bounds
     */
    public boolean isInBounds(int dx, int dz) {
        return this.workArea.isInBounds(dx, dz);
    }

    /**
     * Step the quarry working position by one block
     * @return True if we can keep moving
     */
    public boolean stepPos() {
        dy--;
        if (dy <= 1) {
            // stack is done, move back up
            dy = this.yCoord + 5;

            boolean resetX = false;
            if (dx + 1 >> 4 == chunkX && dx + 1 <= workArea.high.x) {
                dx++;
            } else {
                if (dz + 1 >> 4 == chunkZ && dz + 1 <= workArea.high.y) {
                    dz++;
                    resetX = true;
                } else {
                    // next pos up z is a new chunk and maybe oob
                    if (dz + 1 <= workArea.high.y) {
                        // just the next chunk
                        dz++;
                        chunkZ++;
                        resetX = true;
                    } else {
                        // the next z slice
                        if (dx + 1 <= workArea.high.x) {
                            dx++;
                            chunkX++;
                            dz = workArea.low.y;
                            chunkZ = workArea.low.y >> 4;
                        } else {
                            // Finished with area
                            return false;
                        }
                    }
                }
            }

            if (resetX) {
                // need to move x back left towards/to bounds
                int toMove = dx + 1 > workArea.high.x ? workArea.chunkOffX.y : 15;
                if (dx - toMove >= workArea.low.x) {
                    dx -= toMove;
                } else {
                    dx = workArea.low.x;
                }
            }
        }
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        facing = ForgeDirection.getOrientation(nbt.getInteger("facing"));
        state = QuarryWorkState.values()[nbt.getInteger("state")];
        dx = nbt.getInteger("dy");
        dy = nbt.getInteger("dz");
        dz = nbt.getInteger("dx");
        chunkX = dx >> 4;
        chunkZ = dz >> 4;
        brokenBlocksTotal = nbt.getInteger("blocks");

        workArea = Area2d.fromNBTTag(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("facing", facing.ordinal());
        nbt.setInteger("state", state.ordinal());
        nbt.setInteger("dx", dx);
        nbt.setInteger("dy", dy);
        nbt.setInteger("dz", dz);
        nbt.setInteger("blocks", brokenBlocksTotal);
        workArea.writeNBTTag(nbt);
    }

    public enum QuarryWorkState {
        STOPPED,
        FINISHED,
        RUNNING;
    }

    public static class Area2d {
        // The corner that has the lower x&y
        public final Vector2i low;
        // The corner that has the higher x&y
        public final Vector2i high;
        // The width of the entire working area
        public final int width;
        // The height of the entire working area
        public final int height;
        /**
        * The relative x offset to the chunk grid,
         * with vec.x as the extra space to the left,
         * and vec.y as the extra space to the right
         * */
        public final Vector2i chunkOffX;
        /**
         * The relative x offset to the chunk grid,
         * with vec.x as the extra space to the bottom,
         * and vec.y as the extra space to the top
         * */
        public final Vector2i chunkOffZ;

        public Area2d(Vector2i first, Vector2i second) {
            int lowX = Math.min(first.x, second.x);
            int lowZ = Math.min(first.y, second.y);
            int highX = Math.max(first.x, second.x);
            int highZ = Math.max(first.y, second.y);
            this.low = new Vector2i(lowX, lowZ);
            this.high = new Vector2i(highX, highZ);
            this.width = highX - lowX;
            this.height = highZ - lowZ;
            this.chunkOffX = new Vector2i(15 - lowX % 16, highX % 16);
            this.chunkOffZ = new Vector2i(15 - lowZ % 16, highZ % 16);
        }

        public Area2d(int x1, int z1, int x2, int z2) {
            this(new Vector2i(x1, z1), new Vector2i(x2, z2));
        }

        public boolean isInBounds(int x, int z) {
            return x >= this.low.x && x <= this.high.x && z >= this.low.y && z <= this.high.y;
        }

        public void writeNBTTag(NBTTagCompound nbt) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("lowX", low.x);
            tag.setInteger("lowZ", low.y);
            tag.setInteger("highX", high.x);
            tag.setInteger("highY", high.y);

            nbt.setTag("area", tag);
        }

        public static Area2d fromNBTTag(NBTTagCompound nbt) {
            NBTTagCompound tag = nbt.getCompoundTag("area");
            int lowX =  tag.getInteger("lowX");
            int lowZ =  tag.getInteger("lowZ");
            int highX = tag.getInteger("highX");
            int highZ = tag.getInteger("highY");
            return new Area2d(lowX, lowZ, highX, highZ);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Area2d other)) return false;
            return low.equals(other.low) && high.equals(other.high);
        }
    }
}
