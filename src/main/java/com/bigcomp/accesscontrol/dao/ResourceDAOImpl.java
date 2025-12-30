package com.bigcomp.accesscontrol.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.bigcomp.accesscontrol.model.Resource;
import com.bigcomp.accesscontrol.model.ResourceGroup;
import com.bigcomp.accesscontrol.util.DatabaseConnection;

/**
 * Implementation of ResourceDAO interface.
 * Provides database operations for Resource and ResourceGroup entities using JDBC.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class ResourceDAOImpl implements ResourceDAO {
    /** Database connection manager */
    private DatabaseConnection dbConnection;

    /**
     * Default constructor
     */
    public ResourceDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Resource getResourceById(String resourceId) {
        String sql = "SELECT * FROM resources WHERE resource_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resourceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToResource(rs);
            }
        } catch (SQLException e) {
            System.err.println("Failed to query resource: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Resource> getAllResources() {
        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT * FROM resources";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resources.add(mapResultSetToResource(rs));
            }
        } catch (SQLException e) {
            System.err.println("Failed to query all resources: " + e.getMessage());
        }
        return resources;
    }

    @Override
    public boolean insertResource(Resource resource) {
        String sql = "INSERT INTO resources (resource_id, badge_reader_id, resource_name, location, " +
                     "resource_type, state, building, floor) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resource.getResourceId());
            pstmt.setString(2, resource.getBadgeReaderId());
            pstmt.setString(3, resource.getResourceName());
            pstmt.setString(4, resource.getLocation());
            pstmt.setString(5, resource.getResourceType().name());
            pstmt.setString(6, resource.getState().name());
            pstmt.setString(7, resource.getBuilding());
            if (resource.getFloor() != null) {
                pstmt.setInt(8, resource.getFloor());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to insert resource: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateResource(Resource resource) {
        String sql = "UPDATE resources SET badge_reader_id = ?, resource_name = ?, location = ?, " +
                     "resource_type = ?, state = ?, building = ?, floor = ? WHERE resource_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resource.getBadgeReaderId());
            pstmt.setString(2, resource.getResourceName());
            pstmt.setString(3, resource.getLocation());
            pstmt.setString(4, resource.getResourceType().name());
            pstmt.setString(5, resource.getState().name());
            pstmt.setString(6, resource.getBuilding());
            if (resource.getFloor() != null) {
                pstmt.setInt(7, resource.getFloor());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            pstmt.setString(8, resource.getResourceId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update resource: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteResource(String resourceId) {
        String sql = "DELETE FROM resources WHERE resource_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resourceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete resource: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ResourceGroup getResourceGroupByName(String groupName) {
        String sql = "SELECT * FROM resource_groups WHERE group_name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                ResourceGroup group = mapResultSetToResourceGroup(rs);
                loadResourceGroupMembers(group);
                return group;
            }
        } catch (SQLException e) {
            System.err.println("Failed to query resource group: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ResourceGroup> getAllResourceGroups() {
        List<ResourceGroup> groups = new ArrayList<>();
        String sql = "SELECT * FROM resource_groups";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ResourceGroup group = mapResultSetToResourceGroup(rs);
                loadResourceGroupMembers(group);
                groups.add(group);
            }
        } catch (SQLException e) {
            System.err.println("Failed to query all resource groups: " + e.getMessage());
        }
        return groups;
    }

    /**
     * Maps a ResultSet row to a Resource object
     * @param rs the ResultSet containing resource data
     * @return Resource object mapped from the ResultSet
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Maps a ResultSet row to a ResourceGroup object
     * @param rs the ResultSet containing resource group data
     * @return ResourceGroup object mapped from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ResourceGroup mapResultSetToResourceGroup(ResultSet rs) throws SQLException {
        ResourceGroup group = new ResourceGroup();
        group.setGroupName(rs.getString("group_name"));
        group.setSecurityLevel(rs.getInt("security_level"));
        group.setFilePath(rs.getString("file_path"));
        group.setDescription(rs.getString("description"));
        return group;
    }

    /**
     * Loads resource members for a resource group from the database
     * @param group the ResourceGroup object to load members for
     */
    private void loadResourceGroupMembers(ResourceGroup group) {
        String sql = "SELECT resource_id FROM resource_group_members WHERE group_name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, group.getGroupName());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                group.addResource(rs.getString("resource_id"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load resource group members: " + e.getMessage());
        }
    }
}

