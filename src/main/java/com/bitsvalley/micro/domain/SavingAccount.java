package com.bitsvalley.micro.domain;
import com.bitsvalley.micro.utils.AccountStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Entity
@Table(name = "savingaccount")
public class SavingAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String createdBy;
    private String lastUpdatedBy;
    private Date createdDate;
    private Date lastUpdatedDate;
    private boolean accountMinBalanceLocked;
    private boolean defaultedPayment;

    private AccountStatus accountStatus;
    private int minimumPayment;
    private String intervalOfSaving;
    private int interestRate;
    private String country;
    private String branch;
    private String productCode;
    private String accountNumber;

    @ManyToOne
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    private SavingAccountType savingAccountType; //Leave

    @OneToMany(cascade = CascadeType.ALL)
    private List<SavingAccountTransaction> savingAccountTransaction = new ArrayList<SavingAccountTransaction>();

    private String notes;
    private double accountMinBalance;

    public boolean isDefaultedPayment() {
        return defaultedPayment;
    }

    public void setDefaultedPayment(boolean defaultedPayment) {
        this.defaultedPayment = defaultedPayment;
    }

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

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public int getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(int minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

    public String getIntervalOfSaving() {
        return intervalOfSaving;
    }

    public void setIntervalOfSaving(String intervalOfSaving) {
        this.intervalOfSaving = intervalOfSaving;
    }

    public SavingAccountType getAccountSavingType() {
        return savingAccountType;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
