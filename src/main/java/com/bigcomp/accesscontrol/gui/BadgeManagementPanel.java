package com.bigcomp.accesscontrol.gui;

import com.bigcomp.accesscontrol.dao.BadgeDAO;
import com.bigcomp.accesscontrol.dao.BadgeDAOImpl;
import com.bigcomp.accesscontrol.dao.ResourceDAO;
import com.bigcomp.accesscontrol.dao.ResourceDAOImpl;
import com.bigcomp.accesscontrol.model.Badge;
import com.bigcomp.accesscontrol.model.Resource;
import com.bigcomp.accesscontrol.service.PermissionService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Badge management panel for the Access Control System.
 * Provides GUI for managing badges, including creating, activating/deactivating,
 * deleting badges, and managing badge permissions.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class BadgeManagementPanel extends JPanel {
    /** Data access object for badges */
    private BadgeDAO badgeDAO;
    
    /** Data access object for resources */
    private ResourceDAO resourceDAO;
    
    /** Service for managing badge permissions */
    private PermissionService permissionService;
    
    /** Table displaying all badges */
    private JTable badgeTable;
    
    /** Table model for the badge table */
    private DefaultTableModel tableModel;
    
    /** Text field for badge ID input */
    private JTextField badgeIdField;
    
    /** Text field for user ID input */
    private JTextField userIdField;
    
    /** Text field for expiration date input */
    private JTextField expirationDateField;

    /**
     * Constructor for BadgeManagementPanel
     * Initializes components and sets up the layout
     */
    public BadgeManagementPanel() {
        this.badgeDAO = new BadgeDAOImpl();
        this.resourceDAO = new ResourceDAOImpl();
        this.permissionService = new PermissionService();
        initializeComponents();
        setupLayout();
        loadBadges();
    }

    private void initializeComponents() {
        tableModel = new DefaultTableModel(new String[]{"Badge ID", "User ID", "Created Date", "Expiration Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        badgeTable = new JTable(tableModel);
        badgeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        badgeIdField = new JTextField(20);
        userIdField = new JTextField(20);
        expirationDateField = new JTextField(20);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top: Input form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Badge ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(badgeIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(userIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Expiration Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(expirationDateField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Badge");
        JButton deactivateButton = new JButton("Deactivate Badge");
        JButton deleteButton = new JButton("Delete Badge");
        JButton managePermissionButton = new JButton("Manage Permissions");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addBadge());
        deactivateButton.addActionListener(e -> deactivateBadge());
        deleteButton.addActionListener(e -> deleteBadge());
        managePermissionButton.addActionListener(e -> managePermissions());
        refreshButton.addActionListener(e -> loadBadges());

        buttonPanel.add(addButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(managePermissionButton);
        buttonPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Middle: Table
        JScrollPane scrollPane = new JScrollPane(badgeTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadBadges() {
        tableModel.setRowCount(0);
        List<Badge> badges = badgeDAO.getAllBadges();
        for (Badge badge : badges) {
            tableModel.addRow(new Object[]{
                    badge.getBadgeId(),
                    badge.getUserId(),
                    badge.getCreatedDate(),
                    badge.getExpirationDate(),
                    badge.isActive() ? "Active" : "Inactive"
            });
        }
    }

    private void addBadge() {
        try {
            Badge badge = new Badge();
            badge.setBadgeId(badgeIdField.getText().trim());
            badge.setUserId(userIdField.getText().trim());
            badge.setCreatedDate(LocalDate.now());
            badge.setLastUpdateDate(LocalDate.now());
            badge.setActive(true);
            
            String expDateStr = expirationDateField.getText().trim();
            if (!expDateStr.isEmpty()) {
                badge.setExpirationDate(LocalDate.parse(expDateStr));
            }

            if (badgeDAO.insertBadge(badge)) {
                JOptionPane.showMessageDialog(this, "Badge added successfully");
                clearForm();
                loadBadges();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add badge", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deactivateBadge() {
        int selectedRow = badgeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a badge to deactivate", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String badgeId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to deactivate badge " + badgeId + "?\nThe badge will be unusable after deactivation, but data will be retained.", 
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Badge badge = badgeDAO.getBadgeById(badgeId);
            if (badge != null) {
                badge.setActive(false);
                badge.setLastUpdateDate(LocalDate.now());
                if (badgeDAO.updateBadge(badge)) {
                    JOptionPane.showMessageDialog(this, "Badge deactivated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBadges();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to deactivate badge, please check console for error details", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteBadge() {
        int selectedRow = badgeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a badge to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String badgeId = (String) tableModel.getValueAt(selectedRow, 0);
        String userId = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Warning: Deletion is irreversible
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to permanently delete badge " + badgeId + "?\n\n" +
                "Warning: This operation cannot be undone!\n" +
                "The following will also be deleted:\n" +
                "- All permission configurations for this badge\n" +
                "- All access record associations for this badge\n\n" +
                "User " + userId + " data will not be deleted.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Second confirmation
            int secondConfirm = JOptionPane.showConfirmDialog(this,
                    "Final confirmation: Are you really sure you want to delete badge " + badgeId + "?\nThis operation cannot be undone!",
                    "Final Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (secondConfirm == JOptionPane.YES_OPTION) {
                if (badgeDAO.deleteBadge(badgeId)) {
                    JOptionPane.showMessageDialog(this, "Badge deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBadges();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete badge, please check console for error details", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void clearForm() {
        badgeIdField.setText("");
        userIdField.setText("");
        expirationDateField.setText("");
    }

    /**
     * Opens the permission management dialog
     */
    private void managePermissions() {
        int selectedRow = badgeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a badge to manage permissions", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String badgeId = (String) tableModel.getValueAt(selectedRow, 0);
        Badge badge = badgeDAO.getBadgeById(badgeId);
        if (badge == null) {
            JOptionPane.showMessageDialog(this, "Badge does not exist", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open permission management dialog
        new PermissionManagementDialog(badgeId).setVisible(true);
    }

    /**
     * Permission management dialog
     */
    private class PermissionManagementDialog extends JDialog {
        private String badgeId;
        private JTable currentResourcesTable;
        private JTable allResourcesTable;
        private DefaultTableModel currentResourcesModel;
        private DefaultTableModel allResourcesModel;

        public PermissionManagementDialog(String badgeId) {
            super((Frame) SwingUtilities.getWindowAncestor(BadgeManagementPanel.this), "Manage Badge Permissions", true);
            this.badgeId = badgeId;
            initializeDialog();
            loadCurrentPermissions();
            loadAllResources();
        }

        private void initializeDialog() {
            setLayout(new BorderLayout());
            setSize(800, 600);
            setLocationRelativeTo(getParent());

            // Top: Badge information
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.add(new JLabel("Badge ID: " + badgeId));
            add(infoPanel, BorderLayout.NORTH);

            // Middle: Left and right panels
            JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Left: Currently accessible resources
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBorder(BorderFactory.createTitledBorder("Currently Accessible Resources"));
            currentResourcesModel = new DefaultTableModel(new String[]{"Resource ID", "Resource Name", "Location", "Type"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            currentResourcesTable = new JTable(currentResourcesModel);
            leftPanel.add(new JScrollPane(currentResourcesTable), BorderLayout.CENTER);

            // Right: All available resources
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBorder(BorderFactory.createTitledBorder("All Available Resources"));
            allResourcesModel = new DefaultTableModel(new String[]{"Resource ID", "Resource Name", "Location", "Type"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            allResourcesTable = new JTable(allResourcesModel);
            rightPanel.add(new JScrollPane(allResourcesTable), BorderLayout.CENTER);

            contentPanel.add(leftPanel);
            contentPanel.add(rightPanel);
            add(contentPanel, BorderLayout.CENTER);

            // Bottom: Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton addButton = new JButton("Add Selected Resource");
            JButton removeButton = new JButton("Remove Selected Resource");
            JButton refreshButton = new JButton("Refresh");
            JButton closeButton = new JButton("Close");

            addButton.addActionListener(e -> addSelectedResource());
            removeButton.addActionListener(e -> removeSelectedResource());
            refreshButton.addActionListener(e -> {
                loadCurrentPermissions();
                loadAllResources();
            });
            closeButton.addActionListener(e -> dispose());

            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void loadCurrentPermissions() {
            currentResourcesModel.setRowCount(0);
            List<Resource> resources = permissionService.getBadgeAccessibleResources(badgeId);
            for (Resource resource : resources) {
                currentResourcesModel.addRow(new Object[]{
                        resource.getResourceId(),
                        resource.getResourceName(),
                        resource.getLocation(),
                        resource.getResourceType()
                });
            }
        }

        private void loadAllResources() {
            allResourcesModel.setRowCount(0);
            List<Resource> allResources = resourceDAO.getAllResources();
            List<Resource> accessibleResources = permissionService.getBadgeAccessibleResources(badgeId);
            
            // Only show inaccessible resources
            for (Resource resource : allResources) {
                boolean isAccessible = accessibleResources.stream()
                        .anyMatch(r -> r.getResourceId().equals(resource.getResourceId()));
                if (!isAccessible) {
                    allResourcesModel.addRow(new Object[]{
                            resource.getResourceId(),
                            resource.getResourceName(),
                            resource.getLocation(),
                            resource.getResourceType()
                    });
                }
            }
        }

        private void addSelectedResource() {
            int selectedRow = allResourcesTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a resource to add", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String resourceId = (String) allResourcesModel.getValueAt(selectedRow, 0);
            String resourceName = (String) allResourcesModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to add access permission for resource " + resourceName + " to badge " + badgeId + "?",
                    "Confirm Add", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (permissionService.addResourceAccessToBadge(badgeId, resourceId)) {
                    JOptionPane.showMessageDialog(this, "Permission added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCurrentPermissions();
                    loadAllResources();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add permission, please check console for error details", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void removeSelectedResource() {
            int selectedRow = currentResourcesTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a resource to remove", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String resourceId = (String) currentResourcesModel.getValueAt(selectedRow, 0);
            String resourceName = (String) currentResourcesModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove access permission for resource " + resourceName + " from badge " + badgeId + "?",
                    "Confirm Remove", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (permissionService.removeResourceAccessFromBadge(badgeId, resourceId)) {
                    JOptionPane.showMessageDialog(this, "Permission removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCurrentPermissions();
                    loadAllResources();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove permission, please check console for error details", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}

