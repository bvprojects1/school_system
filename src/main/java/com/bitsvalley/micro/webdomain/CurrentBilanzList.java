package com.bitsvalley.micro.webdomain;

import java.util.ArrayList;
import java.util.List;

public class CurrentBilanzList {

    List<CurrentBilanz> currentBilanzList = new ArrayList<CurrentBilanz>();
    String totalCurrentInterest = "0";
    String totalCurrent = "0";
    String currentLoanBalance = "";
    String numberOfLoanAccounts = "0";
    String numberOfRetirementLoans = "0";

    public List<CurrentBilanz> getCurrentBilanzList() {
        return currentBilanzList;
    }

    public void setCurrentBilanzList(List<CurrentBilanz> currentBilanzList) {
        this.currentBilanzList = currentBilanzList;
    }

    public String getTotalCurrentInterest() {
        return totalCurrentInterest;
    }

    public void setTotalCurrentInterest(String totalCurrentInterest) {
        this.totalCurrentInterest = totalCurrentInterest;
    }

    public String getTotalCurrent() {
        return totalCurrent;
    }

    public void setTotalCurrent(String totalCurrent) {
        this.totalCurrent = totalCurrent;
    }

    public String getNumberOfLoanAccounts() {
        return numberOfLoanAccounts;
    }

    public void setNumberOfLoanAccounts(String numberOfLoanAccounts) {
        this.numberOfLoanAccounts = numberOfLoanAccounts;
    }

    public String getNumberOfRetirementLoans() {
        return numberOfRetirementLoans;
    }

    public void setNumberOfRetirementLoans(String numberOfRetirementLoans) {
        this.numberOfRetirementLoans = numberOfRetirementLoans;
    }

    public String getCurrentLoanBalance() {
        return currentLoanBalance;
    }

    public void setCurrentLoanBalance(String currentLoanBalance) {
        this.currentLoanBalance = currentLoanBalance;
    }



}
