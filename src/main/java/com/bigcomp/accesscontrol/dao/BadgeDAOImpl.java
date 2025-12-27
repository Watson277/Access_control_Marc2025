package com.bigcomp.accesscontrol.dao;

import com.bigcomp.accesscontrol.model.Badge;
import com.bigcomp.accesscontrol.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BadgeDAO interface.
 * Provides database operations for Badge entities using JDBC.
 * Also handles badge-profile associations.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class BadgeDAOImpl implements BadgeDAO {
    /** Database connection manager */
    private DatabaseConnection dbConnection;

    /**
     * Default constructor
     */
    public BadgeDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Badge getBadgeById(String badgeId) {
        String sql = "SELECT * FROM badges WHERE badge_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badgeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Badge badge = mapResultSetToBadge(rs);
                // Load associated profiles
                loadBadgeProfiles(badge);
                return badge;
            }
        } catch (SQLException e) {
            System.err.println("查询徽章失败: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Badge> getAllBadges() {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT * FROM badges";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Badge badge = mapResultSetToBadge(rs);
                loadBadgeProfiles(badge);
                badges.add(badge);
            }
        } catch (SQLException e) {
            System.err.println("查询所有徽章失败: " + e.getMessage());
        }
        return badges;
    }

    @Override
    public boolean insertBadge(Badge badge) {
        String sql = "INSERT INTO badges (badge_id, user_id, created_date, expiration_date, last_update_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badge.getBadgeId());
            pstmt.setString(2, badge.getUserId());
            pstmt.setDate(3, badge.getCreatedDate() != null ? Date.valueOf(badge.getCreatedDate()) : null);
            pstmt.setDate(4, badge.getExpirationDate() != null ? Date.valueOf(badge.getExpirationDate()) : null);
            pstmt.setDate(5, badge.getLastUpdateDate() != null ? Date.valueOf(badge.getLastUpdateDate()) : null);
            pstmt.setBoolean(6, badge.isActive());
            
            int result = pstmt.executeUpdate();
            
            // Insert associated profiles
            if (result > 0 && !badge.getProfileNames().isEmpty()) {
                insertBadgeProfiles(badge);
            }
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println("插入徽章失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateBadge(Badge badge) {
        String sql = "UPDATE badges SET user_id = ?, expiration_date = ?, last_update_date = ?, is_active = ? " +
                     "WHERE badge_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badge.getUserId());
            pstmt.setDate(2, badge.getExpirationDate() != null ? Date.valueOf(badge.getExpirationDate()) : null);
            pstmt.setDate(3, badge.getLastUpdateDate() != null ? Date.valueOf(badge.getLastUpdateDate()) : null);
            pstmt.setBoolean(4, badge.isActive());
            pstmt.setString(5, badge.getBadgeId());
            
            // Update associated profiles
            deleteBadgeProfiles(badge.getBadgeId());
            if (!badge.getProfileNames().isEmpty()) {
                insertBadgeProfiles(badge);
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新徽章失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteBadge(String badgeId) {
        String sql = "DELETE FROM badges WHERE badge_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badgeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("删除徽章失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Badge> getBadgesByUserId(String userId) {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT * FROM badges WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Badge badge = mapResultSetToBadge(rs);
                loadBadgeProfiles(badge);
                badges.add(badge);
            }
        } catch (SQLException e) {
            System.err.println("查询用户徽章失败: " + e.getMessage());
        }
        return badges;
    }

    /**
     * Maps a ResultSet row to a Badge object
     * @param rs the ResultSet containing badge data
     * @return Badge object mapped from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Badge mapResultSetToBadge(ResultSet rs) throws SQLException {
        Badge badge = new Badge();
        badge.setBadgeId(rs.getString("badge_id"));
        badge.setUserId(rs.getString("user_id"));
        Date createdDate = rs.getDate("created_date");
        if (createdDate != null) {
            badge.setCreatedDate(createdDate.toLocalDate());
        }
        Date expirationDate = rs.getDate("expiration_date");
        if (expirationDate != null) {
            badge.setExpirationDate(expirationDate.toLocalDate());
        }
        Date lastUpdateDate = rs.getDate("last_update_date");
        if (lastUpdateDate != null) {
            badge.setLastUpdateDate(lastUpdateDate.toLocalDate());
        }
        badge.setActive(rs.getBoolean("is_active"));
        return badge;
    }

    /**
     * Loads associated profiles for a badge from the database
     * @param badge the Badge object to load profiles for
     */
    private void loadBadgeProfiles(Badge badge) {
        String sql = "SELECT profile_name FROM badge_profiles WHERE badge_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badge.getBadgeId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                badge.addProfile(rs.getString("profile_name"));
            }
        } catch (SQLException e) {
            System.err.println("加载徽章配置文件失败: " + e.getMessage());
        }
    }

    /**
     * Inserts badge-profile associations into the database
     * @param badge the Badge object containing profile names to associate
     */
    private void insertBadgeProfiles(Badge badge) {
        String sql = "INSERT INTO badge_profiles (badge_id, profile_name) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String profileName : badge.getProfileNames()) {
                pstmt.setString(1, badge.getBadgeId());
                pstmt.setString(2, profileName);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("插入徽章配置文件关联失败: " + e.getMessage());
        }
    }

    /**
     * Deletes all badge-profile associations for a badge
     * @param badgeId the ID of the badge
     */
    private void deleteBadgeProfiles(String badgeId) {
        String sql = "DELETE FROM badge_profiles WHERE badge_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, badgeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("删除徽章配置文件关联失败: " + e.getMessage());
        }
    }
}

