package com.bigcomp.accesscontrol.util;

import com.bigcomp.accesscontrol.dao.*;
import com.bigcomp.accesscontrol.model.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory cache for access control data.
 * Loads all necessary data from database at startup to enable fast access without database queries.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class AccessControlCache {
    /** Singleton instance */
    private static AccessControlCache instance;
    
    /** Cache for badges by badge ID */
    private Map<String, Badge> badgeCache;
    
    /** Cache for resources by resource ID */
    private Map<String, Resource> resourceCache;
    
    /** Cache for users by user ID */
    private Map<String, User> userCache;
    
    /** Cache for profiles by profile name */
    private Map<String, Profile> profileCache;
    
    /** Cache for resource groups by group name */
    private Map<String, ResourceGroup> resourceGroupCache;
    
    /** Cache for access rules by profile name and group name */
    private Map<String, AccessRule> accessRuleCache; // Key: "profileName:groupName"
    
    /** Cache for badge-reader status (active/inactive) by resource ID */
    private Map<String, Boolean> readerStatusCache; // true = active, false = inactive (locked)
    
    /** DAO objects for loading data */
    private BadgeDAO badgeDAO;
    private ResourceDAO resourceDAO;
    private UserDAO userDAO;
    private ProfileDAO profileDAO;
    private AccessRuleDAO accessRuleDAO;
    
    /** Whether the cache has been initialized */
    private boolean initialized;

    /**
     * Private constructor for singleton pattern
     */
    private AccessControlCache() {
        this.badgeCache = new ConcurrentHashMap<>();
        this.resourceCache = new ConcurrentHashMap<>();
        this.userCache = new ConcurrentHashMap<>();
        this.profileCache = new ConcurrentHashMap<>();
        this.resourceGroupCache = new ConcurrentHashMap<>();
        this.accessRuleCache = new ConcurrentHashMap<>();
        this.readerStatusCache = new ConcurrentHashMap<>();
        this.badgeDAO = new BadgeDAOImpl();
        this.resourceDAO = new ResourceDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.profileDAO = new ProfileDAOImpl();
        this.accessRuleDAO = new AccessRuleDAOImpl();
        this.initialized = false;
    }

    /**
     * Gets the singleton instance
     * @return the singleton instance
     */
    public static synchronized AccessControlCache getInstance() {
        if (instance == null) {
            instance = new AccessControlCache();
        }
        return instance;
    }

    /**
     * Initializes the cache by loading all data from the database
     * @return true if initialization was successful, false otherwise
     */
    public boolean initialize() {
        if (initialized) {
            return true;
        }
        
        try {
            // Load all badges
            List<Badge> badges = badgeDAO.getAllBadges();
            for (Badge badge : badges) {
                badgeCache.put(badge.getBadgeId(), badge);
            }
            
            // Load all resources
            List<Resource> resources = resourceDAO.getAllResources();
            for (Resource resource : resources) {
                resourceCache.put(resource.getResourceId(), resource);
                // Initialize all readers as active
                readerStatusCache.put(resource.getResourceId(), true);
            }
            
            // Load all users
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                userCache.put(user.getUserId(), user);
            }
            
            // Load all profiles
            List<Profile> profiles = profileDAO.getAllProfiles();
            for (Profile profile : profiles) {
                profileCache.put(profile.getProfileName(), profile);
            }
            
            // Load all resource groups
            List<ResourceGroup> resourceGroups = resourceDAO.getAllResourceGroups();
            for (ResourceGroup group : resourceGroups) {
                resourceGroupCache.put(group.getGroupName(), group);
            }
            
            // Load all access rules
            // Note: Access rules are typically stored in configuration files, not the database.
            // If rules are stored in the database, they will be loaded here.
            List<AccessRule> rules = accessRuleDAO.getAllRules();
            for (AccessRule rule : rules) {
                String key = rule.getProfileName() + ":" + rule.getResourceGroupName();
                accessRuleCache.put(key, rule);
            }
            
            initialized = true;
            System.out.println("AccessControlCache initialized successfully. " +
                    "Loaded " + badges.size() + " badges, " + resources.size() + " resources, " +
                    users.size() + " users, " + profiles.size() + " profiles, " +
                    resourceGroups.size() + " resource groups, " + rules.size() + " access rules.");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to initialize AccessControlCache: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets a badge from cache
     * @param badgeId the badge ID
     * @return Badge object if found, null otherwise
     */
    public Badge getBadge(String badgeId) {
        return badgeCache.get(badgeId);
    }

    /**
     * Gets a resource from cache
     * @param resourceId the resource ID
     * @return Resource object if found, null otherwise
     */
    public Resource getResource(String resourceId) {
        return resourceCache.get(resourceId);
    }

    /**
     * Gets a user from cache
     * @param userId the user ID
     * @return User object if found, null otherwise
     */
    public User getUser(String userId) {
        return userCache.get(userId);
    }

    /**
     * Gets a profile from cache
     * @param profileName the profile name
     * @return Profile object if found, null otherwise
     */
    public Profile getProfile(String profileName) {
        return profileCache.get(profileName);
    }

    /**
     * Gets a resource group from cache
     * @param groupName the group name
     * @return ResourceGroup object if found, null otherwise
     */
    public ResourceGroup getResourceGroup(String groupName) {
        return resourceGroupCache.get(groupName);
    }

    /**
     * Gets an access rule from cache
     * @param profileName the profile name
     * @param groupName the group name
     * @return AccessRule object if found, null otherwise
     */
    public AccessRule getAccessRule(String profileName, String groupName) {
        String key = profileName + ":" + groupName;
        return accessRuleCache.get(key);
    }

    /**
     * Checks if a badge reader (resource) is currently active
     * @param resourceId the resource ID
     * @return true if active, false if locked/inactive
     */
    public boolean isReaderActive(String resourceId) {
        Boolean status = readerStatusCache.get(resourceId);
        return status != null && status;
    }

    /**
     * Sets a badge reader (resource) to inactive (locked) state
     * @param resourceId the resource ID
     */
    public void lockReader(String resourceId) {
        readerStatusCache.put(resourceId, false);
    }

    /**
     * Sets a badge reader (resource) to active (unlocked) state
     * @param resourceId the resource ID
     */
    public void unlockReader(String resourceId) {
        readerStatusCache.put(resourceId, true);
    }

    /**
     * Gets all active badges (for random selection in simulation)
     * @return list of active badge IDs
     */
    public List<String> getActiveBadgeIds() {
        List<String> activeBadges = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Badge badge : badgeCache.values()) {
            if (badge.isActive() && !badge.isExpired()) {
                activeBadges.add(badge.getBadgeId());
            }
        }
        return activeBadges;
    }

    /**
     * Gets all resource IDs (for random selection in simulation)
     * @return list of resource IDs
     */
    public List<String> getAllResourceIds() {
        return new ArrayList<>(resourceCache.keySet());
    }

    /**
     * Checks if the cache has been initialized
     * @return true if initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Clears the cache (for testing or re-initialization)
     */
    public void clear() {
        badgeCache.clear();
        resourceCache.clear();
        userCache.clear();
        profileCache.clear();
        resourceGroupCache.clear();
        accessRuleCache.clear();
        readerStatusCache.clear();
        initialized = false;
    }
}

