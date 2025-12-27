package com.foodordering.ui;

import com.foodordering.dao.MenuItemDAO;
import com.foodordering.dao.RestaurantDAO;
import com.foodordering.dao.ReviewDAO;
import com.foodordering.models.Cart;
import com.foodordering.models.Restaurant;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UI frame to display restaurants and open their menus.
 */
public class RestaurantFrame extends JFrame {
    private final RestaurantDAO restaurantDAO;
    private final MenuItemDAO menuItemDAO;
    private final ReviewDAO reviewDAO;
    private final Cart cart;

    private final DefaultListModel<String> restaurantListModel = new DefaultListModel<>();
    private final JList<String> restaurantList = new JList<>(restaurantListModel);
    private List<Restaurant> restaurants = new ArrayList<>();

    public RestaurantFrame() {
        this(new Cart(), new RestaurantDAO(), new MenuItemDAO());
    }

    public RestaurantFrame(Cart cart, RestaurantDAO restaurantDAO, MenuItemDAO menuItemDAO) {
        this.cart = cart;
        this.restaurantDAO = restaurantDAO;
        this.menuItemDAO = menuItemDAO;
        this.reviewDAO = new ReviewDAO();
        initializeUI();
        loadRestaurants();
    }

    private void initializeUI() {
        setTitle("Restaurants");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        restaurantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(restaurantList);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton viewMenuButton = new JButton("View Menu");
        JButton viewReviewsButton = new JButton("⭐ Reviews");
        
        viewMenuButton.addActionListener(e -> openSelectedMenu());
        viewReviewsButton.addActionListener(e -> openSelectedReviews());

        buttonPanel.add(viewMenuButton);
        buttonPanel.add(viewReviewsButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadRestaurants() {
        restaurantListModel.clear();
        restaurants = restaurantDAO.getAllRestaurants();
        for (Restaurant r : restaurants) {
            restaurantListModel.addElement(r.getName());
        }
    }

    private void openSelectedMenu() {
        int idx = restaurantList.getSelectedIndex();
        if (idx < 0 || idx >= restaurants.size()) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Restaurant selected = restaurants.get(idx);
        MenuFrame menuFrame = new MenuFrame(selected, cart, menuItemDAO);
        menuFrame.setVisible(true);
    }

    private void openSelectedReviews() {
        int idx = restaurantList.getSelectedIndex();
        if (idx < 0 || idx >= restaurants.size()) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Restaurant selected = restaurants.get(idx);
        ReviewFrame reviewFrame = new ReviewFrame(selected, reviewDAO);
        reviewFrame.setVisible(true);
    }

    // Testing helpers
    public DefaultListModel<String> getRestaurantListModel() {
        return restaurantListModel;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }
}
