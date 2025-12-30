package com.bigcomp.accesscontrol.util;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps resource IDs to their locations (coordinates) on the floor plan image.
 * This class provides a mapping between resource IDs and their visual positions
 * on the Map.png floor plan.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class ResourceLocationMapper {
    /** Singleton instance */
    private static ResourceLocationMapper instance;
    
    /** Map from resource ID to Point (x, y coordinates on the image) */
    private Map<String, Point> resourceLocations;
    
    /**
     * Private constructor for singleton pattern
     */
    private ResourceLocationMapper() {
        resourceLocations = new HashMap<>();
        initializeDefaultLocations();
    }
    
    /**
     * Gets the singleton instance
     * @return the singleton instance
     */
    public static synchronized ResourceLocationMapper getInstance() {
        if (instance == null) {
            instance = new ResourceLocationMapper();
        }
        return instance;
    }
    
    /**
     * Initializes default resource locations on the floor plan.
     * Coordinates are relative to the image size.
     * These coordinates need to be adjusted based on the actual Map.png image dimensions.
     * 
     * Based on the image description:
     * - Red dots: Server Room entrance, Passenger Elevators
     * - Green dots: Offices 4/5 entrance, Office 7 entrance, Office 1 entrance, Freight Elevators area
     */
    private void initializeDefaultLocations() {
        // Default image size assumption: 1200x800 (will be adjusted based on actual image)
        // These are percentage-based coordinates (0.0 to 1.0) that will be scaled to actual image size
        
        // RES001 - Main Entrance Door (assuming it's at the main entrance)
        // Position: approximately center-left of the image
        resourceLocations.put("RES001", new Point(150, 400));
        
        // RES002 - Office Door 201 (Office 7 area based on description)
        // Position: top-center area
        resourceLocations.put("RES002", new Point(600, 200));
        
        // RES003 - Elevator 1 (Passenger Elevators - red dot at bottom-center)
        // Position: bottom-center
        resourceLocations.put("RES003", new Point(600, 700));
        
        // Additional resources can be added here
        // For example, if you have more resources:
        // resourceLocations.put("RES004", new Point(x, y));
        // resourceLocations.put("RES005", new Point(x, y));
    }
    
    /**
     * Gets the location (coordinates) of a resource on the floor plan
     * @param resourceId the resource ID
     * @param imageWidth the actual width of the image
     * @param imageHeight the actual height of the image
     * @return Point representing the location, or null if resource not found
     */
    public Point getLocation(String resourceId, int imageWidth, int imageHeight) {
        Point defaultLocation = resourceLocations.get(resourceId);
        if (defaultLocation == null) {
            return null;
        }
        
        // Scale coordinates based on actual image size
        // Assuming default coordinates are for 1200x800 image
        int defaultWidth = 1200;
        int defaultHeight = 800;
        
        int scaledX = (int) (defaultLocation.x * ((double) imageWidth / defaultWidth));
        int scaledY = (int) (defaultLocation.y * ((double) imageHeight / defaultHeight));
        
        return new Point(scaledX, scaledY);
    }
    
    /**
     * Sets the location of a resource (for configuration)
     * @param resourceId the resource ID
     * @param x the x coordinate (for 1200x800 reference image)
     * @param y the y coordinate (for 1200x800 reference image)
     */
    public void setLocation(String resourceId, int x, int y) {
        resourceLocations.put(resourceId, new Point(x, y));
    }
    
    /**
     * Gets all resource IDs that have locations mapped
     * @return set of resource IDs
     */
    public java.util.Set<String> getMappedResourceIds() {
        return resourceLocations.keySet();
    }
}

