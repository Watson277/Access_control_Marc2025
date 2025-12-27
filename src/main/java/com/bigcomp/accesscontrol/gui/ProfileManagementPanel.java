package com.bigcomp.accesscontrol.gui;

import com.bigcomp.accesscontrol.dao.ProfileDAO;
import com.bigcomp.accesscontrol.dao.ProfileDAOImpl;
import com.bigcomp.accesscontrol.model.Profile;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Profile management panel for the Access Control System.
 * Provides GUI for viewing profiles.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class ProfileManagementPanel extends JPanel {
    /** Data access object for profiles */
    private ProfileDAO profileDAO;
    
    /** Table displaying all profiles */
    private JTable profileTable;
    
    /** Table model for the profile table */
    private DefaultTableModel tableModel;

    /**
     * Constructor for ProfileManagementPanel
     * Initializes components and sets up the layout
     */
    public ProfileManagementPanel() {
        this.profileDAO = new ProfileDAOImpl();
        initializeComponents();
        setupLayout();
        loadProfiles();
    }

    private void initializeComponents() {
        tableModel = new DefaultTableModel(new String[]{"Profile Name", "File Path", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        profileTable = new JTable(tableModel);
        profileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadProfiles());
        buttonPanel.add(refreshButton);

        JScrollPane scrollPane = new JScrollPane(profileTable);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadProfiles() {
        tableModel.setRowCount(0);
        List<Profile> profiles = profileDAO.getAllProfiles();
        for (Profile profile : profiles) {
            tableModel.addRow(new Object[]{
                    profile.getProfileName(),
                    profile.getFilePath(),
                    profile.getDescription()
            });
        }
    }
}

