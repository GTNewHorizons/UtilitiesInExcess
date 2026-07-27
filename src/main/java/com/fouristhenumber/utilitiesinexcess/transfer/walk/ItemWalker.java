package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.ItemTargetResolver;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.StepStrategy;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.TargetResolver;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemWalker extends WalkerBase<IInventory, ItemStack>
{
    StepStrategy stepper;
    TargetResolver<IInventory> targeter;

    public ItemWalker(IWalkingComponent<ItemStack> originComponent)
    {
        super(originComponent);
        stepper = new RandomStepper(TransportType.ITEM);
        targeter = new ItemTargetResolver();
    }

    @Override
    public void step(World world)
    {
        walkerPos = stepper.step(world, walkerPos, walkingComponent);
    }

    @Override
    public List<TargetResolver.Target<IInventory>> getValidTargets(World world)
    {
        return targeter.getValidTargets(world, walkerPos, walkingComponent, stepper.fromDirection);
    }

    // TODO
    // Gets the amount of items that can be put into an inventory by a certain component. This is relevant
    // for rationing pipes. If result is -1, the limit is ignored.
    @Override
    public int getInsertLimit(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) instanceof BlockTransferBase networkBlock)
        {
            return networkBlock.maxInsertable(world.getBlockMetadata(x, y, z));
        }
        return -1;
    }

    @Override
    public void reset()
    {
        stepper.reset(walkerPos, walkingComponent);
    }
}
