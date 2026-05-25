package com.lena.restaurant.pool;

import com.lena.restaurant.entity.Table;
import com.lena.restaurant.exception.RestaurantException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    private static final Logger logger = LogManager.getLogger(Restaurant.class);

    private final Lock lock = new ReentrantLock(true);
    private final Condition tableAvailable = lock.newCondition();
    private final Condition kitchenReady = lock.newCondition();

    private final List<Table> tables = new ArrayList<>();
    private int availableDishes;

    private static class Holder {
        private static final Restaurant INSTANCE = new Restaurant();
    }

    private Restaurant() {}

    public static Restaurant getInstance() {
        return Holder.INSTANCE;
    }

    public void init(int tableCount, int initialDishes) {
        lock.lock();
        try {
            this.availableDishes = initialDishes;
            for (int i = 1; i <= tableCount; i++) {
                tables.add(new Table(i));
            }
            logger.info("Restaurant successfully initialized. Tables: {}, Dishes available: {}", tableCount, initialDishes);
        } finally {
            lock.unlock();
        }
    }

    public Table occupyTable(int companyId) throws RestaurantException {
        lock.lock();
        try {
            while (!getFreeTable().isPresent()) {
                logger.info("Company #{} is waiting for an available table...", companyId);
                try {
                    tableAvailable.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RestaurantException("Company thread #" + companyId + " was interrupted while waiting for a table", e);
                }
            }
            
            Table table = getFreeTable()
                    .orElseThrow(() -> new RestaurantException("Critical error: free table vanished from the pool!"));
            
            table.changeState(); 
            logger.info("Company #{} took table #{}", companyId, table.getId());
            return table;
            
        } finally {
            lock.unlock();
        }
    }

    public void releaseTable(Table table, int companyId) {
        lock.lock();
        try {
            table.changeState();
            logger.info("Company #{} released table #{}", companyId, table.getId());
            tableAvailable.signalAll(); 
        } finally {
            lock.unlock();
        }
    }

    public void orderDishes(int companyId, int count) throws RestaurantException {
        lock.lock();
        try {
            while (availableDishes < count) {
                logger.warn("Kitchen out of food for company #{}. Required: {}, Available: {}. Waiting...", 
                        companyId, count, availableDishes);
                try {
                    kitchenReady.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RestaurantException("Company thread #" + companyId + " was interrupted while waiting for food", e);
                }
            }
            availableDishes -= count;
            logger.info("Company #{} received order (portions: {}). Remaining dishes in kitchen: {}", 
                    companyId, count, availableDishes);
        } finally {
            lock.unlock();
        }
    }

    private Optional<Table> getFreeTable() {
        return tables.stream()
                .filter(t -> "Свободен".equals(t.getState().getStatus()))
                .findFirst();
    }
}