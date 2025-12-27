package com.bigcomp.accesscontrol.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Profile entity class representing an access control profile.
 * A profile defines which resource groups a badge can access.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class Profile {
    /** Name of the profile (primary key) */
    private String profileName;
    
    /** File path where the profile configuration is stored */
    private String filePath;
    
    /** Description of the profile */
    private String description;
    
    /** List of resource group names associated with this profile */
    private List<String> resourceGroupNames;

    /**
     * Default constructor
     */
    public Profile() {
        this.resourceGroupNames = new ArrayList<>();
    }

    /**
     * Constructor with main fields
     * @param profileName name of the profile
     * @param filePath file path where the profile configuration is stored
     * @param description description of the profile
     */
    public Profile(String profileName, String filePath, String description) {
        this.profileName = profileName;
        this.filePath = filePath;
        this.description = description;
        this.resourceGroupNames = new ArrayList<>();
    }

    // Getters and Setters
    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
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

    public List<String> getResourceGroupNames() {
        return resourceGroupNames;
    }

    public void setResourceGroupNames(List<String> resourceGroupNames) {
        this.resourceGroupNames = resourceGroupNames;
    }

    /**
     * Adds a resource group to this profile
     * @param groupName name of the resource group to add
     */
    public void addResourceGroup(String groupName) {
        if (!resourceGroupNames.contains(groupName)) {
            resourceGroupNames.add(groupName);
        }
    }

    /**
     * Removes a resource group from this profile
     * @param groupName name of the resource group to remove
     */
    public void removeResourceGroup(String groupName) {
        resourceGroupNames.remove(groupName);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "profileName='" + profileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

