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
        logger.info("Чтение файла по пути: {}", filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            logger.error("Ошибка ввода-вывода при чтении файла: {}", filePath, e);
            throw new RestaurantException("Не удалось прочитать файл: " + filePath, e);
        }

        return lines;
    }
}