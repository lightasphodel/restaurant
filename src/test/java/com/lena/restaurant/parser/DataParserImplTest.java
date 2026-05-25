package com.lena.restaurant.parser;

import com.lena.restaurant.exception.RestaurantException;
import com.lena.restaurant.parser.impl.DataParserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataParserImplTest {

    private DataParser parser;
    private List<String> mockCorrectLines;

    @BeforeEach
    void setUp() {
        parser = new DataParserImpl();
        mockCorrectLines = Arrays.asList(
            "3",         
            "15",        
            "4",        
            "2 4 1 3"    
        );
    }

    @Test
    void testParseTableCountSuccess() throws RestaurantException {
        int tables = parser.parseTableCount(mockCorrectLines);
        assertEquals(3, tables, "Table count must be exactly 3");
    }

    @Test
    void testParseTotalDishesSuccess() throws RestaurantException {
        int dishes = parser.parseTotalDishes(mockCorrectLines);
        assertEquals(15, dishes, "Total dishes count must be exactly 15");
    }

    @Test
    void testParseCompaniesSpecsSuccess() throws RestaurantException {
        List<Integer> specs = parser.parseCompaniesSpecs(mockCorrectLines);
        
        assertNotNull(specs, "Company specifications list must not be null");
        assertEquals(4, specs.size(), "Exactly 4 company specifications should be parsed");
        assertEquals(2, specs.get(0));
        assertEquals(3, specs.get(3));
    }

    @Test
    void testParseTableCountThrowsRestaurantException() {
        List<String> badLines = Collections.singletonList("NOT_A_NUMBER");

        RestaurantException exception = assertThrows(RestaurantException.class, () -> {
            parser.parseTableCount(badLines);
        });

        assertTrue(exception.getMessage().contains("Failed to parse table count"), 
                "Exception message should contain the correct English description");
        assertNotNull(exception.getCause(), "The root cause exception must be preserved");
        assertTrue(exception.getCause() instanceof NumberFormatException);
    }

    @Test
    void testParseTotalDishesWithMissingLine() {
        List<String> incompleteLines = Collections.singletonList("3");

        assertThrows(RestaurantException.class, () -> {
            parser.parseTotalDishes(incompleteLines);
        }, "Method must throw RestaurantException when necessary input lines are missing");
    }

    @Test
    void testParseCompaniesSpecsWithMismatchCount() {
        List<String> brokenSpecsLines = Arrays.asList(
            "3",
            "15",
            "10",       
            "2 4 1"    
        );

        RestaurantException exception = assertThrows(RestaurantException.class, () -> {
            parser.parseCompaniesSpecs(brokenSpecsLines);
        });

        assertTrue(exception.getMessage().contains("Configuration mismatch"), 
                "Exception message should report a configuration mismatch in English");
    }
}