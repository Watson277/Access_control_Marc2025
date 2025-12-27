package com.bigcomp.accesscontrol.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Access rule entity class defining access restrictions for a profile-resource group combination.
 * Rules can specify time windows, allowed days, access frequency limits, etc.
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class AccessRule {
    /** Unique identifier for the rule */
    private String ruleId;
    
    /** Name of the profile this rule applies to */
    private String profileName;
    
    /** Name of the resource group this rule applies to */
    private String resourceGroupName;
    
    /** List of allowed days of week (0=Sunday, 1=Monday, ..., 6=Saturday) */
    private List<Integer> allowedDaysOfWeek;
    
    /** Start time of allowed access window */
    private LocalTime startTime;
    
    /** End time of allowed access window */
    private LocalTime endTime;
    
    /** Maximum number of accesses allowed per day */
    private Integer maxAccessPerDay;
    
    /** Maximum number of accesses allowed per week */
    private Integer maxAccessPerWeek;
    
    /** Maximum number of accesses allowed per month */
    private Integer maxAccessPerMonth;
    
    /** Whether access to this resource requires precedence (e.g., must enter building before office) */
    private boolean requiresPrecedence;

    /**
     * Default constructor
     */
    public AccessRule() {
        this.allowedDaysOfWeek = new ArrayList<>();
    }

    /**
     * Constructor with main fields
     * @param ruleId unique identifier for the rule
     * @param profileName name of the profile this rule applies to
     * @param resourceGroupName name of the resource group this rule applies to
     */
    public AccessRule(String ruleId, String profileName, String resourceGroupName) {
        this.ruleId = ruleId;
        this.profileName = profileName;
        this.resourceGroupName = resourceGroupName;
        this.allowedDaysOfWeek = new ArrayList<>();
        this.requiresPrecedence = false;
    }

    // Getters and Setters
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getResourceGroupName() {
        return resourceGroupName;
    }

    public void setResourceGroupName(String resourceGroupName) {
        this.resourceGroupName = resourceGroupName;
    }

    public List<Integer> getAllowedDaysOfWeek() {
        return allowedDaysOfWeek;
    }

    public void setAllowedDaysOfWeek(List<Integer> allowedDaysOfWeek) {
        this.allowedDaysOfWeek = allowedDaysOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxAccessPerDay() {
        return maxAccessPerDay;
    }

    public void setMaxAccessPerDay(Integer maxAccessPerDay) {
        this.maxAccessPerDay = maxAccessPerDay;
    }

    public Integer getMaxAccessPerWeek() {
        return maxAccessPerWeek;
    }

    public void setMaxAccessPerWeek(Integer maxAccessPerWeek) {
        this.maxAccessPerWeek = maxAccessPerWeek;
    }

    public Integer getMaxAccessPerMonth() {
        return maxAccessPerMonth;
    }

    public void setMaxAccessPerMonth(Integer maxAccessPerMonth) {
        this.maxAccessPerMonth = maxAccessPerMonth;
    }

    public boolean isRequiresPrecedence() {
        return requiresPrecedence;
    }

    public void setRequiresPrecedence(boolean requiresPrecedence) {
        this.requiresPrecedence = requiresPrecedence;
    }

    @Override
    public String toString() {
        return "AccessRule{" +
                "ruleId='" + ruleId + '\'' +
                ", profileName='" + profileName + '\'' +
                ", resourceGroupName='" + resourceGroupName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}

