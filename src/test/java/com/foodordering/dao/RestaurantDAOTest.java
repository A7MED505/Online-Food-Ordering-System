package com.foodordering.dao;

import com.foodordering.models.Restaurant;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestaurantDAOTest {

    private static RestaurantDAO restaurantDAO;

    @BeforeAll
    static void setup() {
        restaurantDAO = new RestaurantDAO();
    }

    @AfterEach
    void cleanup() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM restaurants WHERE name LIKE 'DAO Test %'");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test add and get restaurant by id")
    void testGetRestaurantById() {
        Restaurant r = new Restaurant(0, "DAO Test R1", "Addr1", "555-1001", 4.5);
        assertTrue(restaurantDAO.addRestaurant(r));
        assertTrue(r.getRestaurantId() > 0);

        Restaurant fromDb = restaurantDAO.getRestaurantById(r.getRestaurantId());
        assertNotNull(fromDb);
        assertEquals("DAO Test R1", fromDb.getName());
        assertEquals(4.5, fromDb.getRating(), 0.001);
    }

    @Test
    @Order(2)
    @DisplayName("Test get all restaurants")
    void testGetAllRestaurants() {
        Restaurant r1 = new Restaurant(0, "DAO Test R2", "Addr2", "555-1002", 3.8);
        Restaurant r2 = new Restaurant(0, "DAO Test R3", "Addr3", "555-1003", 4.2);
        restaurantDAO.addRestaurant(r1);
        restaurantDAO.addRestaurant(r2);

        List<Restaurant> list = restaurantDAO.getAllRestaurants();
        assertNotNull(list);
        assertTrue(list.size() >= 2);
        boolean foundR2 = list.stream().anyMatch(x -> "DAO Test R2".equals(x.getName()));
        boolean foundR3 = list.stream().anyMatch(x -> "DAO Test R3".equals(x.getName()));
        assertTrue(foundR2 && foundR3);
    }
}
