package com.lena.restaurant.entity;

import com.lena.restaurant.pool.Restaurant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

public class VisitorCompany implements Runnable {
    private static final Logger logger = LogManager.getLogger(VisitorCompany.class);
    private final int companyId;
    private final int dishesNeeded;

    public VisitorCompany(int companyId, int dishesNeeded) {
        this.companyId = companyId;
        this.dishesNeeded = dishesNeeded;
    }

    @Override
    public void run() {
        logger.info("Компания №{} прибыла в ресторан. Требуется блюд: {}", companyId, dishesNeeded);
        Restaurant restaurant = Restaurant.getInstance();
        Table allocatedTable = null;

        try {
            allocatedTable = restaurant.occupyTable(companyId);
            
            TimeUnit.MILLISECONDS.sleep(300);

            restaurant.orderDishes(companyId, dishesNeeded);

            TimeUnit.MILLISECONDS.sleep(600);
            logger.info("Компания №{} закончила ужинать", companyId);

        } catch (InterruptedException e) {
            logger.error("Поток компании №{} был аварийно прерван", companyId, e);
            Thread.currentThread().interrupt();
        } finally {
            if (allocatedTable != null) {
                restaurant.releaseTable(allocatedTable, companyId);
            }
        }
    }
}