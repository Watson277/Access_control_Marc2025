package com.bigcomp.accesscontrol.service;

import com.bigcomp.accesscontrol.model.AccessRequest;
import com.bigcomp.accesscontrol.model.Badge;
import com.bigcomp.accesscontrol.model.Resource;
import com.bigcomp.accesscontrol.util.LogManager;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Access control service interface.
 * Provides business logic for processing access requests and validating permissions.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public interface AccessControlService {
    /**
     * Processes an access request from a badge reader
     * @param badgeId the ID of the badge used for access
     * @param readerId the ID of the badge reader
     * @param resourceId the ID of the resource being accessed
     * @return AccessRequest object containing the result of the access attempt
     */
    AccessRequest processAccessRequest(String badgeId, String readerId, String resourceId);

    /**
     * Validates whether a badge is active and not expired
     * @param badgeId the ID of the badge to validate
     * @return true if the badge is valid, false otherwise
     */
    boolean validateBadge(String badgeId);

    /**
     * Checks if a badge has permission to access a specific resource
     * @param badgeId the ID of the badge
     * @param resourceId the ID of the resource
     * @return true if access is permitted, false otherwise
     */
    boolean checkAccessPermission(String badgeId, String resourceId);

    /**
     * Retrieves badge information by ID
     * @param badgeId the ID of the badge
     * @return Badge object if found, null otherwise
     */
    Badge getBadge(String badgeId);

    /**
     * Retrieves resource information by ID
     * @param resourceId the ID of the resource
     * @return Resource object if found, null otherwise
     */
    Resource getResource(String resourceId);
}

