package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.BFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.DFSStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.ItemTargetResolver;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RoundRobinStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.StepStrategy;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.TargetResolver;
import net.minecraft.inventory.IInventory;

import java.util.List;

public class ItemWalker extends WalkerBase<IInventory>
{
    StepStrategy stepper;
    TargetResolver<IInventory> targeter = new ItemTargetResolver();

    public ItemWalker(ITransferNetworkComponent originComponent)
    {
        super(originComponent);
        stepper = new RandomStepper(TransportType.ITEM);
    }

    @Override
    public void step()
    {
        currentComponent = stepper.step(currentComponent, walkingComponent);
    }

    @Override
    public TargetResolver.Target<IInventory> getValidTarget()
    {
        List<TargetResolver.Target<IInventory>> validTargets = targeter.getValidTargets(currentComponent);
        if (validTargets.isEmpty())
        {
            return null;
        }
        return validTargets.get(rand.nextInt(validTargets.size()));
    }

    @Override
    public void reset()
    {
        currentComponent = stepper.reset(currentComponent, walkingComponent);
    }
}
