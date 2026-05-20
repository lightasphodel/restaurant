package com.lena.restaurant.state;

import com.lena.restaurant.entity.Table;

public interface TableState {
    void nextState(Table table);
    String getStatus();
}