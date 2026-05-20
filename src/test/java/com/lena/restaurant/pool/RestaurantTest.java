package com.lena.restaurant.pool;

import com.lena.restaurant.entity.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.getInstance();
        restaurant.init(2, 10);
    }

    @Test
    void testSingletonInstanceIdentity() {
        Restaurant anotherInstance = Restaurant.getInstance();
        assertSame(restaurant, anotherInstance, "Синглтон должен возвращать один и тот же объект");
    }

    @Test
    void testOccupyAndReleaseTable() throws InterruptedException {
        Table table1 = restaurant.occupyTable(101);
        assertNotNull(table1, "Стол должен успешно выделиться");
        assertEquals("Занят", table1.getState().getStatus());

        Table table2 = restaurant.occupyTable(102);
        assertNotNull(table2);

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
                } catch (InterruptedException e) {
                    exceptionCounter.incrementAndGet();
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);

        assertTrue(completed, "Все потоки должны были завершить заказы вовремя");
        assertEquals(0, exceptionCounter.get(), "Во время параллельных заказов не должно быть исключений");
        
        executorService.shutdown();
    }
}