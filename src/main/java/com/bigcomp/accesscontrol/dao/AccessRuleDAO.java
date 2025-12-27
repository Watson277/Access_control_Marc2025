package com.bigcomp.accesscontrol.dao;

import com.bigcomp.accesscontrol.model.AccessRule;

/**
 * Data Access Object interface for AccessRule entities.
 * Provides CRUD operations for access rule management.
 * Note: Rules are typically stored in configuration files, not the database.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public interface AccessRuleDAO {
    /**
     * Retrieves an access rule by profile name and resource group name
     * @param profileName the name of the profile
     * @param resourceGroupName the name of the resource group
     * @return AccessRule object if found, null otherwise
     */
    AccessRule getRuleByProfileAndGroup(String profileName, String resourceGroupName);
    
    /**
     * Inserts a new access rule
     * @param rule the AccessRule object to insert
     * @return true if insertion was successful, false otherwise
     */
    boolean insertRule(AccessRule rule);
    
    /**
     * Updates an existing access rule
     * @param rule the AccessRule object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean updateRule(AccessRule rule);
    
    /**
     * Deletes an access rule
     * @param ruleId the unique identifier of the rule to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteRule(String ruleId);
}

