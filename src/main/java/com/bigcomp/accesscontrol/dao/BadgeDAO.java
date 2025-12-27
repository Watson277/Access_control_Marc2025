package com.bigcomp.accesscontrol.dao;

import com.bigcomp.accesscontrol.model.Badge;
import java.util.List;

/**
 * Data Access Object interface for Badge entities.
 * Provides CRUD operations for badge management.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public interface BadgeDAO {
    /**
     * Retrieves a badge by its ID
     * @param badgeId the unique identifier of the badge
     * @return Badge object if found, null otherwise
     */
    Badge getBadgeById(String badgeId);
    
    /**
     * Retrieves all badges from the database
     * @return list of all Badge objects
     */
    List<Badge> getAllBadges();
    
    /**
     * Inserts a new badge into the database
     * @param badge the Badge object to insert
     * @return true if insertion was successful, false otherwise
     */
    boolean insertBadge(Badge badge);
    
    /**
     * Updates an existing badge in the database
     * @param badge the Badge object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean updateBadge(Badge badge);
    
    /**
     * Deletes a badge from the database
     * @param badgeId the unique identifier of the badge to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteBadge(String badgeId);
    
    /**
     * Retrieves all badges owned by a specific user
     * @param userId the unique identifier of the user
     * @return list of Badge objects owned by the user
     */
    List<Badge> getBadgesByUserId(String userId);
}

