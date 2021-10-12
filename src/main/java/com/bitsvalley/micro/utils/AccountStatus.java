package com.bitsvalley.micro.utils;

public enum AccountStatus {

    ACTIVE("ACTIVE"), DELETED("DELETED"), SHORTEE_ACCOUNT("SHORTEE_ACCOUNT"), SUSPENDED("SUSPENDED"), DEFAULTED("DEFAULTED"), IN_ACTIVE("IN_ACTIVE"), PENDING_APPROVAL("PENDING_APPROVAL");

    private AccountStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    private final String displayValue;

}
