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
        assertEquals(3, tables, "Количество столов должно быть равно 3");
    }

    @Test
    void testParseTotalDishesSuccess() throws RestaurantException {
        int dishes = parser.parseTotalDishes(mockCorrectLines);
        assertEquals(15, dishes, "Общее количество блюд должно быть равно 15");
    }

    @Test
    void testParseCompaniesSpecsSuccess() throws RestaurantException {
        List<Integer> specs = parser.parseCompaniesSpecs(mockCorrectLines);
        
        assertNotNull(specs, "Список спецификаций не должен быть null");
        assertEquals(4, specs.size(), "Должно быть распарсено ровно 4 компании");
        assertEquals(2, specs.get(0));
        assertEquals(3, specs.get(3));
    }

    @Test
    void testParseTableCountThrowsRestaurantException() {
        List<String> badLines = Collections.singletonList("НЕ_ЧИСЛО");

        RestaurantException exception = assertThrows(RestaurantException.class, () -> {
            parser.parseTableCount(badLines);
        });

        assertTrue(exception.getMessage().contains("Ошибка парсинга количества столов"));
        assertNotNull(exception.getCause(), "Первопричина (cause) должна быть сохранена");
        assertTrue(exception.getCause() instanceof NumberFormatException);
    }

    @Test
    void testParseTotalDishesWithMissingLine() {
        List<String> incompleteLines = Collections.singletonList("3");

        assertThrows(RestaurantException.class, () -> {
            parser.parseTotalDishes(incompleteLines);
        }, "Метод должен упасть с RestaurantException, если строк не хватает");
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

        assertTrue(exception.getMessage().contains("Конфигурация нарушена"));
    }
}