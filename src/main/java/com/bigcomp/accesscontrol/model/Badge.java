package com.bigcomp.accesscontrol.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Badge entity class representing an access badge.
 * A badge is a physical or virtual identification device used for access control.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class Badge {
    /** Unique identifier for the badge */
    private String badgeId;
    
    /** ID of the user who owns this badge */
    private String userId;
    
    /** Date when the badge was created */
    private LocalDate createdDate;
    
    /** Date when the badge expires */
    private LocalDate expirationDate;
    
    /** Date of the last update to the badge */
    private LocalDate lastUpdateDate;
    
    /** Whether the badge is currently active */
    private boolean isActive;
    
    /** List of profile names associated with this badge */
    private List<String> profileNames;

    /**
     * Default constructor
     */
    public Badge() {
        this.profileNames = new ArrayList<>();
    }

    /**
     * Constructor with all fields
     * @param badgeId unique identifier for the badge
     * @param userId ID of the user who owns this badge
     * @param createdDate date when the badge was created
     * @param expirationDate date when the badge expires
     * @param lastUpdateDate date of the last update
     * @param isActive whether the badge is currently active
     */
    public Badge(String badgeId, String userId, LocalDate createdDate, 
                 LocalDate expirationDate, LocalDate lastUpdateDate, boolean isActive) {
        this.badgeId = badgeId;
        this.userId = userId;
        this.createdDate = createdDate;
        this.expirationDate = expirationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.isActive = isActive;
        this.profileNames = new ArrayList<>();
    }

    // Getters and Setters
    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDate lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getProfileNames() {
        return profileNames;
    }

    public void setProfileNames(List<String> profileNames) {
        this.profileNames = profileNames;
    }

    /**
     * Adds a profile to this badge
     * @param profileName name of the profile to add
     */
    public void addProfile(String profileName) {
        if (!profileNames.contains(profileName)) {
            profileNames.add(profileName);
        }
    }

    /**
     * Removes a profile from this badge
     * @param profileName name of the profile to remove
     */
    public void removeProfile(String profileName) {
        profileNames.remove(profileName);
    }

    /**
     * Checks if the badge has expired
     * @return true if the badge expiration date is before today, false otherwise
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Badge{" +
                "badgeId='" + badgeId + '\'' +
                ", userId='" + userId + '\'' +
                ", createdDate=" + createdDate +
                ", expirationDate=" + expirationDate +
                ", isActive=" + isActive +
                '}';
    }
}

