package com.bitsvaley.micro.domain;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Entity
@Table(name = "savingaccount")
public class SavingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String createdBy;
    private String lastUpdatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private boolean accountMinBalanceLocked;

    private String accountNumber;
    private int interestRate;

    @ManyToOne
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    private SavingAccountType savingAccountType; //Leave

    @OneToMany(cascade = CascadeType.ALL)
    private List<SavingAccountTransaction> savingAccountTransaction = new ArrayList<SavingAccountTransaction>();

    private String notes;
    private double accountMinBalance;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<SavingAccountTransaction> getSavingAccountTransaction() {
        return savingAccountTransaction;
    }

    public void setSavingAccountTransaction(List<SavingAccountTransaction> savingAccountTransaction) {
        this.savingAccountTransaction = savingAccountTransaction;
    }

    public SavingAccountType getAccountSavingsType() {
        return savingAccountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAccountMinBalance() {
        return accountMinBalance;
    }

    public void setAccountMinBalance(double accountMinBalance) {
        this.accountMinBalance = accountMinBalance;
    }

    public boolean isAccountMinBalanceLocked() {
        return accountMinBalanceLocked;
    }

    public void setAccountMinBalanceLocked(boolean accountMinBalanceLocked) {
        this.accountMinBalanceLocked = accountMinBalanceLocked;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public int getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(int interestRate) {
        this.interestRate = interestRate;
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

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }


    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountMinBalanceLocked = accountLocked;
    }

    public boolean getAccountLocked() {
        return accountMinBalanceLocked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SavingAccountType getSavingAccountType() {
        return savingAccountType;
    }

    public void setSavingAccountType(SavingAccountType savingAccountType) {
        this.savingAccountType = savingAccountType;
    }

}
