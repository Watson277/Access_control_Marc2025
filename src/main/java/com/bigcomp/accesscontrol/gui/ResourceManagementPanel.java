package com.bigcomp.accesscontrol.gui;

import com.bigcomp.accesscontrol.dao.ResourceDAO;
import com.bigcomp.accesscontrol.dao.ResourceDAOImpl;
import com.bigcomp.accesscontrol.model.Resource;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Resource management panel for the Access Control System.
 * Provides GUI for viewing resources.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class ResourceManagementPanel extends JPanel {
    /** Data access object for resources */
    private ResourceDAO resourceDAO;
    
    /** Table displaying all resources */
    private JTable resourceTable;
    
    /** Table model for the resource table */
    private DefaultTableModel tableModel;

    /**
     * Constructor for ResourceManagementPanel
     * Initializes components and sets up the layout
     */
    public ResourceManagementPanel() {
        this.resourceDAO = new ResourceDAOImpl();
        initializeComponents();
        setupLayout();
        loadResources();
    }

    private void initializeComponents() {
        tableModel = new DefaultTableModel(new String[]{"Resource ID", "Resource Name", "Location", "Type", "State"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resourceTable = new JTable(tableModel);
        resourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadResources());
        buttonPanel.add(refreshButton);

        JScrollPane scrollPane = new JScrollPane(resourceTable);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadResources() {
        tableModel.setRowCount(0);
        List<Resource> resources = resourceDAO.getAllResources();
        for (Resource resource : resources) {
            tableModel.addRow(new Object[]{
                    resource.getResourceId(),
                    resource.getResourceName(),
                    resource.getLocation(),
                    resource.getResourceType(),
                    resource.getState()
            });
        }
    }
}

