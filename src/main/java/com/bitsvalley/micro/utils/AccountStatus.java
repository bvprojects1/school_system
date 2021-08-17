package com.bitsvalley.micro.utils;

public enum AccountStatus {

    ACTIVE("ACTIVE"), DELETED("DELETED"), SUSPENDED("SUSPENDED"), IN_ACTIVE("IN_ACTIVE"), NEW("NEW");

    private AccountStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    private final String displayValue;

}
