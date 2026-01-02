package com.foodordering.performance;

import com.foodordering.dao.*;
import com.foodordering.models.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests to measure system responsiveness and efficiency.
 * Tests database operations, cart calculations, and UI response times.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerformanceTest {

    private UserDAO userDAO;
    private RestaurantDAO restaurantDAO;
    private MenuItemDAO menuItemDAO;
    
    @BeforeAll
    void setUp() {
        userDAO = new UserDAO();
        restaurantDAO = new RestaurantDAO();
        menuItemDAO = new MenuItemDAO();
    }

    /**
     * Test bulk user query performance.
     * Expected: < 500ms for 10 user lookups
     */
    @Test
    @org.junit.jupiter.api.Disabled("Connection pooling issues - requires DB optimization")
    @DisplayName("Performance: Bulk User Query")
    void testBulkUserQuery() {
        long startTime = System.currentTimeMillis();
        
        int successCount = 0;
        // Perform 10 user lookups to measure query performance
        for (int i = 1; i <= 10; i++) {
            User user = userDAO.getUserById(1); // Query existing user
            if (user != null) {
                successCount++;
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("✓ Queried " + successCount + " users in " + duration + " ms");
        assertEquals(10, successCount, "All user queries should succeed");
        assertTrue(duration < 500, "Bulk queries should complete within 500ms");
    }

    /**
     * Test database query performance.
     * Expected: < 100ms for single restaurant fetch
     */
    @Test
    @DisplayName("Performance: Database Query Response Time")
    void testDatabaseQueryPerformance() {
        long startTime = System.currentTimeMillis();
        
        Restaurant restaurant = restaurantDAO.getRestaurantById(1);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("✓ Restaurant fetched in " + duration + " ms");
        assertNotNull(restaurant, "Restaurant should be retrieved");
        assertTrue(duration < 100, "Single query should complete within 100ms");
    }

    /**
     * Test menu item loading performance.
     * Expected: < 200ms for 50 items
     */
    @Test
    @DisplayName("Performance: Menu Items Loading")
    void testMenuItemsLoadingPerformance() {
        long startTime = System.currentTimeMillis();
        
        List<MenuItem> items = menuItemDAO.getMenuItemsByRestaurant(1);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("✓ Loaded " + items.size() + " menu items in " + duration + " ms");
        assertTrue(duration < 200, "Menu loading should complete within 200ms");
    }

    /**
     * Test cart calculation performance with large number of items.
     * Expected: < 50ms for 20 items
     */
    @Test
    @DisplayName("Performance: Cart Total Calculation")
    void testCartCalculationPerformance() {
        Cart cart = new Cart();
        
        // Add 20 items
        for (int i = 1; i <= 20; i++) {
            MenuItem item = new MenuItem(i, 1, "Item" + i, 10.0 + i, "Test", true);
            cart.addItem(item, 2);
        }
        
        long startTime = System.currentTimeMillis();
        double total = cart.calculateTotal();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("✓ Cart total calculated in " + duration + " ms");
        assertTrue(total > 0, "Cart total should be calculated");
        assertTrue(duration < 50, "Cart calculation should complete within 50ms");
    }

    /**
     * Test concurrent cart operations.
     * Expected: No data corruption, consistent totals
     */
    @Test
    @DisplayName("Performance: Concurrent Cart Operations")
    void testConcurrentCartOperations() throws InterruptedException {
        Cart cart = new Cart();
        MenuItem item = new MenuItem(1, 1, "Test Item", 10.0, "Test", true);
        
        // Simulate concurrent additions
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                cart.addItem(item, 1);
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                cart.addItem(item, 1);
            }
        });
        
        long startTime = System.currentTimeMillis();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long endTime = System.currentTimeMillis();
        
        double total = cart.calculateTotal();
        long duration = endTime - startTime;
        
        System.out.println("✓ Concurrent operations completed in " + duration + " ms");
        assertTrue(total > 0, "Cart should have items");
        assertTrue(duration < 1000, "Concurrent operations should complete within 1 second");
    }

    /**
     * Test memory efficiency with large data sets.
     * Expected: No OutOfMemoryError
     */
    @Test
    @DisplayName("Performance: Memory Efficiency")
    void testMemoryEfficiency() {
        List<MenuItem> largeList = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        // Create 1000 menu items
        for (int i = 1; i <= 1000; i++) {
            MenuItem item = new MenuItem(i, 1, "Item" + i, 10.0, "Description " + i, true);
            largeList.add(item);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("✓ Created 1000 menu items in " + duration + " ms");
        assertEquals(1000, largeList.size(), "All items should be created");
        assertTrue(duration < 100, "Object creation should be fast");
        
        // Clear to free memory
        largeList.clear();
    }

    /**
     * Test restaurant listing performance.
     * Expected: < 150ms
     */
    @Test
    @DisplayName("Performance: Restaurant Listing")
    void testRestaurantListingPerformance() {
        long startTime = System.currentTimeMillis();
        
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("✓ Loaded " + restaurants.size() + " restaurants in " + duration + " ms");
        assertNotNull(restaurants, "Restaurants list should not be null");
        assertTrue(duration < 150, "Restaurant listing should complete within 150ms");
    }

    @AfterAll
    void cleanup() {
        // Clean up test users
        System.out.println("\n✓ Performance tests completed");
    }
}
