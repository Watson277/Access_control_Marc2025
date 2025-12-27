package com.bigcomp.accesscontrol.service;

import com.bigcomp.accesscontrol.dao.*;
import com.bigcomp.accesscontrol.model.*;
import com.bigcomp.accesscontrol.util.LogManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of AccessControlService interface.
 * Handles access request processing, badge validation, and permission checking.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class AccessControlServiceImpl implements AccessControlService {
    /** Data access object for badges */
    private BadgeDAO badgeDAO;
    
    /** Data access object for resources */
    private ResourceDAO resourceDAO;
    
    /** Data access object for users */
    private UserDAO userDAO;
    
    /** Data access object for profiles */
    private ProfileDAO profileDAO;
    
    /** Data access object for access rules */
    private AccessRuleDAO accessRuleDAO;
    
    /** Log manager for recording access requests */
    private LogManager logManager;

    /**
     * Default constructor
     * Initializes all DAO objects and the log manager
     */
    public AccessControlServiceImpl() {
        this.badgeDAO = new BadgeDAOImpl();
        this.resourceDAO = new ResourceDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.profileDAO = new ProfileDAOImpl();
        this.accessRuleDAO = new AccessRuleDAOImpl();
        this.logManager = LogManager.getInstance();
    }

    @Override
    public AccessRequest processAccessRequest(String badgeId, String readerId, String resourceId) {
        AccessRequest request = new AccessRequest(badgeId, readerId, resourceId);
        request.setRequestTime(LocalDateTime.now());

        // Step 1: Validate badge
        if (!validateBadge(badgeId)) {
            request.setStatus(AccessRequest.AccessStatus.DENIED);
            request.setReason("Invalid badge");
            logAccessRequest(request);
            return request;
        }

        // Step 2: Get badge and resource information
        Badge badge = getBadge(badgeId);
        Resource resource = getResource(resourceId);

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

        // Step 3: Get user information
        User user = userDAO.getUserById(badge.getUserId());
        if (user != null) {
            request.setUserId(user.getUserId());
            request.setUserName(user.getFirstName() + ":" + user.getLastName());
        }

        // Step 4: Check access permission
        if (checkAccessPermission(badgeId, resourceId)) {
            request.setStatus(AccessRequest.AccessStatus.GRANTED);
            request.setReason("Access granted");
        } else {
            request.setStatus(AccessRequest.AccessStatus.DENIED);
            request.setReason("Insufficient access rights");
        }

        // Step 5: Log the access request
        logAccessRequest(request);

        return request;
    }

    @Override
    public boolean validateBadge(String badgeId) {
        Badge badge = badgeDAO.getBadgeById(badgeId);
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
        Badge badge = badgeDAO.getBadgeById(badgeId);
        if (badge == null) {
            return false;
        }

        Resource resource = resourceDAO.getResourceById(resourceId);
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
            Profile profile = profileDAO.getProfileByName(profileName);
            if (profile == null) {
                continue;
            }

            // Check resource groups associated with the profile
            List<String> resourceGroupNames = profile.getResourceGroupNames();
            for (String groupName : resourceGroupNames) {
                ResourceGroup group = resourceDAO.getResourceGroupByName(groupName);
                if (group != null && group.getResourceIds().contains(resourceId)) {
                    // Check access rules
                    AccessRule rule = accessRuleDAO.getRuleByProfileAndGroup(profileName, groupName);
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

        // TODO: Check access frequency limits (requires access count statistics)
        // TODO: Check precedence requirements (requires access history records)

        return true;
    }

    @Override
    public Badge getBadge(String badgeId) {
        return badgeDAO.getBadgeById(badgeId);
    }

    @Override
    public Resource getResource(String resourceId) {
        return resourceDAO.getResourceById(resourceId);
    }

    /**
     * Logs an access request using the log manager
     * @param request the AccessRequest to log
     */
    private void logAccessRequest(AccessRequest request) {
        logManager.logAccessRequest(request);
    }
}

