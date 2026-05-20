package com.lena.restaurant.entity;

import com.lena.restaurant.state.TableState;
import com.lena.restaurant.state.impl.FreeStateImpl;

public class Table {
    private final int id;
    private TableState state;

    public Table(int id) {
        this.id = id;
        this.state = new FreeStateImpl(); 
    }

    public int getId() { 
        return id; 
    }
    
    public TableState getState() { 
        return state; 
    }
    
    public void setState(TableState state) { 
        this.state = state; 
    }
    
    public void changeState() {
        state.nextState(this);
    }
}