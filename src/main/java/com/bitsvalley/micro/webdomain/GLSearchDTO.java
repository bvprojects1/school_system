package com.bitsvalley.micro.webdomain;

import com.bitsvalley.micro.domain.LedgerAccount;
import com.bitsvalley.micro.domain.User;

import java.util.List;

public class GLSearchDTO {

    private String startDate;
    private String endDate;
    private String creditOrDebit;
    private String accountNumber;
    private List<Integer> allLedgerAccount;
    private List<String> allGLEntryUsers;

    public String getCreditOrDebit() {
        return creditOrDebit;
    }

    public void setCreditOrDebit(String creditOrDebit) {
        this.creditOrDebit = creditOrDebit;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<Integer> getAllLedgerAccount() {
        return allLedgerAccount;
    }

    public void setAllLedgerAccount(List<Integer> allLedgerAccount) {
        this.allLedgerAccount = allLedgerAccount;
    }

    public List<String> getAllGLEntryUsers() {
        return allGLEntryUsers;
    }

    public void setAllGLEntryUsers(List<String> allGLEntryUsers) {
        this.allGLEntryUsers = allGLEntryUsers;
    }
}
