package com.emtech.dairyapp.Auth.Role;

import lombok.Getter;

@Getter
public enum AccessRight {
    VIEW_DASHBOARD("View dashboard"),
    VIEW_BUSINESS_PROFILE("View business profile"),
    VIEW_COUNTIES("View counties"),
    CREATE_COUNTY("Create county"),
    UPDATE_COUNTY("Update county"),
    DELETE_COUNTY("Delete county"),
    VIEW_SUB_COUNTIES("View sub counties"),
    CREATE_SUB_COUNTY("Create sub county"),
    UPDATE_SUB_COUNTY("Update sub county"),
    DELETE_SUB_COUNTY("Delete sub county"),
    VIEW_PICK_UP_LOCATIONS("View pick up locations"),
    CREATE_PICK_UP_LOCATION("Create pick up location"),
    UPDATE_PICK_UP_LOCATION("Update  pick up location"),
    DELETE_PICK_UP_LOCATION("Delete pick up location"),
    VIEW_FARMERS("View farmers"),
    CREATE_FARMER("Create farmer"),
    UPDATE_FARMER("Update farmer"),
    DELETE_FARMER("Delete farmer"),
    VIEW_FLOATS("View floats"),
    CREATE_FLOAT("Create float"),
    UPDATE_FLOAT("Update float"),
    DELETE_FLOAT("Delete float"),
    VIEW_USERS("View users"),
    CREATE_USER("Create user"),
    UPDATE_USER("Update user"),
    VIEW_SALES("View sales"),
    CREATE_SALE("Create sale"),
    VIEW_PRODUCTS_CONFIGURATIONS("View products configurations"),
    CREATE_PRODUCT_CONFIGURATION("Create product configuration"),
    UPDATE_PRODUCT_CONFIGURATION("Update product configuration"),
    DELETE_PRODUCT_CONFIGURATION("Delete product configuration"),
    UPDATE_SALE("Update sale"),
    DELETE_SALE("Delete sale"),
    VIEW_COLLECTIONS("View collections"),
    CREATE_COLLECTION("Create collection"),
    UPDATE_COLLECTION("Update  collection"),
    DELETE_COLLECTION("Delete collection"),
    ACTIVATE_USER("Activate user"),
    DEACTIVATE_USER("Deactivate user"),
    DELETE_USER("Update user"),
    ASSIGN_ROLE("Assign role"),
    VIEW_ROLES("View roles"),
    CREATE_ROLE("Create role"),
    UPDATE_ROLE("Update role"),
    DELETE_ROLE("Delete role");
    private final String name;
    AccessRight(String name) {
        this.name = name;
    }
}
