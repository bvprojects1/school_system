package com.bitsvalley.micro.webdomain;

import com.bitsvalley.micro.domain.SavingAccount;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LedgerEntryDTO {

    private String accountNumber;
    private long originLedgerAccount;
    private long destinationLedgerAccount;
    private double ledgerAmount;
    private String creditOrDebit;
    private String recordDate;
    private String notes;
    private boolean fromAccountToLedger;
    private List<String> paramValueString = new ArrayList<String>();
    private List<SavingAccount> savingAccounts;

    public long getOriginLedgerAccount() {
        return originLedgerAccount;
    }

    public void setOriginLedgerAccount(long originLedgerAccount) {
        this.originLedgerAccount = originLedgerAccount;
    }

    public Long getDestinationLedgerAccount() {
        return destinationLedgerAccount;
    }

    public void setDestinationLedgerAccount(Long destinationLedgerAccount) {
        this.destinationLedgerAccount = destinationLedgerAccount;
    }

    public double getLedgerAmount() {
        return ledgerAmount;
    }

    public void setLedgerAmount(double ledgerAmount) {
        this.ledgerAmount = ledgerAmount;
    }

    public String getCreditOrDebit() {
        return creditOrDebit;
    }

    public void setCreditOrDebit(String creditOrDebit) {
        this.creditOrDebit = creditOrDebit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDestinationLedgerAccount(long destinationLedgerAccount) {
        this.destinationLedgerAccount = destinationLedgerAccount;
    }

    public List<SavingAccount> getSavingAccounts() {
        return savingAccounts;
    }

    public void setSavingAccounts(List<SavingAccount> savingAccounts) {
        this.savingAccounts = savingAccounts;
    }

    public List<String> getParamValueString() {
        return paramValueString;
    }

    public void setParamValueString(List<String> paramValueString) {
        this.paramValueString = paramValueString;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public boolean isFromAccountToLedger() {
        return fromAccountToLedger;
    }

    public void setFromAccountToLedger(boolean fromAccountToLedger) {
        this.fromAccountToLedger = fromAccountToLedger;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
