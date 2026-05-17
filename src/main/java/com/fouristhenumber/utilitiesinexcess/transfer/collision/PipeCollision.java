package com.fouristhenumber.utilitiesinexcess.transfer.collision;

import net.minecraft.util.AxisAlignedBB;

public enum PipeCollision
{
    MIDDLE {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);

        @Override
        public AxisAlignedBB getBoundingBox() {
            return BOX;
        }
    },
    NORTH {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.375, 0.0, 0.625, 0.625, 0.375);

        @Override
        public AxisAlignedBB getBoundingBox() {
            return BOX;
        }
    },
    SOUTH {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.375, 0.625, 0.625, 0.625, 1.0);

        @Override
        public AxisAlignedBB getBoundingBox() {
            return BOX;
        }
    },
    EAST {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.625, 0.375, 0.375, 1.0, 0.625, 0.625);

        @Override
        public AxisAlignedBB getBoundingBox() {
            return BOX;
        }
    },
    WEST {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.0, 0.375, 0.375, 0.375, 0.625, 0.625);

        @Override
        public AxisAlignedBB getBoundingBox() {
            return BOX;
        }
    },
    UP {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.625, 0.375, 0.625, 1.0, 0.625);

        @Override
        public AxisAlignedBB getBoundingBox() {
            return BOX;
        }
    },
    DOWN {
        private static final AxisAlignedBB BOX =
            AxisAlignedBB.getBoundingBox(0.375, 0.0, 0.375, 0.625, 0.375, 0.625);

        @Override
        public AxisAlignedBB getBoundingBox() {
            return BOX;
        }
    };

    public abstract AxisAlignedBB getBoundingBox();
}
