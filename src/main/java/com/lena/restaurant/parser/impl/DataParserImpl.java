package com.lena.restaurant.parser.impl;

import com.lena.restaurant.exception.RestaurantException;
import com.lena.restaurant.parser.DataParser;

import java.util.ArrayList;
import java.util.List;

public class DataParserImpl implements DataParser {

    @Override
    public int parseTableCount(List<String> lines) throws RestaurantException {
        if (lines == null || lines.isEmpty()) {
            throw new RestaurantException("Невозможно спарсить количество столов: список строк пуст");
        }
        
        try {
            return Integer.parseInt(lines.get(0));
        } catch (NumberFormatException e) {
            throw new RestaurantException("Ошибка парсинга количества столов. Строка некорректна: " + lines.get(0), e);
        }
    }

    @Override
    public int parseTotalDishes(List<String> lines) throws RestaurantException {
        if (lines == null || lines.size() < 2) {
            throw new RestaurantException("Невозможно спарсить количество блюд: отсутствует вторая строка");
        }
        
        try {
            return Integer.parseInt(lines.get(1));
        } catch (NumberFormatException e) {
            throw new RestaurantException("Ошибка парсинга общего количества блюд. Строка некорректна: " + lines.get(1), e);
        }
    }

    @Override
    public List<Integer> parseCompaniesSpecs(List<String> lines) throws RestaurantException {
        if (lines == null || lines.size() < 4) {
            throw new RestaurantException("Недостаточно строк в файле для парсинга спецификаций компаний");
        }

        List<Integer> companiesSpecs = new ArrayList<>();
        String rawCompanyCount = lines.get(2);
        String rawSpecs = lines.get(3);

        try {
            int companyCount = Integer.parseInt(rawCompanyCount);
            String[] specs = rawSpecs.split("\\s+");
            
            if (specs.length < companyCount) {
                throw new RestaurantException("Конфигурация нарушена: указано компаний " + companyCount 
                        + ", но в файле найдено настроек только для " + specs.length);
            }

            for (int i = 0; i < companyCount; i++) {
                companiesSpecs.add(Integer.parseInt(specs[i]));
            }
            
            return companiesSpecs;
            
        } catch (NumberFormatException e) {
            throw new RestaurantException("Ошибка разбора числовых данных в структуре компаний", e);
        }
    }
}