package com.bitsvalley.micro.utils;

public enum UserRole {


    AGENT("AGENT"), BOARD_MEMBER("BOARD_MEMBER"), MANAGER("MANAGER"), CUSTOMER("CUSTOMER"), AUDITOR("AUDITOR");

    private final String displayValue;

    private UserRole(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

}
