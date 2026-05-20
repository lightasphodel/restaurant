package com.lena.restaurant.parser;

import com.lena.restaurant.exception.RestaurantException;
import java.util.List;

public interface DataParser {
    int parseTableCount(List<String> lines) throws RestaurantException;
    int parseTotalDishes(List<String> lines) throws RestaurantException;
    List<Integer> parseCompaniesSpecs(List<String> lines) throws RestaurantException;
}