package com.fouristhenumber.utilitiesinexcess.compat.mui.tradingpost;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.widgets.layout.Column;

public class VillagerColumn extends Column {

    public void moveChild(int prevIndex, int nextIndex) {
        IWidget child = this.getChildren()
            .get(prevIndex);
        this.getChildren()
            .remove(prevIndex);
        this.getChildren()
            .add(nextIndex, child);
    }
}
