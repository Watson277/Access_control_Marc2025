package com.bigcomp.accesscontrol.dao;

import com.bigcomp.accesscontrol.model.Profile;
import com.bigcomp.accesscontrol.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ProfileDAO interface.
 * Provides database operations for Profile entities using JDBC.
 * Also handles profile-resource group associations.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class ProfileDAOImpl implements ProfileDAO {
    /** Database connection manager */
    private DatabaseConnection dbConnection;

    /**
     * Default constructor
     */
    public ProfileDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Profile getProfileByName(String profileName) {
        String sql = "SELECT * FROM profiles WHERE profile_name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Profile profile = mapResultSetToProfile(rs);
                loadProfileResourceGroups(profile);
                return profile;
            }
        } catch (SQLException e) {
            System.err.println("查询配置文件失败: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Profile profile = mapResultSetToProfile(rs);
                loadProfileResourceGroups(profile);
                profiles.add(profile);
            }
        } catch (SQLException e) {
            System.err.println("查询所有配置文件失败: " + e.getMessage());
        }
        return profiles;
    }

    @Override
    public boolean insertProfile(Profile profile) {
        String sql = "INSERT INTO profiles (profile_name, file_path, description) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profile.getProfileName());
            pstmt.setString(2, profile.getFilePath());
            pstmt.setString(3, profile.getDescription());
            
            int result = pstmt.executeUpdate();
            
            // Insert associated resource groups
            if (result > 0 && !profile.getResourceGroupNames().isEmpty()) {
                insertProfileResourceGroups(profile);
            }
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println("插入配置文件失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profiles SET file_path = ?, description = ? WHERE profile_name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profile.getFilePath());
            pstmt.setString(2, profile.getDescription());
            pstmt.setString(3, profile.getProfileName());
            
            // Update associated resource groups
            deleteProfileResourceGroups(profile.getProfileName());
            if (!profile.getResourceGroupNames().isEmpty()) {
                insertProfileResourceGroups(profile);
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新配置文件失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteProfile(String profileName) {
        String sql = "DELETE FROM profiles WHERE profile_name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("删除配置文件失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * Maps a ResultSet row to a Profile object
     * @param rs the ResultSet containing profile data
     * @return Profile object mapped from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Profile mapResultSetToProfile(ResultSet rs) throws SQLException {
        Profile profile = new Profile();
        profile.setProfileName(rs.getString("profile_name"));
        profile.setFilePath(rs.getString("file_path"));
        profile.setDescription(rs.getString("description"));
        return profile;
    }

    /**
     * Loads associated resource groups for a profile from the database
     * @param profile the Profile object to load resource groups for
     */
    private void loadProfileResourceGroups(Profile profile) {
        String sql = "SELECT group_name FROM profile_resource_groups WHERE profile_name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profile.getProfileName());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                profile.addResourceGroup(rs.getString("group_name"));
            }
        } catch (SQLException e) {
            System.err.println("加载配置文件资源组失败: " + e.getMessage());
        }
    }

    /**
     * Inserts profile-resource group associations into the database
     * @param profile the Profile object containing resource group names to associate
     */
    private void insertProfileResourceGroups(Profile profile) {
        String sql = "INSERT INTO profile_resource_groups (profile_name, group_name) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String groupName : profile.getResourceGroupNames()) {
                pstmt.setString(1, profile.getProfileName());
                pstmt.setString(2, groupName);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("插入配置文件资源组关联失败: " + e.getMessage());
        }
    }

    /**
     * Deletes all profile-resource group associations for a profile
     * @param profileName the name of the profile
     */
    private void deleteProfileResourceGroups(String profileName) {
        String sql = "DELETE FROM profile_resource_groups WHERE profile_name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profileName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("删除配置文件资源组关联失败: " + e.getMessage());
        }
    }
}

