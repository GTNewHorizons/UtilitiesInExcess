package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.util;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import it.unimi.dsi.fastutil.Pair;
import net.minecraftforge.common.util.ForgeDirection;

public class CuboidUtils
{
    public final Vector3 posX = new Vector3(1, 0, 0);
    public final Vector3 posY = new Vector3(0, 1, 0);
    public final Vector3 posZ = new Vector3(0, 0, 1);
    public final Vector3 negX = new Vector3(-1, 0, 0);
    public final Vector3 negY = new Vector3(0, -1, 0);
    public final Vector3 negZ = new Vector3(0, 0, -1);

    // ROTATION ASSUMES MIN VECTOR IS ZEROED.
    public Cuboid6 rotateXPos(Cuboid6 target)
    {
        double newX = target.max.x;
        double newY = -target.max.z;
        double newZ = target.max.y;
        target.max.x = newX;
        target.max.y = newY;
        target.max.z = newZ;
        return target;
    }

    public Cuboid6 rotateXNeg(Cuboid6 target)
    {
        double newX = target.max.x;
        double newY = target.max.z;
        double newZ = -target.max.y;
        target.max.x = newX;
        target.max.y = newY;
        target.max.z = newZ;
        return target;
    }

    public Cuboid6 rotateYPos(Cuboid6 target)
    {
        double newX = target.max.z;
        double newY = target.max.y;
        double newZ = -target.max.x;
        target.max.x = newX;
        target.max.y = newY;
        target.max.z = newZ;
        return target;
    }
    public Cuboid6 rotateYNeg(Cuboid6 target)
    {
        double newX = -target.max.z;
        double newY = target.max.y;
        double newZ = target.max.x;
        target.max.x = newX;
        target.max.y = newY;
        target.max.z = newZ;
        return target;
    }

    public Cuboid6 rotateZPos(Cuboid6 target)
    {
        double newX = -target.max.y;
        double newY = target.max.x;
        double newZ = target.max.z;
        target.max.x = newX;
        target.max.y = newY;
        target.max.z = newZ;
        return target;
    }

    public Cuboid6 rotateZNeg(Cuboid6 target)
    {
        double newX = target.max.y;
        double newY = -target.max.x;
        double newZ = target.max.z;
        target.max.x = newX;
        target.max.y = newY;
        target.max.z = newZ;
        return target;
    }

    public static Cuboid6 Translate(Cuboid6 input, Vector3 direction)
    {
        input.max.add(direction);
        input.min.add(direction);
        return input;
    }

    public static Cuboid6 Rotate90AboutXBlockCenterPos(Cuboid6 input, int times)
    {
        Cuboid6 output = input.copy();
        Translate(output, new Vector3(0, -0.5, -0.5));
        Vector3 rotVector = new Vector3(1 ,0, 0);
        for (int i = 0; i < times; i++)
        {
            output.max.rotate(Math.toRadians(90), rotVector);
            output.min.rotate(Math.toRadians(90), rotVector);
        }
        Translate(output, new Vector3(0, 0.5, 0.5));

        // Cuboids can become inside out after this. Gotta fix that
        Vector3 newMin = new Vector3(
            Math.min(output.min.x, output.max.x),
            Math.min(output.min.y, output.max.y),
            Math.min(output.min.z, output.max.z)
        );

        Vector3 newMax = new Vector3(
            Math.max(output.min.x, output.max.x),
            Math.max(output.min.y, output.max.y),
            Math.max(output.min.z, output.max.z)
        );

        output.min = newMin;
        output.max = newMax;

        return output;
    }

    public static Cuboid6 Rotate90AboutYBlockCenterPos(Cuboid6 input, int times)
    {
        Cuboid6 output = input.copy();
        Translate(output, new Vector3(-0.5, 0, -0.5));
        Vector3 rotVector = new Vector3(0 ,1, 0);
        for (int i = 0; i < times; i++)
        {
            output.max.rotate(Math.toRadians(90), rotVector);
            output.min.rotate(Math.toRadians(90), rotVector);
        }
        Translate(output, new Vector3(0.5, 0, 0.5));

        // Cuboids can become inside out after this. Gotta fix that
        Vector3 newMin = new Vector3(
            Math.min(output.min.x, output.max.x),
            Math.min(output.min.y, output.max.y),
            Math.min(output.min.z, output.max.z)
        );

        Vector3 newMax = new Vector3(
            Math.max(output.min.x, output.max.x),
            Math.max(output.min.y, output.max.y),
            Math.max(output.min.z, output.max.z)
        );

        output.min = newMin;
        output.max = newMax;

        return output;
    }

    public static Cuboid6 Rotate90AboutZBlockCenterPos(Cuboid6 input, int times)
    {
        Cuboid6 output = input.copy();
        Translate(output, new Vector3(-0.5, -0.5, 0));
        Vector3 rotVector = new Vector3(0 ,0, 1);
        for (int i = 0; i < times; i++)
        {
            output.max.rotate(Math.toRadians(90), rotVector);
            output.min.rotate(Math.toRadians(90), rotVector);
        }
        Translate(output, new Vector3(0.5, 0.5, 0));

        // Cuboids can become inside out after this. Gotta fix that
        Vector3 newMin = new Vector3(
            Math.min(output.min.x, output.max.x),
            Math.min(output.min.y, output.max.y),
            Math.min(output.min.z, output.max.z)
        );

        Vector3 newMax = new Vector3(
            Math.max(output.min.x, output.max.x),
            Math.max(output.min.y, output.max.y),
            Math.max(output.min.z, output.max.z)
        );

        output.min = newMin;
        output.max = newMax;

        return output;
    }

    // Takes a model that is assumed to be defaulted to the ForgeDirection Down and rotates it so the
    // specified direction is considered "down" for that model
    public static Cuboid6[] RotateModel(Cuboid6[] model, ForgeDirection direction)
    {
        if (direction == ForgeDirection.DOWN)
        {
            return model;
        }

        Cuboid6[] rotatedModel = new Cuboid6[model.length];
        for (int i = 0; i < model.length; i++)
        {
            rotatedModel[i] = RotateCube(model[i], direction);
        }
        return rotatedModel;
    }

    // Same function as above, but is used for cuboids that have some directional information attached to them.
    @SuppressWarnings("unchecked")
    public static Pair<Integer, Cuboid6>[] RotateModel(Pair<Integer, Cuboid6>[] model, ForgeDirection direction)
    {
        if (direction == ForgeDirection.DOWN)
        {
            return model;
        }

        Pair<Integer, Cuboid6>[] rotatedModel = new Pair [model.length];
        for (int i = 0; i < model.length; i++)
        {
            rotatedModel[i] = Pair.of(model[i].first(), RotateCube(model[i].second(), direction));
        }
        return rotatedModel;
    }

    public static Cuboid6 RotateCube(Cuboid6 cube, ForgeDirection direction)
    {
        return switch(direction)
        {
            case NORTH -> Rotate90AboutXBlockCenterPos(cube, 1);
            case SOUTH -> Rotate90AboutXBlockCenterPos(cube, 3);
            case EAST -> Rotate90AboutZBlockCenterPos(cube, 1);
            case WEST -> Rotate90AboutZBlockCenterPos(cube, 3);
            case DOWN -> cube;
            case UP -> Rotate90AboutXBlockCenterPos(cube, 2);
            case UNKNOWN -> throw new IllegalArgumentException("Switch fall off");
        };
    }
}
