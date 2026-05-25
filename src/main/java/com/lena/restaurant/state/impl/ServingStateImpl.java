package com.lena.restaurant.state.impl;

import com.lena.restaurant.entity.Table;
import com.lena.restaurant.state.TableState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServingStateImpl implements TableState {
    private static final Logger logger = LogManager.getLogger(ServingStateImpl.class);

    @Override
    public void nextState(Table table) {
        table.setState(new FreeStateImpl());
        logger.info("Table #{} changed state to: FREE", table.getId());
    }

    @Override
    public String getStatus() {
        return "Served";
    }
}