package com.bigcomp.accesscontrol;

import javax.swing.SwingUtilities;

import com.bigcomp.accesscontrol.gui.MainWindow;

/**
 * Main entry point for the Access Control System application.
 * Initializes the GUI and displays the main window.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class Main {
    /**
     * Main method to start the application
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Failed to set look and feel: " + e.getMessage());
            }

            // Create and display main window
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}

