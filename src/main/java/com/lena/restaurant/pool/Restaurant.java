package com.lena.restaurant.pool;

import com.lena.restaurant.entity.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
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
            logger.info("Ресторан успешно запущен. Столов: {}, Блюд на кухне: {}", tableCount, initialDishes);
        } finally {
            lock.unlock();
        }
    }

    public Table occupyTable(int companyId) throws InterruptedException {
        lock.lock();
        try {
            while (getFreeTable() == null) {
                logger.info("Компания №{} ожидает свободный столик...", companyId);
                tableAvailable.await();
            }
            Table table = getFreeTable();
            table.changeState(); 
            logger.info("Компания №{} села за стол №{}", companyId, table.getId());
            return table;
        } finally {
            lock.unlock();
        }
    }

    public void releaseTable(Table table, int companyId) {
        lock.lock();
        try {
            table.changeState();
            logger.info("Компания №{} освободила стол №{}", companyId, table.getId());
            tableAvailable.signalAll(); 
        } finally {
            lock.unlock();
        }
    }

    public void orderDishes(int companyId, int count) throws InterruptedException {
        lock.lock();
        try {
            while (availableDishes < count) {
                logger.warn("На кухне недостаточно еды для компании №{}. Требуется: {}, Доступно: {}. Ожидание...", 
                        companyId, count, availableDishes);
                kitchenReady.await();
            }
            availableDishes -= count;
            logger.info("Компания №{} получила заказ (порций: {}). Оборот кухни: осталось блюд -> {}", 
                    companyId, count, availableDishes);
        } finally {
            lock.unlock();
        }
    }

    private Table getFreeTable() {
        for (Table t : tables) {
            if ("Свободен".equals(t.getState().getStatus())) {
                return t;
            }
        }
        return null;
    }
}