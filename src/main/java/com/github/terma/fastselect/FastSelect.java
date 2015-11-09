package com.github.terma.fastselect;

import java.util.List;

/**
 * Created by terma on 11/8/15.
 */
public interface FastSelect<T> {
    List<T> select(MultiRequest[] where);

    void select(MultiRequest[] where, Callback<T> callback);
}
