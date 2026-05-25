package com.lena.restaurant.reader.impl;

import com.lena.restaurant.reader.DataReader;
import com.lena.restaurant.exception.RestaurantException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReaderImpl implements DataReader {
    private static final Logger logger = LogManager.getLogger(DataReaderImpl.class);

    @Override
    public List<String> readLines(String filePath) throws RestaurantException {
        List<String> lines = new ArrayList<>();
        logger.info("Cannot read the path: {}", filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.strip());
            }
        } catch (IOException e) {
            logger.error("The input-output error: {}", filePath, e);
            throw new RestaurantException("Cannot read the file: " + filePath, e);
        }

        return lines;
    }
}