package com.bitsvalley.micro.webdomain;

public class LedgerEntryDTO {

    private String originLedgerAccount;
    private String destinationLedgerAccount;
    private String ledgerAmount;
    private String creditOrDebit;
    private String notes;

    public String getOriginLedgerAccount() {
        return originLedgerAccount;
    }

    public void setOriginLedgerAccount(String originLedgerAccount) {
        this.originLedgerAccount = originLedgerAccount;
    }

    public String getDestinationLedgerAccount() {
        return destinationLedgerAccount;
    }

    public void setDestinationLedgerAccount(String destinationLedgerAccount) {
        this.destinationLedgerAccount = destinationLedgerAccount;
    }

    public String getLedgerAmount() {
        return ledgerAmount;
    }

    public void setLedgerAmount(String ledgerAmount) {
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
