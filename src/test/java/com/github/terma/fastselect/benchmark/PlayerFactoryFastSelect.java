package com.github.terma.fastselect.benchmark;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.demo.DemoData;

import java.util.List;

public class PlayerFactoryFastSelect implements PlayerFactory<DemoData> {

    private FastSelect<DemoData> fastSelect = new FastSelect<>(DemoData.class);

    @Override
    public void addData(List<DemoData> data) throws Exception {
        fastSelect.addAll(data);
    }

    @Override
    public Player createPlayer() throws Exception {
        return new PlayerFastSelect(fastSelect);
    }
}
