package com.bigcomp.accesscontrol.gui;

import com.bigcomp.accesscontrol.model.AccessRequest;
import com.bigcomp.accesscontrol.service.AccessControlService;
import com.bigcomp.accesscontrol.service.CachedAccessControlService;
import com.bigcomp.accesscontrol.util.AccessControlCache;
import com.bigcomp.accesscontrol.util.ResourceLocationMapper;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * Real-time monitoring panel for the Access Control System.
 * Provides GUI for monitoring access requests in real-time, simulating access attempts,
 * and viewing activity logs.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class MonitoringPanel extends JPanel {
    /** Cached access control service for real-time simulation */
    private CachedAccessControlService cachedAccessControlService;
    
    /** Cache instance */
    private AccessControlCache cache;
    
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
    
    /** Real-time simulation executor */
    private ScheduledExecutorService simulationExecutor;
    
    /** Real-time simulation future (for cancellation) */
    private ScheduledFuture<?> simulationFuture;
    
    /** Random number generator for simulation */
    private Random random;
    
    /** Button to start/stop real-time simulation */
    private JButton realTimeSimButton;
    
    /** Whether real-time simulation is running */
    private boolean isSimulationRunning;
    
    /** Floor plan image */
    private BufferedImage floorPlanImage;
    
    /** Resource location mapper */
    private ResourceLocationMapper locationMapper;
    
    /** Map of resource ID to latest access status for display */
    private Map<String, AccessRequest.AccessStatus> resourceStatusMap;

    /**
     * Constructor for MonitoringPanel
     * Initializes components and sets up the layout
     */
    public MonitoringPanel() {
        this.cache = AccessControlCache.getInstance();
        this.cachedAccessControlService = new CachedAccessControlService(cache);
        this.recentRequests = new ArrayList<>();
        this.random = new Random();
        this.isSimulationRunning = false;
        this.simulationExecutor = Executors.newScheduledThreadPool(1);
        this.locationMapper = ResourceLocationMapper.getInstance();
        this.resourceStatusMap = new HashMap<>();
        
        // Load floor plan image
        loadFloorPlanImage();
        
        // Initialize cache if not already initialized
        if (!cache.isInitialized()) {
            boolean success = cache.initialize();
            if (!success) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to initialize cache. Real-time simulation may not work properly.",
                    "Cache Initialization Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        initializeComponents();
        setupLayout();
        startMonitoring();
    }

    /**
     * Loads the floor plan image from resources
     */
    private void loadFloorPlanImage() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/Map.png");
            if (imageStream != null) {
                floorPlanImage = ImageIO.read(imageStream);
                imageStream.close();
            } else {
                // Try loading from file system as fallback
                try {
                    java.io.File imageFile = new java.io.File("Map.png");
                    if (imageFile.exists()) {
                        floorPlanImage = ImageIO.read(imageFile);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load Map.png from file system: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load floor plan image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() {
        // Calculate panel size based on image or use default
        int panelWidth = floorPlanImage != null ? floorPlanImage.getWidth() : 1200;
        int panelHeight = floorPlanImage != null ? floorPlanImage.getHeight() : 800;
        
        sitePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Enable anti-aliasing for better image quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                // Draw floor plan image as background
                if (floorPlanImage != null) {
                    // Scale image to fit panel while maintaining aspect ratio
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    int imgWidth = floorPlanImage.getWidth();
                    int imgHeight = floorPlanImage.getHeight();
                    
                    double scaleX = (double) panelWidth / imgWidth;
                    double scaleY = (double) panelHeight / imgHeight;
                    double scale = Math.min(scaleX, scaleY);
                    
                    int scaledWidth = (int) (imgWidth * scale);
                    int scaledHeight = (int) (imgHeight * scale);
                    int x = (panelWidth - scaledWidth) / 2;
                    int y = (panelHeight - scaledHeight) / 2;
                    
                    g2d.drawImage(floorPlanImage, x, y, scaledWidth, scaledHeight, null);
                    
                    // Draw access status indicators on the image
                    // Scale coordinates to match scaled image
                    for (Map.Entry<String, AccessRequest.AccessStatus> entry : resourceStatusMap.entrySet()) {
                        String resourceId = entry.getKey();
                        AccessRequest.AccessStatus status = entry.getValue();
                        
                        Point location = locationMapper.getLocation(resourceId, imgWidth, imgHeight);
                        if (location != null) {
                            // Scale location to match scaled image
                            int markerX = x + (int) (location.x * scale);
                            int markerY = y + (int) (location.y * scale);
                            
                            // Draw colored circle (green for granted, red for denied)
                            if (status == AccessRequest.AccessStatus.GRANTED) {
                                g2d.setColor(new Color(0, 255, 0, 200)); // Green with transparency
                            } else {
                                g2d.setColor(new Color(255, 0, 0, 200)); // Red with transparency
                            }
                            
                            // Draw filled circle
                            int markerSize = (int) (15 * scale);
                            g2d.fillOval(markerX - markerSize/2, markerY - markerSize/2, markerSize, markerSize);
                            
                            // Draw border
                            g2d.setColor(Color.BLACK);
                            g2d.setStroke(new BasicStroke(2));
                            g2d.drawOval(markerX - markerSize/2, markerY - markerSize/2, markerSize, markerSize);
                        }
                    }
                } else {
                    // Fallback: draw placeholder if image not loaded
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.BLACK);
                    FontMetrics fm = g.getFontMetrics();
                    String message = "Floor plan image not found";
                    int textX = (getWidth() - fm.stringWidth(message)) / 2;
                    int textY = getHeight() / 2;
                    g.drawString(message, textX, textY);
                }
            }
        };
        
        // Set preferred size based on image or default
        sitePanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        sitePanel.setBorder(BorderFactory.createTitledBorder("Floor Plan - Real-time Access Status"));

        // Remove floorPanel as we only need one panel now
        floorPanel = null;

        activityLog = new JTextArea(10, 40);
        activityLog.setEditable(false);
        activityLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Single panel for floor plan visualization
        JScrollPane mapScrollPane = new JScrollPane(sitePanel);
        mapScrollPane.setBorder(BorderFactory.createTitledBorder("Floor Plan - Real-time Access Status"));
        mapScrollPane.setPreferredSize(new Dimension(1200, 600));

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
        
        // Real-time simulation button
        realTimeSimButton = new JButton("Start Real-time Simulation");
        realTimeSimButton.addActionListener(e -> toggleRealTimeSimulation());
        testPanel.add(realTimeSimButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(mapScrollPane, BorderLayout.CENTER);
        centerPanel.add(logScrollPane, BorderLayout.SOUTH);
        centerPanel.add(testPanel, BorderLayout.NORTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void startMonitoring() {
        // Refresh every 1 second for real-time updates
        refreshTimer = new Timer(1000, e -> {
            sitePanel.repaint();
        });
        refreshTimer.start();
    }

    private void simulateAccessRequest(String badgeId, String resourceId) {
        if (badgeId.isEmpty() || resourceId.isEmpty()) {
            activityLog.append("[Error] Badge ID and Resource ID cannot be empty\n");
            return;
        }
        
        // Get reader ID from resource
        String readerId = "BR001"; // Default
        if (cache.getResource(resourceId) != null && 
            cache.getResource(resourceId).getBadgeReaderId() != null) {
            readerId = cache.getResource(resourceId).getBadgeReaderId();
        }
        
        // Simulate access request using cached service
        AccessRequest request = cachedAccessControlService.processAccessRequest(
                badgeId, readerId, resourceId);
        
        recentRequests.add(request);
        if (recentRequests.size() > 10) {
            recentRequests.remove(0);
        }
        
        // Update resource status map for visualization
        resourceStatusMap.put(resourceId, request.getStatus());
        
        // Fade out status after 3 seconds
        final String finalResourceId = resourceId;
        ScheduledExecutorService fadeExecutor = Executors.newSingleThreadScheduledExecutor();
        fadeExecutor.schedule(() -> {
            SwingUtilities.invokeLater(() -> {
                resourceStatusMap.remove(finalResourceId);
                sitePanel.repaint();
            });
            fadeExecutor.shutdown();
        }, 3, TimeUnit.SECONDS);

        String statusIcon = request.getStatus() == AccessRequest.AccessStatus.GRANTED ? "✓" : "✗";
        String logEntry = String.format("[%s] %s Badge: %s, Resource: %s, Status: %s (%s)\n",
                request.getRequestTime().toLocalTime(),
                statusIcon,
                request.getBadgeId(),
                request.getResourceId(),
                request.getStatus() == AccessRequest.AccessStatus.GRANTED ? "Granted" : "Denied",
                request.getReason() != null ? request.getReason() : "");
        activityLog.append(logEntry);
        
        // Auto-scroll to bottom
        activityLog.setCaretPosition(activityLog.getDocument().getLength());

        // Trigger repaint
        sitePanel.repaint();
        
        // If access was granted, schedule reader unlock after 2 seconds
        if (request.getStatus() == AccessRequest.AccessStatus.GRANTED) {
            scheduleReaderUnlock(resourceId);
        }
    }
    
    /**
     * Schedules a reader to be unlocked after 2 seconds
     * @param resourceId the resource ID whose reader should be unlocked
     */
    private void scheduleReaderUnlock(String resourceId) {
        ScheduledExecutorService unlockExecutor = Executors.newSingleThreadScheduledExecutor();
        unlockExecutor.schedule(() -> {
            cache.unlockReader(resourceId);
            SwingUtilities.invokeLater(() -> {
                activityLog.append(String.format("[%s] Reader for Resource %s is now active again\n",
                    java.time.LocalTime.now(), resourceId));
                activityLog.setCaretPosition(activityLog.getDocument().getLength());
            });
            unlockExecutor.shutdown();
        }, 2, TimeUnit.SECONDS);
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
    
    /**
     * Toggles real-time simulation on/off
     */
    private void toggleRealTimeSimulation() {
        if (isSimulationRunning) {
            stopRealTimeSimulation();
        } else {
            startRealTimeSimulation();
        }
    }
    
    /**
     * Starts real-time simulation
     */
    private void startRealTimeSimulation() {
        if (!cache.isInitialized()) {
            JOptionPane.showMessageDialog(this,
                "Cache is not initialized. Cannot start real-time simulation.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<String> activeBadges = cache.getActiveBadgeIds();
        List<String> allResources = cache.getAllResourceIds();
        
        if (activeBadges.isEmpty() || allResources.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No active badges or resources found. Cannot start simulation.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        isSimulationRunning = true;
        realTimeSimButton.setText("Stop Real-time Simulation");
        activityLog.append("\n========== Real-time Simulation Started ==========\n");
        activityLog.append("Random badges will access random resources every 3 seconds.\n");
        activityLog.append("Readers will be locked for 2 seconds after successful access.\n");
        activityLog.append("==================================================\n\n");
        activityLog.setCaretPosition(activityLog.getDocument().getLength());
        
        // Start simulation task
        simulationFuture = simulationExecutor.scheduleAtFixedRate(() -> {
            if (!isSimulationRunning) {
                return;
            }
            
            // Randomly select a badge and resource
            String randomBadgeId = activeBadges.get(random.nextInt(activeBadges.size()));
            String randomResourceId = allResources.get(random.nextInt(allResources.size()));
            
            // Process access request on EDT
            SwingUtilities.invokeLater(() -> {
                simulateAccessRequest(randomBadgeId, randomResourceId);
            });
        }, 0, 3, TimeUnit.SECONDS);
    }
    
    /**
     * Stops real-time simulation
     */
    private void stopRealTimeSimulation() {
        isSimulationRunning = false;
        if (simulationFuture != null) {
            simulationFuture.cancel(false);
        }
        realTimeSimButton.setText("Start Real-time Simulation");
        activityLog.append("\n========== Real-time Simulation Stopped ==========\n\n");
        activityLog.setCaretPosition(activityLog.getDocument().getLength());
    }
    
    /**
     * Cleanup method to stop simulation when panel is destroyed
     */
    public void cleanup() {
        stopRealTimeSimulation();
        if (simulationExecutor != null) {
            simulationExecutor.shutdown();
        }
    }
}

