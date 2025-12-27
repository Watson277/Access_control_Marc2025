package com.bigcomp.accesscontrol.model;

import java.time.LocalDateTime;

/**
 * Access request entity class representing an attempt to access a resource.
 * This class stores information about each access attempt, including the result.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class AccessRequest {
    /** ID of the badge used in the access request */
    private String badgeId;
    
    /** ID of the badge reader that read the badge */
    private String readerId;
    
    /** ID of the resource being accessed */
    private String resourceId;
    
    /** Date and time when the access request was made */
    private LocalDateTime requestTime;
    
    /** Status of the access request (granted or denied) */
    private AccessStatus status;
    
    /** Reason for the access decision */
    private String reason;
    
    /** ID of the user who made the request */
    private String userId;
    
    /** Name of the user who made the request */
    private String userName;

    /**
     * Access status enumeration
     */
    public enum AccessStatus {
        GRANTED,  // Access was granted
        DENIED    // Access was denied
    }

    /**
     * Default constructor
     * Sets request time to current time
     */
    public AccessRequest() {
        this.requestTime = LocalDateTime.now();
    }

    /**
     * Constructor with badge, reader, and resource IDs
     * @param badgeId ID of the badge used in the access request
     * @param readerId ID of the badge reader that read the badge
     * @param resourceId ID of the resource being accessed
     */
    public AccessRequest(String badgeId, String readerId, String resourceId) {
        this.badgeId = badgeId;
        this.readerId = readerId;
        this.resourceId = resourceId;
        this.requestTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

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

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public AccessStatus getStatus() {
        return status;
    }

    public void setStatus(AccessStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "AccessRequest{" +
                "badgeId='" + badgeId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", requestTime=" + requestTime +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }
}

