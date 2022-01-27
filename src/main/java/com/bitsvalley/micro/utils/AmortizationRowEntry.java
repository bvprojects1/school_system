package com.bitsvalley.micro.utils;

public class AmortizationRowEntry {

    double monthlyInterest;
    String payment;
    String principal;
    String loanBalance;
    String date;
    int monthNumber;
    double VATOnInterest;
    double interestOnHT;
    double interestOnTTC;

    public double getMonthlyInterest() {
        return monthlyInterest;
    }

    public void setMonthlyInterest(double monthlyInterest) {
        this.monthlyInterest = monthlyInterest;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getLoanBalance() {
        return loanBalance;
    }

    public void setLoanBalance(String loanBalance) {
        this.loanBalance = loanBalance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public double getVATOnInterest() {
        return VATOnInterest;
    }

    public void setVATOnInterest(double VATOnInterest) {
        this.VATOnInterest = VATOnInterest;
    }

    public double getInterestOnHT() {
        return interestOnHT;
    }

    public void setInterestOnHT(double interestOnHT) {
        this.interestOnHT = interestOnHT;
    }

    public void setInterestOnTTC(double interestOnTTC) {
        this.interestOnTTC = interestOnTTC;
    }

    public double getInterestOnTTC() {
        return interestOnTTC;
    }
}
