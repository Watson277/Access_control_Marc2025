package com.bigcomp.accesscontrol.model;

/**
 * Resource entity class representing a physical or logical resource in the access control system.
 * Resources can be doors, gates, elevators, printers, etc.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class Resource {
    /** Unique identifier for the resource */
    private String resourceId;
    
    /** ID of the badge reader associated with this resource */
    private String badgeReaderId;
    
    /** Name of the resource */
    private String resourceName;
    
    /** Physical location of the resource */
    private String location;
    
    /** Type of the resource */
    private ResourceType resourceType;
    
    /** State of the resource (controlled or uncontrolled) */
    private ResourceState state;
    
    /** Building where the resource is located */
    private String building;
    
    /** Floor number where the resource is located */
    private Integer floor;

    /**
     * Resource type enumeration
     */
    public enum ResourceType {
        DOOR,                  // Door
        GATE,                  // Gate
        ELEVATOR,              // Elevator
        STAIRWAY,              // Stairway
        PRINTER,               // Printer
        BEVERAGE_DISPENSER,    // Beverage dispenser
        OTHER                  // Other types
    }

    /**
     * Resource state enumeration
     */
    public enum ResourceState {
        CONTROLLED,    // Access is controlled by the system
        UNCONTROLLED   // Access is not controlled (free access)
    }

    /**
     * Default constructor
     */
    public Resource() {
    }

    /**
     * Constructor with main fields
     * @param resourceId unique identifier for the resource
     * @param badgeReaderId ID of the badge reader associated with this resource
     * @param resourceName name of the resource
     * @param location physical location of the resource
     * @param resourceType type of the resource
     * @param state state of the resource (controlled or uncontrolled)
     */
    public Resource(String resourceId, String badgeReaderId, String resourceName, 
                   String location, ResourceType resourceType, ResourceState state) {
        this.resourceId = resourceId;
        this.badgeReaderId = badgeReaderId;
        this.resourceName = resourceName;
        this.location = location;
        this.resourceType = resourceType;
        this.state = state;
    }

    // Getters and Setters
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getBadgeReaderId() {
        return badgeReaderId;
    }

    public void setBadgeReaderId(String badgeReaderId) {
        this.badgeReaderId = badgeReaderId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceState getState() {
        return state;
    }

    public void setState(ResourceState state) {
        this.state = state;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceId='" + resourceId + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", location='" + location + '\'' +
                ", resourceType=" + resourceType +
                ", state=" + state +
                '}';
    }
}

