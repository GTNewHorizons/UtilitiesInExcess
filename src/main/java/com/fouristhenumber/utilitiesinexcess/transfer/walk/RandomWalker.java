package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public class RandomWalker extends WalkerBase
{

    Random rand = new Random();
    public RandomWalker(ITransferNetworkComponent originComponent)
    {
        super(originComponent);
    }

    @Override
    public void step()
    {
        MaskedArrayView<ITransferNetworkComponent> options = currentComponent.getWalkableDirs(walkingComponent, fromDirection);
        if (options.size() != 0)
        {
            int direction = rand.nextInt(options.size());
            currentComponent = options.get(direction);
            fromDirection = ForgeDirection.getOrientation(options.actualLocation(direction)).getOpposite();
        }
        else
        {
            currentComponent = walkingComponent;
            fromDirection = null;
        }
    }
}
