package com.bigcomp.accesscontrol.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main window for the Access Control System application.
 * Contains a tabbed pane with multiple management panels.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class MainWindow extends JFrame {
    /** Tabbed pane containing all management panels */
    private JTabbedPane tabbedPane;
    
    /** Panel for user management */
    private UserManagementPanel userManagementPanel;
    
    /** Panel for badge management */
    private BadgeManagementPanel badgeManagementPanel;
    
    /** Panel for resource management */
    private ResourceManagementPanel resourceManagementPanel;
    
    /** Panel for profile management */
    private ProfileManagementPanel profileManagementPanel;
    
    /** Panel for log search */
    private LogSearchPanel logSearchPanel;
    
    /** Panel for real-time monitoring */
    private MonitoringPanel monitoringPanel;

    /**
     * Constructor for MainWindow
     * Initializes components and sets up the layout
     */
    public MainWindow() {
        initializeComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("Access Control System - BigComp");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Add window close listener to cleanup resources
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cleanup();
                System.exit(0);
            }
        });
    }
    
    /**
     * Cleanup method to release resources when window is closed
     */
    private void cleanup() {
        if (monitoringPanel != null) {
            monitoringPanel.cleanup();
        }
    }

    /**
     * Initializes all GUI components
     */
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        userManagementPanel = new UserManagementPanel();
        badgeManagementPanel = new BadgeManagementPanel();
        resourceManagementPanel = new ResourceManagementPanel();
        profileManagementPanel = new ProfileManagementPanel();
        logSearchPanel = new LogSearchPanel();
        monitoringPanel = new MonitoringPanel();

        tabbedPane.addTab("User Management", userManagementPanel);
        tabbedPane.addTab("Badge Management", badgeManagementPanel);
        tabbedPane.addTab("Resource Management", resourceManagementPanel);
        tabbedPane.addTab("Profile Management", profileManagementPanel);
        tabbedPane.addTab("Log Search", logSearchPanel);
        tabbedPane.addTab("Real-time Monitoring", monitoringPanel);
    }

    /**
     * Sets up the layout of the main window
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        
        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Access Control System v1.0\n\n" +
                "Team Members:\n" +
                "[Team Member 1]\n" +
                "[Team Member 2]\n" +
                "[Team Member 3]\n\n" +
                "BigComp Headquarters Access Control Prototype System",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}

