package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import cofh.api.energy.IEnergyConnection;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockPipe;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.PipeType;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.*;
import net.minecraft.world.World;

import java.util.List;

// Pretty sure these are totally unaffected by search type upgrades. For round-robin, dfs, and bfs.
public class EnergyWalker extends WalkerBase<IEnergyConnection, Integer>
{

    StepStrategy stepper;
    TargetResolver<IEnergyConnection> targeter;

    EnergyWalker(IWalkingComponent<Integer> walkingComponent) {
        super(walkingComponent);
        stepper = new EnergyStepper(TransportType.ENERGY);
        targeter = new EnergyTargetResolver();
    }

    @Override
    public void step(World world) {
        walkerPos = stepper.step(world, walkerPos, walkingComponent);
    }

    @Override
    public void reset() {

    }

    @Override
    public List<TargetResolver.Target<IEnergyConnection>> getValidTargets(World world) {
        return targeter.getValidTargets(world, walkerPos, walkingComponent, stepper.fromDirection);
    }

    public boolean isOnExtractionPipe(World world)
    {
        return (world.getBlock(walkerPos.x, walkerPos.y, walkerPos.z) instanceof BlockPipe &&
            world.getBlockMetadata(walkerPos.x, walkerPos.y, walkerPos.z) == PipeType.ENERGYEXTRACTION.ordinal());
    }

    public boolean isAtOrigin()
    {
        return (walkingComponent.getX() == walkerPos.x && walkingComponent.getY() == walkerPos.y && walkingComponent.getZ() == walkerPos.z);
    }
}
