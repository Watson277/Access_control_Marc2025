package com.bigcomp.accesscontrol.gui;

import com.bigcomp.accesscontrol.util.LogManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Log search panel for the Access Control System.
 * Provides GUI for searching and viewing access logs by date, badge ID, and resource ID.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class LogSearchPanel extends JPanel {
    /** Table displaying search results */
    private JTable logTable;
    
    /** Table model for the log table */
    private DefaultTableModel tableModel;
    
    /** Text field for year input */
    private JTextField yearField;
    
    /** Text field for month input */
    private JTextField monthField;
    
    /** Text field for day input */
    private JTextField dayField;
    
    /** Text field for badge ID filter */
    private JTextField badgeIdField;
    
    /** Text field for resource ID filter */
    private JTextField resourceIdField;

    /**
     * Constructor for LogSearchPanel
     * Initializes components and sets up the layout
     */
    public LogSearchPanel() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        tableModel = new DefaultTableModel(new String[]{
                "Date", "Time", "Badge ID", "Reader ID", "Resource ID", "User ID", "User Name", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        logTable = new JTable(tableModel);

        yearField = new JTextField(10);
        monthField = new JTextField(10);
        dayField = new JTextField(10);
        badgeIdField = new JTextField(20);
        resourceIdField = new JTextField(20);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top: Search form
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("Month:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(monthField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        searchPanel.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(dayField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        searchPanel.add(new JLabel("Badge ID:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(badgeIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        searchPanel.add(new JLabel("Resource ID:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(resourceIdField, gbc);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchLogs());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Middle: Table
        JScrollPane scrollPane = new JScrollPane(logTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void searchLogs() {
        tableModel.setRowCount(0);

        try {
            int year = Integer.parseInt(yearField.getText().trim());
            int month = Integer.parseInt(monthField.getText().trim());
            int day = Integer.parseInt(dayField.getText().trim());

            String monthDir = String.format("%02d", month);
            String fileName = String.format("%04d-%02d-%02d.csv", year, month, day);
            Path logFile = Paths.get("logs", String.valueOf(year), monthDir, fileName);

            if (!Files.exists(logFile)) {
                JOptionPane.showMessageDialog(this, "Log file does not exist");
                return;
            }

            String badgeFilter = badgeIdField.getText().trim().toLowerCase();
            String resourceFilter = resourceIdField.getText().trim().toLowerCase();

            try (BufferedReader br = Files.newBufferedReader(logFile)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 8) {
                        String badgeId = parts[4].toLowerCase();
                        String resourceId = parts[6].toLowerCase();

                        // Apply filters
                        if ((badgeFilter.isEmpty() || badgeId.contains(badgeFilter)) &&
                            (resourceFilter.isEmpty() || resourceId.contains(resourceFilter))) {
                            tableModel.addRow(new Object[]{
                                    parts[0] + "-" + parts[1] + "-" + parts[2],
                                    parts[4],
                                    parts[5],
                                    parts[6],
                                    parts[7],
                                    parts[8],
                                    parts[9],
                                    parts[10]
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to search logs: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

