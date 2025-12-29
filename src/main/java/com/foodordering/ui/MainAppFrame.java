package com.foodordering.ui;

import com.foodordering.dao.MenuItemDAO;
import com.foodordering.dao.OrderDAO;
import com.foodordering.dao.RestaurantDAO;
import com.foodordering.dao.UserDAO;
import com.foodordering.models.Cart;
import com.foodordering.models.User;
import com.foodordering.services.Session;

import javax.swing.*;
import java.awt.*;

/**
 * Main application home screen.
 * Displays welcome message, browse restaurants, view cart, and logout options.
 */
public class MainAppFrame extends JFrame {
    private final User currentUser;
    private final Cart cart;
    private final RestaurantDAO restaurantDAO;
    private final MenuItemDAO menuItemDAO;
    private final UserDAO userDAO;
    private final OrderDAO orderDAO;

    public MainAppFrame(User user) {
        this(user, new Cart(), new RestaurantDAO(), new MenuItemDAO());
    }

    public MainAppFrame(User user, Cart cart, RestaurantDAO restaurantDAO, MenuItemDAO menuItemDAO) {
        this.currentUser = user;
        this.cart = cart;
        this.restaurantDAO = restaurantDAO;
        this.menuItemDAO = menuItemDAO;
        this.userDAO = new UserDAO();
        this.orderDAO = new OrderDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Online Food Ordering - Home");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome panel
        JLabel welcome = new JLabel("Welcome, " + currentUser.getUsername(), SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(welcome, BorderLayout.NORTH);

        // Center panel with buttons
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton browseButton = new JButton("🏪 Browse Restaurants");
        browseButton.setFont(new Font("Arial", Font.PLAIN, 14));
        browseButton.addActionListener(e -> openRestaurantBrowser());

        JButton cartButton = new JButton("🛒 View Cart (" + cart.getItems().size() + " items)");
        cartButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cartButton.addActionListener(e -> openCartView());

        JButton profileButton = new JButton("👤 My Profile");
        profileButton.setFont(new Font("Arial", Font.PLAIN, 14));
        profileButton.addActionListener(e -> openProfileView());

        centerPanel.add(browseButton);
        centerPanel.add(cartButton);
        centerPanel.add(profileButton);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with logout
        JPanel bottomPanel = new JPanel();
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void openRestaurantBrowser() {
        RestaurantFrame restaurantFrame = new RestaurantFrame(cart, restaurantDAO, menuItemDAO);
        restaurantFrame.setVisible(true);
    }

    private void openCartView() {
        CartFrame cartFrame = new CartFrame(cart);
        cartFrame.setVisible(true);
    }

    private void handleLogout() {
        Session.getInstance().logout();
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
        dispose();
    }

    private void openProfileView() {
        ProfileFrame profileFrame = new ProfileFrame(currentUser, userDAO, orderDAO);
        profileFrame.setVisible(true);
    }

    // Testing helpers
    public Cart getCart() { return cart; }
}
