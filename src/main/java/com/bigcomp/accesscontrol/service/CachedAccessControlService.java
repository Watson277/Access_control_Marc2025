package com.bigcomp.accesscontrol.service;

import com.bigcomp.accesscontrol.model.*;
import com.bigcomp.accesscontrol.util.AccessControlCache;
import com.bigcomp.accesscontrol.util.LogManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cached implementation of AccessControlService.
 * Uses in-memory cache instead of database queries for fast access control processing.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class CachedAccessControlService implements AccessControlService {
    /** Cache instance for accessing data */
    private AccessControlCache cache;
    
    /** Log manager for recording access requests */
    private LogManager logManager;

    /**
     * Constructor
     * @param cache the AccessControlCache instance to use
     */
    public CachedAccessControlService(AccessControlCache cache) {
        this.cache = cache;
        this.logManager = LogManager.getInstance();
    }

    @Override
    public AccessRequest processAccessRequest(String badgeId, String readerId, String resourceId) {
        AccessRequest request = new AccessRequest(badgeId, readerId, resourceId);
        request.setRequestTime(LocalDateTime.now());

        // Step 1: Check if reader is active (not locked)
        if (!cache.isReaderActive(resourceId)) {
            request.setStatus(AccessRequest.AccessStatus.DENIED);
            request.setReason("Reader is locked (door opened, please wait)");
            logAccessRequest(request);
            return request;
        }

        // Step 2: Validate badge
        if (!validateBadge(badgeId)) {
            request.setStatus(AccessRequest.AccessStatus.DENIED);
            request.setReason("Invalid badge");
            logAccessRequest(request);
            return request;
        }

        // Step 3: Get badge and resource information from cache
        Badge badge = cache.getBadge(badgeId);
        Resource resource = cache.getResource(resourceId);

        if (badge == null) {
            request.setStatus(AccessRequest.AccessStatus.DENIED);
            request.setReason("Badge does not exist");
            logAccessRequest(request);
            return request;
        }

        if (resource == null) {
            request.setStatus(AccessRequest.AccessStatus.DENIED);
            request.setReason("Resource does not exist");
            logAccessRequest(request);
            return request;
        }

        // Step 4: Get user information from cache
        User user = cache.getUser(badge.getUserId());
        if (user != null) {
            request.setUserId(user.getUserId());
            request.setUserName(user.getFirstName() + ":" + user.getLastName());
        }

        // Step 5: Check access permission
        if (checkAccessPermission(badgeId, resourceId)) {
            request.setStatus(AccessRequest.AccessStatus.GRANTED);
            request.setReason("Access granted");
            
            // Lock the reader for 2 seconds after successful access
            cache.lockReader(resourceId);
        } else {
            request.setStatus(AccessRequest.AccessStatus.DENIED);
            request.setReason("Insufficient access rights");
        }

        // Step 6: Log the access request
        logAccessRequest(request);

        return request;
    }

    @Override
    public boolean validateBadge(String badgeId) {
        Badge badge = cache.getBadge(badgeId);
        if (badge == null) {
            return false;
        }

        // Check if badge is active
        if (!badge.isActive()) {
            return false;
        }

        // Check if badge has expired
        if (badge.isExpired()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean checkAccessPermission(String badgeId, String resourceId) {
        Badge badge = cache.getBadge(badgeId);
        if (badge == null) {
            return false;
        }

        Resource resource = cache.getResource(resourceId);
        if (resource == null) {
            return false;
        }

        // If resource is uncontrolled, allow access
        if (resource.getState() == Resource.ResourceState.UNCONTROLLED) {
            return true;
        }

        // Get profiles associated with the badge
        List<String> profileNames = badge.getProfileNames();
        if (profileNames.isEmpty()) {
            return false;
        }

        // Check each profile for access permission
        for (String profileName : profileNames) {
            Profile profile = cache.getProfile(profileName);
            if (profile == null) {
                continue;
            }

            // Check resource groups associated with the profile
            List<String> resourceGroupNames = profile.getResourceGroupNames();
            for (String groupName : resourceGroupNames) {
                ResourceGroup group = cache.getResourceGroup(groupName);
                if (group != null && group.getResourceIds().contains(resourceId)) {
                    // Check access rules
                    AccessRule rule = cache.getAccessRule(profileName, groupName);
                    if (rule != null && checkAccessRule(rule, badgeId)) {
                        return true;
                    } else if (rule == null) {
                        // If no specific rule, default to allow
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks access rules (time, date, access frequency, etc.)
     * @param rule the AccessRule to check
     * @param badgeId the ID of the badge (for future access count checking)
     * @return true if the rule allows access, false otherwise
     */
    private boolean checkAccessRule(AccessRule rule, String badgeId) {
        LocalDateTime now = LocalDateTime.now();
        int dayOfWeek = now.getDayOfWeek().getValue() % 7; // Convert to 0-6 format (0=Sunday)

        // Check allowed days of week
        if (!rule.getAllowedDaysOfWeek().isEmpty() && 
            !rule.getAllowedDaysOfWeek().contains(dayOfWeek)) {
            return false;
        }

        // Check time range
        if (rule.getStartTime() != null && rule.getEndTime() != null) {
            LocalDateTime start = now.toLocalDate().atTime(rule.getStartTime());
            LocalDateTime end = now.toLocalDate().atTime(rule.getEndTime());
            if (now.isBefore(start) || now.isAfter(end)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Badge getBadge(String badgeId) {
        return cache.getBadge(badgeId);
    }

    @Override
    public Resource getResource(String resourceId) {
        return cache.getResource(resourceId);
    }

    /**
     * Logs an access request using the log manager
     * @param request the AccessRequest to log
     */
    private void logAccessRequest(AccessRequest request) {
        logManager.logAccessRequest(request);
    }
}

