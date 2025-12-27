package com.bigcomp.accesscontrol.model;

/**
 * Badge reader entity class representing a physical device that reads badge IDs.
 * Each badge reader is associated with a resource.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class BadgeReader {
    /** Unique identifier for the badge reader */
    private String readerId;
    
    /** ID of the resource this reader is associated with */
    private String resourceId;
    
    /** Name of the badge reader */
    private String readerName;
    
    /** Physical location of the badge reader */
    private String location;
    
    /** Whether the badge reader is currently active */
    private boolean isActive;

    /**
     * Default constructor
     */
    public BadgeReader() {
    }

    /**
     * Constructor with all fields
     * @param readerId unique identifier for the badge reader
     * @param resourceId ID of the resource this reader is associated with
     * @param readerName name of the badge reader
     * @param location physical location of the badge reader
     * @param isActive whether the badge reader is currently active
     */
    public BadgeReader(String readerId, String resourceId, String readerName, 
                      String location, boolean isActive) {
        this.readerId = readerId;
        this.resourceId = resourceId;
        this.readerName = readerName;
        this.location = location;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "BadgeReader{" +
                "readerId='" + readerId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", readerName='" + readerName + '\'' +
                ", location='" + location + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

