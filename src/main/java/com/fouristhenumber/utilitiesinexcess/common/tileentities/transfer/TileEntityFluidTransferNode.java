package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.sharedtransferlogic.FluidTransferNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.sharedtransferlogic.NetworkLogic;

public class TileEntityFluidTransferNode extends TileEntityTransferNodeBase {

    @Override
    public NetworkLogic getLogic() {
        if (logic == null) {
            logic = new FluidTransferNodeLogic(this);
        }
        return logic;
    }

}
