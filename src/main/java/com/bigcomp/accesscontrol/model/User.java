package com.bigcomp.accesscontrol.model;

/**
 * User entity class representing a person in the access control system.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class User {
    /** Unique identifier for the user */
    private String userId;
    
    /** Gender of the user */
    private Gender gender;
    
    /** First name of the user */
    private String firstName;
    
    /** Last name of the user */
    private String lastName;
    
    /** Type of user (employee, contractor, intern, or visitor) */
    private UserType userType;

    /**
     * Gender enumeration
     */
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    /**
     * User type enumeration
     */
    public enum UserType {
        EMPLOYEE,      // Permanent employee
        CONTRACTOR,    // Contractor
        INTERN,        // Intern
        VISITOR        // Visitor
    }

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor with all fields
     * @param userId unique identifier for the user
     * @param gender gender of the user
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @param userType type of user
     */
    public User(String userId, Gender gender, String firstName, String lastName, UserType userType) {
        this.userId = userId;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * Gets the full name of the user (first name + last name)
     * @return full name string
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", gender=" + gender +
                ", userType=" + userType +
                '}';
    }
}

