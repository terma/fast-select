package com.github.terma.fastselect.benchmark;

import java.util.List;

public interface PlayerFactory<T> {

    void addData(List<T> data) throws Exception;

    Player createPlayer() throws Exception;

}
