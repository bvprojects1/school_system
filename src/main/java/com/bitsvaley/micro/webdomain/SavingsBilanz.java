package com.bitsvaley.micro.webdomain;

public class SavingsBilanz {

    String createdDate;
    int interestAccrued;
    String notes;
    String agent;
    String noOfDays;
    int savingsAmount;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getInterestAccrued() {
        return interestAccrued;
    }

    public void setInterestAccrued(int interestAccrued) {
        this.interestAccrued = interestAccrued;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(String noOfDays) {
        this.noOfDays = noOfDays;
    }

    public int getSavingsAmount() {
        return savingsAmount;
    }

    public void setSavingsAmount(int savingsAmount) {
        this.savingsAmount = savingsAmount;
    }
}
