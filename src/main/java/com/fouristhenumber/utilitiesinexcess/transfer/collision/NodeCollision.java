package com.fouristhenumber.utilitiesinexcess.transfer.collision;

import net.minecraft.util.AxisAlignedBB;

public enum NodeCollision
{
    DOWN {
        private static final AxisAlignedBB[] COLLISION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.0625, 0.0, 0.0625, 0.9375, 0.0625, 0.9375),
                // Middle
                AxisAlignedBB.getBoundingBox(0.1875, 0.0, 0.1875, 0.8125, 0.25, 0.8125),
                // Top
                AxisAlignedBB.getBoundingBox(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875)
            };

        // These need to be different because the side of the occlusion box for parts needs to be slightly smaller
        // on the bottom to allow for covers.
        private static final AxisAlignedBB[] PART_OCCLUSION_BOX =
            {
                AxisAlignedBB.getBoundingBox(0.125, 0.0,    0.125, 0.875, 0.0625, 0.875),
                AxisAlignedBB.getBoundingBox(0.25,  0.0,    0.25, 0.75,  0.25,   0.75),
                AxisAlignedBB.getBoundingBox(0.375, 0.0,    0.375, 0.625, 0.375,  0.625)
            };

        private static final AxisAlignedBB BOUNDING_BOX = AxisAlignedBB.getBoundingBox(0.0625, 0.0, 0.0625, 0.9375, 0.375, 0.9375);


        @Override
        public AxisAlignedBB[] getCollisionBoxes() {
            return COLLISION_BOX;
        }

        @Override
        public AxisAlignedBB getBoundingBox()
        {
            return BOUNDING_BOX;
        }

        @Override
        public AxisAlignedBB[] getOcclusionBoxes()
        {
            return PART_OCCLUSION_BOX;
        }
    },
    UP {
        private static final AxisAlignedBB[] COLLISION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.0625, 0.9375, 0.0625, 0.9375, 1.0, 0.9375),
                // Middle
                AxisAlignedBB.getBoundingBox(0.1875, 0.75, 0.1875, 0.8125, 1.0, 0.8125),
                // Top
                AxisAlignedBB.getBoundingBox(0.3125, 0.625, 0.3125, 0.6875, 1.0, 0.6875)
            };

        private static final AxisAlignedBB[] PART_OCCLUSION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.125, 0.9375, 0.125, 0.875, 1.0, 0.875),
                // Middle
                AxisAlignedBB.getBoundingBox(.25, 0.75, .25, .75, 1.0, .75),
                // Top
                AxisAlignedBB.getBoundingBox(.375, 0.625, .375, .75, 1.0, .75)
            };

        private static final AxisAlignedBB BOUNDING_BOX = AxisAlignedBB.getBoundingBox(0.0625, 0.6875, 0.0625, 0.9375, 1.0, 0.9375);

        @Override
        public AxisAlignedBB[] getCollisionBoxes() {
            return COLLISION_BOX;
        }

        @Override
        public AxisAlignedBB getBoundingBox()
        {
            return BOUNDING_BOX;
        }

        @Override
        public AxisAlignedBB[] getOcclusionBoxes()
        {
            return PART_OCCLUSION_BOX;
        }
    },
    NORTH {
        private static final AxisAlignedBB[] COLLISION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.0625, 0.0625, 0.0, 0.9375, 0.9375, 0.0625),
                // Middle
                AxisAlignedBB.getBoundingBox(0.1875, 0.1875, 0.0, 0.8125, 0.8125, 0.25),
                // Top
                AxisAlignedBB.getBoundingBox(0.3125, 0.3125, 0.0, 0.6875, 0.6875, 0.375)
            };

        private static final AxisAlignedBB[] PART_OCCLUSION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.125, 0.125, 0.0, 0.875, 0.875, 0.0625),
                // Middle
                AxisAlignedBB.getBoundingBox(0.25, 0.25, 0.0, 0.75, .75, 0.25),
                // Top
                AxisAlignedBB.getBoundingBox(0.375, 0.375, 0.0, 0.625, 0.625, 0.375)
            };

        private static final AxisAlignedBB BOUNDING_BOX = AxisAlignedBB.getBoundingBox(0.0625, 0.0625, 0.0, 0.9375, 0.9375, 0.375);

        @Override
        public AxisAlignedBB[] getCollisionBoxes() {
            return COLLISION_BOX;
        }

        @Override
        public AxisAlignedBB getBoundingBox()
        {
            return BOUNDING_BOX;
        }

        @Override
        public AxisAlignedBB[] getOcclusionBoxes()
        {
            return PART_OCCLUSION_BOX;
        }
    },
    SOUTH {
        private static final AxisAlignedBB[] COLLISION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.0625, 0.0625, 0.9375, 0.9375, 0.9375, 1.0),
                // Middle
                AxisAlignedBB.getBoundingBox(0.1875, 0.1875, 0.75, 0.8125, 0.8125, 1.0),
                // Top
                AxisAlignedBB.getBoundingBox(0.3125, 0.3125, 0.625, 0.6875, 0.6875, 1.0)
            };

        private static final AxisAlignedBB[] PART_OCCLUSION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.125, 0.125, 0.9375, 0.875, 0.875, 1.0),
                // Middle
                AxisAlignedBB.getBoundingBox(0.25, 0.25, 0.75, 0.75, 0.75, 1.0),
                // Top
                AxisAlignedBB.getBoundingBox(0.375, 0.375, 0.625, 0.625, 0.625, 1.0)
            };

        private static final AxisAlignedBB BOUNDING_BOX = AxisAlignedBB.getBoundingBox(0.0625, 0.0625, 0.625, 0.9375, 0.9375, 1.0);

        @Override
        public AxisAlignedBB[] getCollisionBoxes() {
            return COLLISION_BOX;
        }

        @Override
        public AxisAlignedBB getBoundingBox()
        {
            return BOUNDING_BOX;
        }

        @Override
        public AxisAlignedBB[] getOcclusionBoxes()
        {
            return PART_OCCLUSION_BOX;
        }
    },
    WEST {
        private static final AxisAlignedBB[] COLLISION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.0, 0.0625, 0.0625, 0.0625, 0.9375, 0.9375),
                // Middle
                AxisAlignedBB.getBoundingBox(0.0, 0.1875, 0.1875, 0.25, 0.8125, 0.8125),
                // Top
                AxisAlignedBB.getBoundingBox(0.0, 0.3125, 0.3125, 0.375, 0.6875, 0.6875)
            };

        private static final AxisAlignedBB[] PART_OCCLUSION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.0, 0.125, 0.125, 0.0625, 0.875, 0.875),
                // Middle
                AxisAlignedBB.getBoundingBox(0.0, 0.25, 0.25, 0.25, 0.75, 0.75),
                // Top
                AxisAlignedBB.getBoundingBox(0.0, 0.375, 0.375, 0.375, 0.625, 0.625)
            };

        private static final AxisAlignedBB BOUNDING_BOX = AxisAlignedBB.getBoundingBox(0.0, 0.0625, 0.0625, 0.375, 0.9375, 0.9375);

        @Override
        public AxisAlignedBB[] getCollisionBoxes() {
            return COLLISION_BOX;
        }

        @Override
        public AxisAlignedBB getBoundingBox()
        {
            return BOUNDING_BOX;
        }

        @Override
        public AxisAlignedBB[] getOcclusionBoxes()
        {
            return PART_OCCLUSION_BOX;
        }
    },
    EAST {
        private static final AxisAlignedBB[] COLLISION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.9375, 0.0625, 0.0625, 1.0, 0.9375, 0.9375),
                // Middle
                AxisAlignedBB.getBoundingBox(0.75, 0.1875, 0.1875, 1.0, 0.8125, 0.8125),
                // Top
                AxisAlignedBB.getBoundingBox(0.625, 0.3125, 0.3125, 1.0, 0.6875, 0.6875)
            };

        private static final AxisAlignedBB[] PART_OCCLUSION_BOX =
            {
                // Bottom
                AxisAlignedBB.getBoundingBox(0.9375, 0.125, 0.125, 1.0, 0.875, 0.875),
                // Middle
                AxisAlignedBB.getBoundingBox(0.75, 0.25, 0.25, 1.0, 0.8125, 0.75),
                // Top
                AxisAlignedBB.getBoundingBox(0.625, 0.375, 0.375, 1.0, 0.625, 0.625)
            };

        private static final AxisAlignedBB BOUNDING_BOX = AxisAlignedBB.getBoundingBox(0.625, 0.0625, 0.0625, 1.0, 0.9375, 0.9375);

        @Override
        public AxisAlignedBB[] getCollisionBoxes() {
            return COLLISION_BOX;
        }

        @Override
        public AxisAlignedBB getBoundingBox()
        {
            return BOUNDING_BOX;
        }

        @Override
        public AxisAlignedBB[] getOcclusionBoxes()
        {
            return PART_OCCLUSION_BOX;
        }
    };

    public abstract AxisAlignedBB[] getCollisionBoxes();
    public abstract AxisAlignedBB getBoundingBox();
    public abstract AxisAlignedBB[] getOcclusionBoxes();

}
