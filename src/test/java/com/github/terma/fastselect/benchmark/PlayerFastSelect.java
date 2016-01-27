package com.github.terma.fastselect.benchmark;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.callbacks.CounterCallback;
import com.github.terma.fastselect.demo.DemoData;
import com.github.terma.fastselect.demo.DemoUtils;

public class PlayerFastSelect implements Player {

    private final FastSelect<DemoData> fastSelect;

    public PlayerFastSelect(FastSelect<DemoData> fastSelect) {
        this.fastSelect = fastSelect;
    }

    @Override
    public Object playGroupByGAndR() {
        CounterCallback callback = new CounterCallback();
        fastSelect.select(DemoUtils.whereGAndR(), callback);
        return callback;
    }
}
