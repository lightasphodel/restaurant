package com.lena.restaurant;

import com.lena.restaurant.entity.VisitorCompany;
import com.lena.restaurant.exception.RestaurantException;
import com.lena.restaurant.parser.DataParser;
import com.lena.restaurant.parser.impl.DataParserImpl;
import com.lena.restaurant.pool.Restaurant;
import com.lena.restaurant.reader.DataReader;
import com.lena.restaurant.reader.impl.DataReaderImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Thec programm has been started");

        DataReader reader = new DataReaderImpl();
        DataParser parser = new DataParserImpl();
        
        int tableCount;
        int totalDishes;
        List<Integer> companiesSpecs;

        try {
            List<String> rawLines = reader.readLines("src/main/resources/restaurant_init.txt");
            
            tableCount = parser.parseTableCount(rawLines);
            totalDishes = parser.parseTotalDishes(rawLines);
            companiesSpecs = parser.parseCompaniesSpecs(rawLines);
            
        } catch (RestaurantException e) {
            logger.error("Critical error preparing application data!", e);
            return;
        }

        Restaurant restaurant = Restaurant.getInstance();
        restaurant.init(tableCount, totalDishes);

        ExecutorService executorService = Executors.newFixedThreadPool(companiesSpecs.size());

        logger.info("Launching restaurant customer flows...");
        for (int i = 0; i < companiesSpecs.size(); i++) {
            Runnable company = new VisitorCompany(i + 1, companiesSpecs.get(i));
            executorService.execute(company);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        logger.info("The programm has been finished");
    }
}