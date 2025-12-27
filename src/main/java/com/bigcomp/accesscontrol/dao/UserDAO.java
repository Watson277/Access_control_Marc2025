package com.bigcomp.accesscontrol.dao;

import com.bigcomp.accesscontrol.model.User;
import java.util.List;

/**
 * Data Access Object interface for User entities.
 * Provides CRUD operations for user management.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public interface UserDAO {
    /**
     * Retrieves a user by their ID
     * @param userId the unique identifier of the user
     * @return User object if found, null otherwise
     */
    User getUserById(String userId);
    
    /**
     * Retrieves all users from the database
     * @return list of all User objects
     */
    List<User> getAllUsers();
    
    /**
     * Inserts a new user into the database
     * @param user the User object to insert
     * @return true if insertion was successful, false otherwise
     */
    boolean insertUser(User user);
    
    /**
     * Updates an existing user in the database
     * @param user the User object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean updateUser(User user);
    
    /**
     * Deletes a user from the database
     * @param userId the unique identifier of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteUser(String userId);
}

