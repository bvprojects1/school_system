package com.bitsvalley.micro.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Entity
@Table(name = "savingAccountTransaction")
public class SavingAccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String createdBy;
    private LocalDateTime createdDate;
    private int savingAmount;
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
    private String savingAmountInLetters;

    @Column(unique = true)
    private String reference;

    public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public long getId() {
        return id;
    }

    public long getBranch() {
        return branch;
    }

    public void setBranch(long branch) {
        this.branch = branch;
    }

    @ManyToOne
    private SavingAccount savingAccount;

    public SavingAccount getSavingAccount() {
        return savingAccount;
    }

    public void setSavingAccount(SavingAccount savingAccount) {
        this.savingAccount = savingAccount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public int getSavingAmount() {
        return savingAmount;
    }

    public void setSavingAmount(int savingAmount) {
        this.savingAmount = savingAmount;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }


    public String getSavingAmountInLetters() {
        return savingAmountInLetters;
    }

    public void setSavingAmountInLetters(String savingAmountInLetters) {
        this.savingAmountInLetters = savingAmountInLetters;
    }
}
