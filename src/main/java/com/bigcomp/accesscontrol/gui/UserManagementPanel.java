package com.bigcomp.accesscontrol.gui;

import com.bigcomp.accesscontrol.dao.UserDAO;
import com.bigcomp.accesscontrol.dao.UserDAOImpl;
import com.bigcomp.accesscontrol.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * User management panel for the Access Control System.
 * Provides GUI for adding, updating, deleting, and viewing users.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class UserManagementPanel extends JPanel {
    /** Data access object for users */
    private UserDAO userDAO;
    
    /** Table displaying all users */
    private JTable userTable;
    
    /** Table model for the user table */
    private DefaultTableModel tableModel;
    
    /** Text field for user ID input */
    private JTextField userIdField;
    
    /** Combo box for gender selection */
    private JComboBox<User.Gender> genderCombo;
    
    /** Text field for first name input */
    private JTextField firstNameField;
    
    /** Text field for last name input */
    private JTextField lastNameField;
    
    /** Combo box for user type selection */
    private JComboBox<User.UserType> userTypeCombo;

    /**
     * Constructor for UserManagementPanel
     * Initializes components and sets up the layout
     */
    public UserManagementPanel() {
        this.userDAO = new UserDAOImpl();
        initializeComponents();
        setupLayout();
        loadUsers();
    }

    private void initializeComponents() {
        tableModel = new DefaultTableModel(new String[]{"User ID", "Name", "Gender", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        userIdField = new JTextField(20);
        genderCombo = new JComboBox<>(User.Gender.values());
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        userTypeCombo = new JComboBox<>(User.UserType.values());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top: Input form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(userIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(userTypeCombo, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add User");
        JButton updateButton = new JButton("Update User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addUser());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUsers());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Middle: Table
        JScrollPane scrollPane = new JScrollPane(userTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getFirstName() + " " + user.getLastName(),
                    user.getGender(),
                    user.getUserType()
            });
        }
    }

    private void addUser() {
        try {
            // Validate input
            String userId = userIdField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            
            if (userId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "User ID cannot be empty", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (firstName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name cannot be empty", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Last name cannot be empty", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check if user ID already exists
            if (userDAO.getUserById(userId) != null) {
                JOptionPane.showMessageDialog(this, "User ID already exists: " + userId, "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            User user = new User();
            user.setUserId(userId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setGender((User.Gender) genderCombo.getSelectedItem());
            user.setUserType((User.UserType) userTypeCombo.getSelectedItem());

            if (userDAO.insertUser(user)) {
                JOptionPane.showMessageDialog(this, "User added successfully");
                clearForm();
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user\nPlease check console for error details", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage() + "\nPlease check console for details", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to update");
            return;
        }

        try {
            String userId = (String) tableModel.getValueAt(selectedRow, 0);
            User user = userDAO.getUserById(userId);
            if (user != null) {
                user.setFirstName(firstNameField.getText().trim());
                user.setLastName(lastNameField.getText().trim());
                user.setGender((User.Gender) genderCombo.getSelectedItem());
                user.setUserType((User.UserType) userTypeCombo.getSelectedItem());

                if (userDAO.updateUser(user)) {
                    JOptionPane.showMessageDialog(this, "User updated successfully");
                    clearForm();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update user", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String userId = (String) tableModel.getValueAt(selectedRow, 0);
            if (userDAO.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        userIdField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        genderCombo.setSelectedIndex(0);
        userTypeCombo.setSelectedIndex(0);
    }
}

