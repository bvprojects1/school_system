package com.bitsvalley.micro.webdomain;

public class TransferBilanz {

    String transferFromAccount;
    double transferAmount;
    String transferToAccount;
    String reference;
    String transferDateTime;
    String userAgent;
    String notes;
    String transferFromText;
    String transferToText;

    public double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(double transferAmount) {
        this.transferAmount = transferAmount;
    }


    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTransferDateTime() {
        return transferDateTime;
    }

    public void setTransferDateTime(String transferDateTime) {
        this.transferDateTime = transferDateTime;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getTransferFromAccount() {
        return transferFromAccount;
    }

    public void setTransferFromAccount(String transferFromAccount) {
        this.transferFromAccount = transferFromAccount;
    }

    public String getTransferToAccount() {
        return transferToAccount;
    }

    public void setTransferToAccount(String toAccount) {
        this.transferToAccount = toAccount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTransferFromText() {
        return transferFromText;
    }

    public void setTransferFromText(String transferFromText) {
        this.transferFromText = transferFromText;
    }

    public String getTransferToText() {
        return transferToText;
    }

    public void setTransferToText(String transferToText) {
        this.transferToText = transferToText;
    }

}
