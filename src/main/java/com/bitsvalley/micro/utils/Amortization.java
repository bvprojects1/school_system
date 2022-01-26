package com.bitsvalley.micro.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 This class stores loan information and creates a
 text file containing an amortization report.
 */

public class Amortization
{
    private double loanAmount;   // Loan Amount
    private double interestRate; // Annual Interest Rate
    private double loanBalance;  // Monthly Balance
    private int loanMonths;       // Years of Loan
    private String startDate;
    private String monthlyPayment;
    private double totalInterest;
    private double InterestHT;
    private double InterestVAT;
    private String totalInterestLoanAmount;
    private String amortizationReport;
    private String interestRateString;
    private List<AmortizationRowEntry> amortizationRowEntryList = new ArrayList<AmortizationRowEntry>();

    /**
     The constructor accepts the loan amount, the annual
     interest rate, and the number of years of the loan
     as arguments. The private method CalcPayment is then
     called.
     @param loan The loan amount.
     @param rate The annual interest rate.
     @param months The number of years of the loan.
     */

    public Amortization(double loan, double rate, int months, double payment)
    {
        BigDecimal bd = new BigDecimal(rate);
        rate = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();

        loanAmount = loan;
        loanBalance = loan;
        interestRate = rate*0.01;
        loanMonths = months;
        monthlyPayment = BVMicroUtils.formatCurrency(payment);
        startDate = BVMicroUtils.formatDateOnly(LocalDate.now().plusMonths(1));

        interestRateString = BVMicroUtils.formatCurrency(rate);
        getAmortizationReport(payment);
    }

    /**
     The calcPayment method calculates the monthly payment
     amount. The result is stored in the payment field.
     */


    /**
     The getNumberOfPayments method returns the total number of
     payments to be made for the loan.
     @return The number of loan payments.
     */

    public int getNumberOfPayments()
    {
        return loanMonths;
    }

    public void getAmortizationReport(double payment )
    {
//        monthlyPayment = BVMicroUtils.formatCurrency(payment);
        double monthlyInterest;  // The monthly interest rate
        double principal = loanAmount;        // The amount of principal
//        DecimalFormat dollar = new DecimalFormat("#");
        LocalDate localDate = LocalDate.now();
        AmortizationRowEntry amortizationRowEntry = null;

        // Display the amortization table.
        for (int month = 1; month <= getNumberOfPayments(); month++) {
            amortizationRowEntry = new AmortizationRowEntry();
            // Calculate monthly interest.
            monthlyInterest = interestRate / 12.0 * loanBalance;
            totalInterest = totalInterest + monthlyInterest;
            if (month != getNumberOfPayments()) {
                // Calculate payment applied to principal
                principal = payment - monthlyInterest;
            } else    // This is the last month.
            {
                principal = loanBalance;
                payment = loanBalance + monthlyInterest;
            }

            // Calculate the new loan balance.
            loanBalance -= principal;

            amortizationRowEntry.setDate(BVMicroUtils.formatDateOnly(localDate.plusMonths(month)));
            amortizationRowEntry.setLoanBalance(BVMicroUtils.formatCurrency(loanBalance));
            amortizationRowEntry.setMonthlyInterest(monthlyInterest);
            amortizationRowEntry.setPrincipal(BVMicroUtils.formatCurrency(principal));
            amortizationRowEntry.setPayment(BVMicroUtils.formatCurrency(payment));
            amortizationRowEntry.setMonthNumber(month);
            amortizationRowEntryList.add(amortizationRowEntry);
        }

        totalInterestLoanAmount = BVMicroUtils.formatCurrency(totalInterest+loanAmount);

    }

    /**
     The getLoanAmount method returns the loan amount.
     @return The value in the loanAmount field.
     */

    public double getLoanAmount()
    {
        return loanAmount;
    }

    /**
     The getInterestRate method returns the interest rate.
     @return The value in the interestRate field.
     */

    public double getInterestRate()
    {
        return interestRate;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getLoanBalance() {
        return loanBalance;
    }

    public void setLoanBalance(double loanBalance) {
        this.loanBalance = loanBalance;
    }

    public int getLoanMonths() {
        return loanMonths;
    }

    public void setLoanMonths(int loanMonths) {
        this.loanMonths = loanMonths;
    }


    public double getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getAmortizationReport() {
        return amortizationReport;
    }

    public void setAmortizationReport(String amortizationReport) {
        this.amortizationReport = amortizationReport;
    }

    public List<AmortizationRowEntry> getAmortizationRowEntryList() {
        return amortizationRowEntryList;
    }

    public void setAmortizationRowEntryList(List<AmortizationRowEntry> amortizationRowEntryList) {
        this.amortizationRowEntryList = amortizationRowEntryList;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTotalInterestLoanAmount() {
        return totalInterestLoanAmount;
    }

    public void setTotalInterestLoanAmount(String totalInterestLoanAmount) {
        this.totalInterestLoanAmount = totalInterestLoanAmount;
    }

    public String getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(String monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public String getInterestRateString() {
        return interestRateString;
    }

    public void setInterestRateString(String interestRateString) {
        this.interestRateString = interestRateString;
    }

    public double getInterestHT() {
        return InterestHT;
    }

    public void setInterestHT(double interestHT) {
        InterestHT = interestHT;
    }

    public double getInterestVAT() {
        return InterestVAT;
    }

    public void setInterestVAT(double interestVAT) {
        InterestVAT = interestVAT;
    }
}