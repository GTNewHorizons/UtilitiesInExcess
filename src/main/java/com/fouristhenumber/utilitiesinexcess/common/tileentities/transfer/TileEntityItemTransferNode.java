package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.sharedtransferlogic.ItemTransferNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.sharedtransferlogic.NetworkLogic;

public class TileEntityItemTransferNode extends TileEntityTransferNodeBase {

    @Override
    public NetworkLogic getLogic() {
        if (logic == null) {
            logic = new ItemTransferNodeLogic(this);
        }
        return logic;
    }
}
