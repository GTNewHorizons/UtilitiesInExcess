package com.fouristhenumber.utilitiesinexcess.transfer.collision;

import net.minecraft.util.AxisAlignedBB;

public enum PipeCollision
{
    MIDDLE(-1) {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);

        @Override
        public AxisAlignedBB getCollisionBox() {
            return BOX;
        }
    },
    DOWN(0) {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.0, 0.375, 0.625, 0.375, 0.625);

        @Override
        public AxisAlignedBB getCollisionBox() {
            return BOX;
        }
    },
    UP(1) {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.625, 0.375, 0.625, 1.0, 0.625);

        @Override
        public AxisAlignedBB getCollisionBox() {
            return BOX;
        }
    },
    NORTH(2) {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.375, 0.0, 0.625, 0.625, 0.375);

        @Override
        public AxisAlignedBB getCollisionBox() {
            return BOX;
        }
    },
    SOUTH(3) {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.375, 0.625, 0.625, 0.625, 1.0);

        @Override
        public AxisAlignedBB getCollisionBox() {
            return BOX;
        }
    },
    WEST(4) {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.0, 0.375, 0.375, 0.375, 0.625, 0.625);

        @Override
        public AxisAlignedBB getCollisionBox() {
            return BOX;
        }
    },
    EAST(5) {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.625, 0.375, 0.375, 1.0, 0.625, 0.625);

        @Override
        public AxisAlignedBB getCollisionBox() {
            return BOX;
        }
    };

    private final int maskBit;

    PipeCollision(int maskBit)
    {
        this.maskBit = maskBit;
    }

    public int getMaskBit()
    {
        return maskBit;
    }

    public abstract AxisAlignedBB getCollisionBox();
}
