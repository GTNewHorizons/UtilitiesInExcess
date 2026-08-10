package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.DefaultInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.ItemTargetResolver;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.StepStrategy;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemWalker extends WalkerBase<IInventory, ItemStack>
{
    TargetResolver<IInventory> targeter;

    public ItemWalker(IWalkingComponent<ItemStack> originComponent)
    {
        super(originComponent);
        stepper = new RandomStepper();
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

    // Different blocks have different insertion rules
    public BaseInserter getInserter(World world)
    {
        if (world.getBlock(walkerPos.x, walkerPos.y, walkerPos.z) instanceof BlockTransferBase transferBase)
        {
            return transferBase.getInserter(world.getBlockMetadata(walkerPos.x, walkerPos.y, walkerPos.z));
        }
        return new DefaultInserter();
    }

    @Override
    public void reset()
    {
        stepper.reset(walkerPos, walkingComponent);
    }
}
