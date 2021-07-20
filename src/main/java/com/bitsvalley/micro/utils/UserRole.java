package com.bitsvalley.micro.utils;

public enum UserRole {


    AGENT("ROLE_AGENT"), BOARD_MEMBER("ROLE_BOARD_MEMBER"), MANAGER("ROLE_MANAGER"), CUSTOMER("ROLE_CUSTOMER"), AUDITOR("ROLE_AUDITOR");

    private final String displayValue;

    private UserRole(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

}
