package com.bitsvalley.micro.webdomain;

import com.bitsvalley.micro.domain.LedgerAccount;
import java.util.List;

public class GLSearchDTO {

    private String startDate;
    private String endDate;
    private String creditOrDebit;
    private String accountNumber;
    private List<LedgerAccount> allLedgerAccount;

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

    public List<LedgerAccount> getAllLedgerAccount() {
        return allLedgerAccount;
    }

    public void setAllLedgerAccount(List<LedgerAccount> allLedgerAccount) {
        this.allLedgerAccount = allLedgerAccount;
    }

}
