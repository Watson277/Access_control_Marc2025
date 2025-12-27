package com.bigcomp.accesscontrol.dao;

import com.bigcomp.accesscontrol.model.Resource;
import com.bigcomp.accesscontrol.model.ResourceGroup;
import java.util.List;

/**
 * Data Access Object interface for Resource and ResourceGroup entities.
 * Provides CRUD operations for resource and resource group management.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public interface ResourceDAO {
    /**
     * Retrieves a resource by its ID
     * @param resourceId the unique identifier of the resource
     * @return Resource object if found, null otherwise
     */
    Resource getResourceById(String resourceId);
    
    /**
     * Retrieves all resources from the database
     * @return list of all Resource objects
     */
    List<Resource> getAllResources();
    
    /**
     * Inserts a new resource into the database
     * @param resource the Resource object to insert
     * @return true if insertion was successful, false otherwise
     */
    boolean insertResource(Resource resource);
    
    /**
     * Updates an existing resource in the database
     * @param resource the Resource object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean updateResource(Resource resource);
    
    /**
     * Deletes a resource from the database
     * @param resourceId the unique identifier of the resource to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteResource(String resourceId);
    
    /**
     * Retrieves a resource group by its name
     * @param groupName the name of the resource group
     * @return ResourceGroup object if found, null otherwise
     */
    ResourceGroup getResourceGroupByName(String groupName);
    
    /**
     * Retrieves all resource groups from the database
     * @return list of all ResourceGroup objects
     */
    List<ResourceGroup> getAllResourceGroups();
}

