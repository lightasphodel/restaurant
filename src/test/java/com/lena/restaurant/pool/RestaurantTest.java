package com.lena.restaurant.pool;

import com.lena.restaurant.entity.Table;
import com.lena.restaurant.exception.RestaurantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    private Restaurant restaurant;

    @BeforeEach
    void setUp() throws Exception {
        restaurant = Restaurant.getInstance();
        
        Field tablesField = Restaurant.class.getDeclaredField("tables");
        tablesField.setAccessible(true);
        List<?> tables = (List<?>) tablesField.get(restaurant);
        tables.clear();

        restaurant.init(2, 10);
    }

    @Test
    void testSingletonInstanceIdentity() {
        Restaurant anotherInstance = Restaurant.getInstance();
        assertSame(restaurant, anotherInstance, "Singleton must always return the exact same object identity");
    }

    @Test
    void testOccupyAndReleaseTable() throws RestaurantException {
        Table table1 = restaurant.occupyTable(101);
        assertNotNull(table1, "Table should be successfully allocated");
        assertEquals("Занят", table1.getState().getStatus());

        Table table2 = restaurant.occupyTable(102);
        assertNotNull(table2, "Second table should be successfully allocated");

        restaurant.releaseTable(table1, 101);
        assertEquals("Свободен", table1.getState().getStatus());
    }

    @Test
    void testKitchenConcurrentOrder() throws InterruptedException {
        int threadCount = 5;
        int dishesPerCompany = 2;
        
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger exceptionCounter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int companyId = i + 1;
            executorService.execute(() -> {
                try {
                    restaurant.orderDishes(companyId, dishesPerCompany);
                } catch (RestaurantException e) {
                    exceptionCounter.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);

        assertTrue(completed, "All company threads should complete their orders in time");
        assertEquals(0, exceptionCounter.get(), "No business logic exceptions should occur during concurrent ordering");
        
        executorService.shutdown();
    }
}