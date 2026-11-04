package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.sharedtransferlogic.ItemRetrievalNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.sharedtransferlogic.NetworkLogic;

public class TileEntityItemRetrievalNode extends TileEntityTransferNodeBase {

    @Override
    public NetworkLogic getLogic() {
        if (logic == null) {
            logic = new ItemRetrievalNodeLogic(this);
        }
        return logic;
    }
}
