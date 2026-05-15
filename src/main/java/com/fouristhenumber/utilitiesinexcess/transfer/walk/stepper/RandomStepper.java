package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class RandomStepper extends StepStrategy
{
    Random rand = new Random();

    public RandomStepper(TransportType transporting) {
        super(transporting);
    }

    @Override
    public ITransferNetworkComponent step(ITransferNetworkComponent currentComponent, ITransferNetworkComponent walkingComponent)
    {
        ITransferNetworkComponent newComponent;
        MaskedArrayView<ITransferNetworkComponent> options = currentComponent.getWalkableDirs(transporting, fromDirection);
        if (options.size() != 0)
        {
            int direction = rand.nextInt(options.size());
            newComponent = options.get(direction);
            fromDirection = ForgeDirection.getOrientation(options.actualLocation(direction)).getOpposite();
        }
        else
        {
            newComponent = walkingComponent;
            fromDirection = null;
        }
        return newComponent;
    }

    @Override
    public ITransferNetworkComponent reset(ITransferNetworkComponent currentComponent, ITransferNetworkComponent walkingComponent) {
        return currentComponent;
    }
}
