package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable.getConnectables;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.DefaultInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.ItemTargetResolver;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;

public class ItemWalker extends WalkerBase<IInventory> {

    TargetResolver<IInventory> targeter;

    public ItemWalker(IWalkingComponent originComponent) {
        super(originComponent);
        stepper = new RandomStepper();
        targeter = new ItemTargetResolver();
    }

    @Override
    public void step(World world) {
        walkerPos = stepper.step(world, walkerPos, walkingComponent);
    }

    @Override
    public List<TargetResolver.Target<IInventory>> getValidTargets(World world) {
        return targeter.getValidTargets(world, walkerPos, walkingComponent, stepper.fromDirection);
    }

    // Different blocks have different insertion rules
    public BaseInserter getInserter(World world) {
        List<IConnectable> connectables = getConnectables(world, walkerPos.x, walkerPos.y, walkerPos.z);
        if (connectables.isEmpty()) {
            return new DefaultInserter();
        }
        // Since theo only thing that can be in multiples in a block that is a connectable is a node, it's safe to
        // just check the first one since all nodes have the same inserter.
        return connectables.getFirst()
            .getInserter(world, walkerPos.x, walkerPos.y, walkerPos.z);
    }

    @Override
    public void reset() {
        stepper.reset(walkerPos, walkingComponent);
    }
}
