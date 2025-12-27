package com.bigcomp.accesscontrol.dao;

import com.bigcomp.accesscontrol.model.Profile;
import java.util.List;

/**
 * Data Access Object interface for Profile entities.
 * Provides CRUD operations for profile management.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public interface ProfileDAO {
    /**
     * Retrieves a profile by its name
     * @param profileName the name of the profile
     * @return Profile object if found, null otherwise
     */
    Profile getProfileByName(String profileName);
    
    /**
     * Retrieves all profiles from the database
     * @return list of all Profile objects
     */
    List<Profile> getAllProfiles();
    
    /**
     * Inserts a new profile into the database
     * @param profile the Profile object to insert
     * @return true if insertion was successful, false otherwise
     */
    boolean insertProfile(Profile profile);
    
    /**
     * Updates an existing profile in the database
     * @param profile the Profile object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean updateProfile(Profile profile);
    
    /**
     * Deletes a profile from the database
     * @param profileName the name of the profile to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteProfile(String profileName);
}

