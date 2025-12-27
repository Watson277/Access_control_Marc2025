package com.bigcomp.accesscontrol.service;

import com.bigcomp.accesscontrol.model.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.bigcomp.accesscontrol.util.DatabaseConnection;

/**
 * Permission management service.
 * Handles adding and removing resource access permissions for badges.
 * Manages the permission chain: Badge -> Profile -> ResourceGroup -> Resource
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class PermissionService {
    /** Database connection manager */
    private DatabaseConnection dbConnection;

    /**
     * Default constructor
     */
    public PermissionService() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Gets all resources that a badge currently has access to
     * @param badgeId the ID of the badge
     * @return list of Resource objects accessible by the badge
     */
    public List<Resource> getBadgeAccessibleResources(String badgeId) {
        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT DISTINCT r.* " +
                     "FROM resources r " +
                     "JOIN resource_group_members rgm ON r.resource_id = rgm.resource_id " +
                     "JOIN resource_groups rg ON rgm.group_name = rg.group_name " +
                     "JOIN profile_resource_groups prg ON rg.group_name = prg.group_name " +
                     "JOIN profiles p ON prg.profile_name = p.profile_name " +
                     "JOIN badge_profiles bp ON p.profile_name = bp.profile_name " +
                     "WHERE bp.badge_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badgeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Resource resource = mapResultSetToResource(rs);
                resources.add(resource);
            }
        } catch (SQLException e) {
            System.err.println("Failed to query badge accessible resources: " + e.getMessage());
        }
        return resources;
    }

    /**
     * Adds resource access permission to a badge.
     * If the resource is not in any resource group, automatically creates a resource group and establishes associations.
     * Creates the permission chain: Badge -> Profile -> ResourceGroup -> Resource
     * @param badgeId the ID of the badge
     * @param resourceId the ID of the resource
     * @return true if permission was added successfully, false otherwise
     */
    public boolean addResourceAccessToBadge(String badgeId, String resourceId) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Step 1: Check if resource exists
                Resource resource = getResourceById(resourceId, conn);
                if (resource == null) {
                    throw new SQLException("Resource does not exist: " + resourceId);
                }

                // Step 2: Check if badge exists and is active
                if (!isBadgeValid(badgeId, conn)) {
                    throw new SQLException("Badge is invalid or deactivated");
                }

                // Step 3: Check if resource is already accessible by the badge
                if (isResourceAccessible(badgeId, resourceId, conn)) {
                    return true; // Already has permission
                }

                // Step 4: Find or create resource group
                String groupName = findOrCreateResourceGroup(resourceId, conn);

                // Step 5: Find or create profile for badge
                String profileName = findOrCreateProfileForBadge(badgeId, conn);

                // Step 6: Establish association chain
                // 6.1 ResourceGroup -> Resource
                addResourceToGroup(resourceId, groupName, conn);

                // 6.2 Profile -> ResourceGroup
                addGroupToProfile(profileName, groupName, conn);

                // 6.3 Badge -> Profile
                addProfileToBadge(badgeId, profileName, conn);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Failed to add permission: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Failed to add permission: " + e.getMessage());
            return false;
        }
    }

    private Resource getResourceById(String resourceId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM resources WHERE resource_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resourceId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToResource(rs);
            }
        }
        return null;
    }

    private boolean isBadgeValid(String badgeId, Connection conn) throws SQLException {
        String sql = "SELECT is_active, expiration_date FROM badges WHERE badge_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badgeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean isActive = rs.getBoolean("is_active");
                Date expDate = rs.getDate("expiration_date");
                if (!isActive) return false;
                if (expDate != null && expDate.before(new java.sql.Date(System.currentTimeMillis()))) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private boolean isResourceAccessible(String badgeId, String resourceId, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as cnt " +
                     "FROM badge_profiles bp " +
                     "JOIN profile_resource_groups prg ON bp.profile_name = prg.profile_name " +
                     "JOIN resource_group_members rgm ON prg.group_name = rgm.group_name " +
                     "WHERE bp.badge_id = ? AND rgm.resource_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badgeId);
            pstmt.setString(2, resourceId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt") > 0;
            }
        }
        return false;
    }

    private String findOrCreateResourceGroup(String resourceId, Connection conn) throws SQLException {
        // Create a dedicated resource group for a single resource to avoid affecting other resources
        // This ensures each resource has its own independent permission control
        String groupName = "auto_group_" + resourceId;
        String insertSql = "INSERT INTO resource_groups (group_name, security_level, file_path, description) " +
                     "VALUES (?, 1, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE description = VALUES(description)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, groupName);
            pstmt.setString(2, "files/resource_groups/" + groupName + ".json");
            pstmt.setString(3, "Auto-created resource group, contains only resource: " + resourceId);
            pstmt.executeUpdate();
        }
        return groupName;
    }

    private String findOrCreateProfileForBadge(String badgeId, Connection conn) throws SQLException {
        // First check if badge already has a profile
        String sql = "SELECT profile_name FROM badge_profiles WHERE badge_id = ? LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badgeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_name");
            }
        }

        // If not, create a new profile
        String profileName = "auto_profile_" + badgeId;
        String insertSql = "INSERT INTO profiles (profile_name, file_path, description) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE description = VALUES(description)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, profileName);
            pstmt.setString(2, "files/profiles/" + profileName + ".json");
            pstmt.setString(3, "Auto-created profile for badge: " + badgeId);
            pstmt.executeUpdate();
        }
        return profileName;
    }

    private void addResourceToGroup(String resourceId, String groupName, Connection conn) throws SQLException {
        String sql = "INSERT INTO resource_group_members (resource_id, group_name) " +
                     "VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE resource_id = VALUES(resource_id)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resourceId);
            pstmt.setString(2, groupName);
            pstmt.executeUpdate();
        }
    }

    private void addGroupToProfile(String profileName, String groupName, Connection conn) throws SQLException {
        String sql = "INSERT INTO profile_resource_groups (profile_name, group_name) " +
                     "VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE profile_name = VALUES(profile_name)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileName);
            pstmt.setString(2, groupName);
            pstmt.executeUpdate();
        }
    }

    private void addProfileToBadge(String badgeId, String profileName, Connection conn) throws SQLException {
        String sql = "INSERT INTO badge_profiles (badge_id, profile_name) " +
                     "VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE profile_name = VALUES(profile_name)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badgeId);
            pstmt.setString(2, profileName);
            pstmt.executeUpdate();
        }
    }

    /**
     * Removes resource access permission from a badge.
     * Intelligently cleans up unused resource groups and profiles.
     * @param badgeId the ID of the badge
     * @param resourceId the ID of the resource
     * @return true if permission was removed successfully, false otherwise
     */
    public boolean removeResourceAccessFromBadge(String badgeId, String resourceId) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Step 1: Find associations between resource and badge through resource groups and profiles
                String sql = "SELECT DISTINCT prg.profile_name, rgm.group_name " +
                             "FROM badge_profiles bp " +
                             "JOIN profile_resource_groups prg ON bp.profile_name = prg.profile_name " +
                             "JOIN resource_group_members rgm ON prg.group_name = rgm.group_name " +
                             "WHERE bp.badge_id = ? AND rgm.resource_id = ?";
                
                List<String[]> associations = new ArrayList<>();
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, badgeId);
                    pstmt.setString(2, resourceId);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        associations.add(new String[]{
                            rs.getString("profile_name"),
                            rs.getString("group_name")
                        });
                    }
                }

                if (associations.isEmpty()) {
                    // Resource is not in badge's permissions
                    return false;
                }

                // Step 2: Delete all related profile-resource group associations
                // For auto-created resource groups (auto_group_resourceId), delete the entire association
                for (String[] association : associations) {
                    String profileName = association[0];
                    String groupName = association[1];
                    
                    // If it's an auto-created resource group (contains only this resource), delete entire association
                    if (groupName.startsWith("auto_group_") && groupName.equals("auto_group_" + resourceId)) {
                        // Delete profile-resource group association
                        removeGroupFromProfile(profileName, groupName, conn);
                        
                        // Delete resource-resource group association
                        removeResourceFromGroup(resourceId, groupName, conn);
                        
                        // Check if resource group has other resources, if not, delete the group
                        if (!hasOtherResources(groupName, conn)) {
                            deleteResourceGroup(groupName, conn);
                        }
                        
                        // Check if profile has other resource groups, if not, delete profile-badge association
                        if (!hasOtherGroups(profileName, conn)) {
                            removeProfileFromBadge(badgeId, profileName, conn);
                            
                            // Check if profile is used by other badges, if not, delete the profile
                            if (!hasOtherBadges(profileName, conn)) {
                                deleteProfile(profileName, conn);
                            }
                        }
                    } else {
                        // If it's a shared resource group, only delete resource-resource group association
                        // Note: This may affect other badges using this resource group
                        // For simplicity, we only delete the resource-resource group association here
                        removeResourceFromGroup(resourceId, groupName, conn);
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Failed to remove permission: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Failed to remove permission: " + e.getMessage());
            return false;
        }
    }

    private void removeGroupFromProfile(String profileName, String groupName, Connection conn) throws SQLException {
        String sql = "DELETE FROM profile_resource_groups WHERE profile_name = ? AND group_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileName);
            pstmt.setString(2, groupName);
            pstmt.executeUpdate();
        }
    }

    private void removeResourceFromGroup(String resourceId, String groupName, Connection conn) throws SQLException {
        String sql = "DELETE FROM resource_group_members WHERE resource_id = ? AND group_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resourceId);
            pstmt.setString(2, groupName);
            pstmt.executeUpdate();
        }
    }

    private void removeProfileFromBadge(String badgeId, String profileName, Connection conn) throws SQLException {
        String sql = "DELETE FROM badge_profiles WHERE badge_id = ? AND profile_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badgeId);
            pstmt.setString(2, profileName);
            pstmt.executeUpdate();
        }
    }

    private boolean hasOtherResources(String groupName, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM resource_group_members WHERE group_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt") > 0;
            }
        }
        return false;
    }

    private boolean hasOtherGroups(String profileName, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM profile_resource_groups WHERE profile_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt") > 0;
            }
        }
        return false;
    }

    private boolean hasOtherBadges(String profileName, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM badge_profiles WHERE profile_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt") > 0;
            }
        }
        return false;
    }

    private void deleteResourceGroup(String groupName, Connection conn) throws SQLException {
        String sql = "DELETE FROM resource_groups WHERE group_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            pstmt.executeUpdate();
        }
    }

    private void deleteProfile(String profileName, Connection conn) throws SQLException {
        String sql = "DELETE FROM profiles WHERE profile_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileName);
            pstmt.executeUpdate();
        }
    }

    private Resource mapResultSetToResource(ResultSet rs) throws SQLException {
        Resource resource = new Resource();
        resource.setResourceId(rs.getString("resource_id"));
        resource.setBadgeReaderId(rs.getString("badge_reader_id"));
        resource.setResourceName(rs.getString("resource_name"));
        resource.setLocation(rs.getString("location"));
        resource.setResourceType(Resource.ResourceType.valueOf(rs.getString("resource_type")));
        resource.setState(Resource.ResourceState.valueOf(rs.getString("state")));
        resource.setBuilding(rs.getString("building"));
        Integer floor = rs.getInt("floor");
        if (!rs.wasNull()) {
            resource.setFloor(floor);
        }
        return resource;
    }
}

