package com.foodordering.ui;

import com.foodordering.dao.MenuItemDAO;
import com.foodordering.models.Cart;
import com.foodordering.models.MenuItem;
import com.foodordering.models.Restaurant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UI frame to display a restaurant menu and add items to cart.
 */
public class MenuFrame extends JFrame {
    private final Restaurant restaurant;
    private final Cart cart;
    private final MenuItemDAO menuItemDAO;

    private final String[] columns = {"Name", "Price", "Available"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable menuTable = new JTable(tableModel);
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private List<MenuItem> menuItems = new ArrayList<>();

    public MenuFrame(Restaurant restaurant, Cart cart, MenuItemDAO menuItemDAO) {
        this.restaurant = restaurant;
        this.cart = cart;
        this.menuItemDAO = menuItemDAO;
        initializeUI();
        loadMenu();
    }

    private void initializeUI() {
        setTitle("Menu - " + restaurant.getName());
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JScrollPane scrollPane = new JScrollPane(menuTable);

        JButton addButton = new JButton("Add to Cart");
        addButton.addActionListener(e -> addSelectedFromUI());

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Quantity:"));
        bottom.add(quantitySpinner);
        bottom.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    public void loadMenu() {
        tableModel.setRowCount(0);
        menuItems = menuItemDAO.getAvailableMenuItemsByRestaurant(restaurant.getRestaurantId());
        for (MenuItem item : menuItems) {
            tableModel.addRow(new Object[]{item.getName(), item.getPrice(), item.isAvailable()});
        }
    }

    private void addSelectedFromUI() {
        int row = menuTable.getSelectedRow();
        if (row < 0 || row >= menuItems.size()) {
            JOptionPane.showMessageDialog(this, "Please select an item.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int qty = (int) quantitySpinner.getValue();
        addSelectedToCart(row, qty);
        JOptionPane.showMessageDialog(this, "Added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void addSelectedToCart(int rowIndex, int quantity) {
        if (rowIndex < 0 || rowIndex >= menuItems.size()) {
            throw new IllegalArgumentException("Invalid row index");
        }
        MenuItem item = menuItems.get(rowIndex);
        cart.addItem(item, quantity);
    }

    // Testing helpers
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }
}
