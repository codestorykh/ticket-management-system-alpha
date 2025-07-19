package com.codestorykh.common.criteria;

public enum EntityFieldShare {
    
    ID("id", "Unique identifier"),
    ACTIVATE("isActivate", "Indicates whether the entity is active"),
    CREATE_BY("createdBy", "User who created the entity"),
    CREATE_DATE("creationDate", "Date when the entity was created"),
    LAST_MODIFIED_BY("lastModifiedBy", "User who last modified the entity"),
    LAST_MODIFIED_DATE("lastModifiedDate", "Date when the entity was last modified"),
    CODE("code", "Unique code for the entity"),
    DESCRIPTION("description", "Detailed description"),
    LOCAL_DESCRIPTION("localDescription", "Localized description"),
    NUM_ORDER("numOrder", "Sorting order number");

    private final String key;
    private final String description;

    EntityFieldShare(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
