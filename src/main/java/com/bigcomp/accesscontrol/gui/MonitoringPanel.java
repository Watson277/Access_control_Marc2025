package com.bigcomp.accesscontrol.gui;

import com.bigcomp.accesscontrol.model.AccessRequest;
import com.bigcomp.accesscontrol.service.AccessControlService;
import com.bigcomp.accesscontrol.service.AccessControlServiceImpl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Real-time monitoring panel for the Access Control System.
 * Provides GUI for monitoring access requests in real-time, simulating access attempts,
 * and viewing activity logs.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class MonitoringPanel extends JPanel {
    /** Service for processing access requests */
    private AccessControlService accessControlService;
    
    /** Panel for site visualization */
    private JPanel sitePanel;
    
    /** Panel for floor visualization */
    private JPanel floorPanel;
    
    /** Text area for displaying activity logs */
    private JTextArea activityLog;
    
    /** List of recent access requests */
    private List<AccessRequest> recentRequests;
    
    /** Timer for refreshing the display */
    private Timer refreshTimer;

    /**
     * Constructor for MonitoringPanel
     * Initializes components and sets up the layout
     */
    public MonitoringPanel() {
        this.accessControlService = new AccessControlServiceImpl();
        this.recentRequests = new ArrayList<>();
        initializeComponents();
        setupLayout();
        startMonitoring();
    }

    private void initializeComponents() {
        sitePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw site floor plan
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(50, 50, 200, 150);
                g.setColor(Color.BLACK);
                g.drawString("Building A", 100, 100);
                
                // Draw access points (green=granted, red=denied)
                for (AccessRequest request : recentRequests) {
                    if (request.getStatus() == AccessRequest.AccessStatus.GRANTED) {
                        g.setColor(Color.GREEN);
                    } else {
                        g.setColor(Color.RED);
                    }
                    g.fillOval(100, 100, 10, 10);
                }
            }
        };
        sitePanel.setPreferredSize(new Dimension(300, 250));
        sitePanel.setBorder(BorderFactory.createTitledBorder("Site Floor Plan"));

        floorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw floor plan
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(50, 50, 200, 150);
                g.setColor(Color.BLACK);
                g.drawString("Floor 1", 100, 100);
            }
        };
        floorPanel.setPreferredSize(new Dimension(300, 250));
        floorPanel.setBorder(BorderFactory.createTitledBorder("Floor Plan"));

        activityLog = new JTextArea(10, 40);
        activityLog.setEditable(false);
        activityLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel visualizationPanel = new JPanel(new GridLayout(1, 2));
        visualizationPanel.add(sitePanel);
        visualizationPanel.add(floorPanel);

        JScrollPane logScrollPane = new JScrollPane(activityLog);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Activity Log"));

        JPanel testPanel = new JPanel(new FlowLayout());
        
        // Add badge ID input
        testPanel.add(new JLabel("Badge ID:"));
        JTextField badgeIdField = new JTextField("BX76Z541", 10);
        testPanel.add(badgeIdField);
        
        // Add resource ID input
        testPanel.add(new JLabel("Resource ID:"));
        JTextField resourceIdField = new JTextField("RES001", 10);
        testPanel.add(resourceIdField);
        
        // Simulate button
        JButton testButton = new JButton("Simulate Access");
        testButton.addActionListener(e -> simulateAccessRequest(
            badgeIdField.getText().trim(), 
            resourceIdField.getText().trim()
        ));
        testPanel.add(testButton);
        
        // Quick test button
        JButton quickTestButton = new JButton("Quick Test");
        quickTestButton.addActionListener(e -> runQuickTests());
        testPanel.add(quickTestButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(visualizationPanel, BorderLayout.NORTH);
        centerPanel.add(logScrollPane, BorderLayout.CENTER);
        centerPanel.add(testPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void startMonitoring() {
        // Refresh every 2 seconds
        refreshTimer = new Timer(2000, e -> {
            sitePanel.repaint();
            floorPanel.repaint();
        });
        refreshTimer.start();
    }

    private void simulateAccessRequest(String badgeId, String resourceId) {
        if (badgeId.isEmpty() || resourceId.isEmpty()) {
            activityLog.append("[Error] Badge ID and Resource ID cannot be empty\n");
            return;
        }
        
        // Simulate access request (using fixed reader ID BR001)
        AccessRequest request = accessControlService.processAccessRequest(
                badgeId, "BR001", resourceId);
        
        recentRequests.add(request);
        if (recentRequests.size() > 10) {
            recentRequests.remove(0);
        }

        String statusIcon = request.getStatus() == AccessRequest.AccessStatus.GRANTED ? "✓" : "✗";
        String logEntry = String.format("[%s] %s Badge: %s, Resource: %s, Status: %s (%s)\n",
                request.getRequestTime().toLocalTime(),
                statusIcon,
                request.getBadgeId(),
                request.getResourceId(),
                request.getStatus() == AccessRequest.AccessStatus.GRANTED ? "Granted" : "Denied",
                request.getReason() != null ? request.getReason() : "");
        activityLog.append(logEntry);

        // Trigger repaint
        sitePanel.repaint();
        floorPanel.repaint();
    }
    
    private void runQuickTests() {
        activityLog.append("\n========== Starting Quick Tests ==========\n");
        
        // Test 1: Valid badge accessing main entrance
        simulateAccessRequest("BX76Z541", "RES001");
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        // Test 2: Valid badge accessing office
        simulateAccessRequest("BR59KA87", "RES002");
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        // Test 3: Invalid badge
        simulateAccessRequest("INVALID001", "RES001");
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        // Test 4: Non-existent resource
        simulateAccessRequest("BX76Z541", "RES999");
        
        activityLog.append("========== Tests Completed ==========\n\n");
    }
}

