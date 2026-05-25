package com.lena.restaurant.entity;

import com.lena.restaurant.exception.RestaurantException;
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
        logger.info("Company #{} arrived at the restaurant. Dishes needed: {}", companyId, dishesNeeded);
        Restaurant restaurant = Restaurant.getInstance();
        Table allocatedTable = null;

        try {
            allocatedTable = restaurant.occupyTable(companyId);
            
            TimeUnit.MILLISECONDS.sleep(300);

            restaurant.orderDishes(companyId, dishesNeeded);

            TimeUnit.MILLISECONDS.sleep(600);
            logger.info("Company #{} finished dining.", companyId);

        } catch (RestaurantException e) {
            logger.error("Restaurant business logic error for company #{}: {}", companyId, e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("Company thread #{} was unexpectedly interrupted", companyId, e);
            Thread.currentThread().interrupt();
        } finally {
            if (allocatedTable != null) {
                restaurant.releaseTable(allocatedTable, companyId);
            }
        }
    }
}