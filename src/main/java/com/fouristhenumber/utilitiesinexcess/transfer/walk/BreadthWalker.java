package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BreadthWalker extends ITransferWalker {

    @Override
    public void step() {
        for (TileEntity tile : ignore) {
            if (tile == null || tile.isInvalid()) {
                reset();
                break;
            }
        }

        tanks.clear();
        chests.clear();


        current = lastChoice.get(0);
        lastChoice.remove(0);
        ignore.add(current);
        ArrayList<TileEntity> extend=new ArrayList<TileEntity>(0);


        action="Searching for inventories at\n"+ Arrays.toString(getRelative(original,current));

        ArrayList<TileEntity> tiles = getAdjacentEntities(current);

        for (TileEntity tile : tiles) {
            if (tile == null || ignore.contains(tile)) {
                continue;
            }

            Package apple=tile.getClass().getPackage();
            if (apple!=null&&apple.getName().equals("com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer")) //somehow gonna need to kill the chest from the node(s) itself..
            {
                extend.add(tile);
                continue;
            }

            if (tile instanceof IInventory) {
                chests.add(tile);
            }

            if (tile instanceof IFluidHandler) {
                tanks.add(tile);
            }
        }

        Collections.shuffle(extend);
        lastChoice.addAll(extend);
        if (lastChoice.isEmpty()) {reset();}
    }
}
