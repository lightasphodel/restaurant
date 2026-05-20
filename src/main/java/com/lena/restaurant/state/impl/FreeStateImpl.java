package com.lena.restaurant.state.impl;

import com.lena.restaurant.entity.Table;
import com.lena.restaurant.state.TableState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FreeStateImpl implements TableState {
    private static final Logger logger = LogManager.getLogger(FreeStateImpl.class);

    @Override
    public void nextState(Table table) {
        table.setState(new OccupiedStateImpl());
        logger.info("Стол №{} переведен в состояние: ЗАНЯТ", table.getId());
    }

    @Override
    public String getStatus() {
        return "Свободен";
    }
}