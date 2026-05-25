package com.lena.restaurant.parser.impl;

import com.lena.restaurant.exception.RestaurantException;
import com.lena.restaurant.parser.DataParser;

import java.util.ArrayList;
import java.util.List;

public class DataParserImpl implements DataParser {

    @Override
    public int parseTableCount(List<String> lines) throws RestaurantException {
        if (lines == null || lines.isEmpty()) {
            throw new RestaurantException("Cannot parse table count: lines list is null or empty");
        }
        
        try {
            return Integer.parseInt(lines.get(0));
        } catch (NumberFormatException e) {
            throw new RestaurantException("Failed to parse table count. Invalid string format: " + lines.get(0), e);
        }
    }

    @Override
    public int parseTotalDishes(List<String> lines) throws RestaurantException {
        if (lines == null || lines.size() < 2) {
            throw new RestaurantException("Cannot parse total dishes count: second line is missing");
        }
        
        try {
            return Integer.parseInt(lines.get(1));
        } catch (NumberFormatException e) {
            throw new RestaurantException("Failed to parse total dishes count. Invalid string format: " + lines.get(1), e);
        }
    }

    @Override
    public List<Integer> parseCompaniesSpecs(List<String> lines) throws RestaurantException {
        if (lines == null || lines.size() < 4) {
            throw new RestaurantException("Insufficient lines in file to parse companies specifications");
        }

        List<Integer> companiesSpecs = new ArrayList<>();
        String rawCompanyCount = lines.get(2);
        String rawSpecs = lines.get(3);

        try {
            int companyCount = Integer.parseInt(rawCompanyCount);
            String[] specs = rawSpecs.split("\\s+");
            
            if (specs.length < companyCount) {
                throw new RestaurantException("Configuration mismatch: company count set to " + companyCount 
                        + ", but only " + specs.length + " specifications found in file");
            }

            for (int i = 0; i < companyCount; i++) {
                companiesSpecs.add(Integer.parseInt(specs[i]));
            }
            
            return companiesSpecs;
            
        } catch (NumberFormatException e) {
            throw new RestaurantException("Failed to parse numeric data within companies structure", e);
        }
    }
}