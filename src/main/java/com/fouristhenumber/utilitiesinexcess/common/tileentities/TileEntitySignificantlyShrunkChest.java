package com.fouristhenumber.utilitiesinexcess.common.tileentities;

public class TileEntitySignificantlyShrunkChest extends TileEntityMarginallyMaximisedChest {

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public String getInventoryName() {
        return "tile.significantly_shrunk_chest.name";
    }
}
