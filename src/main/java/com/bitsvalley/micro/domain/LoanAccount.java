package com.bitsvalley.micro.domain;
import com.bitsvalley.micro.utils.AccountStatus;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Entity
@Table(name = "loanaccount")
public class LoanAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String createdBy;
    private String lastUpdatedBy;
    private Date createdDate;
    private Date lastUpdatedDate;
    private boolean defaultedPayment;
    private String branchCode;
    private AccountStatus accountStatus;
    private int minimumPayment;
    private String intervalOfLoanPayment;
    private int interestRate;
    private String country;
    private String productCode;

    @Column(unique = true)
    private String accountNumber;
    private double currentLoanAmount;
    private double loanAmount;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ShorteeAccount> shorteeAccounts = new ArrayList<ShorteeAccount>();

    @OneToOne(cascade = CascadeType.ALL)
    private AccountType accountType;

    @ManyToOne
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    private List<LoanAccountTransaction> loanAccountTransaction = new ArrayList<LoanAccountTransaction>();

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

    public double getAccountMinBalance() {
        return accountMinBalance;
    }

    public void setAccountMinBalance(double accountMinBalance) {
        this.accountMinBalance = accountMinBalance;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public double getCurrentLoanAmount() {
        return currentLoanAmount;
    }

    public void setCurrentLoanAmount(double currentLoanAmount) {
        this.currentLoanAmount = currentLoanAmount;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getIntervalOfLoanPayment() {
        return intervalOfLoanPayment;
    }

    public void setIntervalOfLoanPayment(String intervalOfLoanPayment) {
        this.intervalOfLoanPayment = intervalOfLoanPayment;
    }

    public List<LoanAccountTransaction> getLoanAccountTransaction() {
        return loanAccountTransaction;
    }

    public void setLoanAccountTransaction(List<LoanAccountTransaction> loanAccountTransaction) {
        this.loanAccountTransaction = loanAccountTransaction;
    }


    public List<ShorteeAccount> getShorteeAccounts() {
        return shorteeAccounts;
    }

    public void setShorteeAccounts(List<ShorteeAccount> shorteeAccounts) {
        this.shorteeAccounts = shorteeAccounts;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

}
