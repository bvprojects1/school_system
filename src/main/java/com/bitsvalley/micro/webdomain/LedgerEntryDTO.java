package com.bitsvalley.micro.webdomain;

public class LedgerEntryDTO {

    private long originLedgerAccount;
    private long destinationLedgerAccount;
    private double ledgerAmount;
    private String creditOrDebit;
    private String notes;

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

}
