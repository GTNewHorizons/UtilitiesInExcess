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

    public void moveChild(IWidget widget, int nextIndex) {
        int index = -1;
        for (int i = 0; i < getChildren().size(); i++) {
            if (getChildren().get(i) == widget) {
                index = i;
                break;
            }
        }

        if (index != -1) moveChild(index, nextIndex);
    }
}
