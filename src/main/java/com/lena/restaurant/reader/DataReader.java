package com.lena.restaurant.reader;

import com.lena.restaurant.exception.RestaurantException;
import java.util.List;

public interface DataReader {
    List<String> readLines(String filePath) throws RestaurantException;
}