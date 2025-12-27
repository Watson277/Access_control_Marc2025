package com.bigcomp.accesscontrol.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource group entity class representing a collection of resources.
 * Resources are grouped together for easier access control management.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class ResourceGroup {
    /** Name of the resource group (primary key) */
    private String groupName;
    
    /** Security level of the resource group (higher number = more secure) */
    private int securityLevel;
    
    /** File path where the resource group configuration is stored */
    private String filePath;
    
    /** Description of the resource group */
    private String description;
    
    /** List of resource IDs that belong to this group */
    private List<String> resourceIds;

    /**
     * Default constructor
     */
    public ResourceGroup() {
        this.resourceIds = new ArrayList<>();
    }

    /**
     * Constructor with all fields
     * @param groupName name of the resource group
     * @param securityLevel security level of the group
     * @param filePath file path where the group configuration is stored
     * @param description description of the resource group
     */
    public ResourceGroup(String groupName, int securityLevel, String filePath, String description) {
        this.groupName = groupName;
        this.securityLevel = securityLevel;
        this.filePath = filePath;
        this.description = description;
        this.resourceIds = new ArrayList<>();
    }

    // Getters and Setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    /**
     * Adds a resource to this group
     * @param resourceId ID of the resource to add
     */
    public void addResource(String resourceId) {
        if (!resourceIds.contains(resourceId)) {
            resourceIds.add(resourceId);
        }
    }

    /**
     * Removes a resource from this group
     * @param resourceId ID of the resource to remove
     */
    public void removeResource(String resourceId) {
        resourceIds.remove(resourceId);
    }

    @Override
    public String toString() {
        return "ResourceGroup{" +
                "groupName='" + groupName + '\'' +
                ", securityLevel=" + securityLevel +
                ", filePath='" + filePath + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

