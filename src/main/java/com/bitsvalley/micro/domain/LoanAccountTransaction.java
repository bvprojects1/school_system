package com.bitsvalley.micro.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Entity
@Table(name = "loanAccountTransaction")
public class LoanAccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double accountBalance;
    private String createdBy;
    private LocalDateTime createdDate;
    private double loanAmount;
    private double currentLoanAmount;
    private double interestPaid;
    private double interestNotPaid;
    private double transactionCharge;
    private double amountReceived;
    private String notes;
    private String modeOfPayment;
    private int tenThousand;
    private int fiveThousand;
    private int twoThousand;
    private int oneThousand;
    private int fiveHundred;
    private int oneHundred;
    private int fifty;
    private int twentyFive;
    private String accountOwner;
    private long branch;
    private String branchCode;
    private String branchCountry;
    private String loanAmountInLetters;
    private double vatPercent;
    private String increaseGuarantorMinimum;
    private int withdrawalDeposit;

    @Column(unique = true)
    private String reference;

    @ManyToOne
    private LoanAccount loanAccount;
    private String representative;

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getIncreaseGuarantorMinimum() {
        return increaseGuarantorMinimum;
    }

    public void setIncreaseGuarantorMinimum(String increaseGuarantorMinimum) {
        this.increaseGuarantorMinimum = increaseGuarantorMinimum;
    }

    public long getId() {
        return id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public int getTenThousand() {
        return tenThousand;
    }

    public void setTenThousand(int tenThousand) {
        this.tenThousand = tenThousand;
    }

    public int getFiveThousand() {
        return fiveThousand;
    }

    public void setFiveThousand(int fiveThousand) {
        this.fiveThousand = fiveThousand;
    }

    public int getTwoThousand() {
        return twoThousand;
    }

    public void setTwoThousand(int twoThousand) {
        this.twoThousand = twoThousand;
    }

    public int getOneThousand() {
        return oneThousand;
    }

    public void setOneThousand(int oneThousand) {
        this.oneThousand = oneThousand;
    }

    public int getFiveHundred() {
        return fiveHundred;
    }

    public void setFiveHundred(int fiveHundred) {
        this.fiveHundred = fiveHundred;
    }

    public int getOneHundred() {
        return oneHundred;
    }

    public void setOneHundred(int oneHundred) {
        this.oneHundred = oneHundred;
    }

    public int getFifty() {
        return fifty;
    }

    public void setFifty(int fifty) {
        this.fifty = fifty;
    }

    public int getTwentyFive() {
        return twentyFive;
    }

    public void setTwentyFive(int twentyFive) {
        this.twentyFive = twentyFive;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }

    public long getBranch() {
        return branch;
    }

    public void setBranch(long branch) {
        this.branch = branch;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchCountry() {
        return branchCountry;
    }

    public void setBranchCountry(String branchCountry) {
        this.branchCountry = branchCountry;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LoanAccount getLoanAccount() {
        return loanAccount;
    }

    public void setLoanAccount(LoanAccount loanAccount) {
        this.loanAccount = loanAccount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanAmountInLetters() {
        return loanAmountInLetters;
    }

    public void setLoanAmountInLetters(String loanAmountInLetters) {
        this.loanAmountInLetters = loanAmountInLetters;
    }

    public double getCurrentLoanAmount() {
        return currentLoanAmount;
    }

    public void setCurrentLoanAmount(double currentLoanAmount) {
        this.currentLoanAmount = currentLoanAmount;
    }

    public double getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(double interestPaid) {
        this.interestPaid = interestPaid;
    }

    public double getInterestNotPaid() {
        return interestNotPaid;
    }

    public void setInterestNotPaid(double interestNotPaid) {
        this.interestNotPaid = interestNotPaid;
    }

    public double getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(double amountReceived) {
        this.amountReceived = amountReceived;
    }

    public double getTransactionCharge() {
        return transactionCharge;
    }

    public void setTransactionCharge(double transactionCharge) {
        this.transactionCharge = transactionCharge;
    }

    public double getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(double vatPercent) {
        this.vatPercent = vatPercent;
    }

    public int getWithdrawalDeposit() {
        return withdrawalDeposit;
    }

    public void setWithdrawalDeposit(int withdrawalDeposit) {
        this.withdrawalDeposit = withdrawalDeposit;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }
}
