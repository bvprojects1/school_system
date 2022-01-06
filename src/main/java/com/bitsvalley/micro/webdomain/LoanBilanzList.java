package com.bitsvalley.micro.webdomain;

import java.util.ArrayList;
import java.util.List;

public class LoanBilanzList {

    List<LoanBilanz> loanBilanzList = new ArrayList<LoanBilanz>();
    String totalLoanInterest = "0";
    String totalLoan = "0";
    String currentLoanBalance = "0";
    String numberOfLoanAccounts = "0";
    String numberOfRetirementLoans = "0";
    Double interestOwed = 0.0;

    public List<LoanBilanz> getLoanBilanzList() {
        return loanBilanzList;
    }

    public void setLoanBilanzList(List<LoanBilanz> loanBilanzList) {
        this.loanBilanzList = loanBilanzList;
    }

    public String getTotalLoanInterest() {
        return totalLoanInterest;
    }

    public void setTotalLoanInterest(String totalLoanInterest) {
        this.totalLoanInterest = totalLoanInterest;
    }

    public String getTotalLoan() {
        return totalLoan;
    }

    public void setTotalLoan(String totalLoan) {
        this.totalLoan = totalLoan;
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

    public Double getInterestOwed() {
        return interestOwed;
    }

    public void setInterestOwed(Double interestOwed) {
        this.interestOwed = interestOwed;
    }

}
