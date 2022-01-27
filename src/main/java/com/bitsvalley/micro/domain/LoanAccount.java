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
    private Date createdDate;
    private boolean defaultedPayment;
    private String branchCode;
    private AccountStatus accountStatus;
    private int glClass;
    private String intervalOfLoanPayment;
    private float interestRate;
    private float interestRateTTC;
    private float vatRate;
    private double minimumPayment;
    private String lastUpdatedBy;
    private Date lastUpdatedDate;
    private String productCode;
    private int termOfLoan;
    private boolean active;
    private boolean vatOption;
    private boolean blockBalanceQuarantor;
    private Date approvedDate;
    private String approvedBy;

    private double initiationFee;
    private double extraFee;
    private Date LastPaymentDate;
    private double monthlyPayment;

    private double interestOwed;

    private String guarantorAccountNumber1;
    private String guarantorAccountNumber2;
    private String guarantorAccountNumber3;

    private int guarantor1Amount1;
    private int guarantor1Amount2;
    private int guarantor1Amount3;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountType accountType;

    @Column(unique = true)
    private String accountNumber;
    private double currentLoanAmount;
    private String country;

    private double totalInterestOnLoan;

    @OneToMany(cascade = CascadeType.ALL)
    private List<LoanAccountTransaction> loanAccountTransaction = new ArrayList<LoanAccountTransaction>();

    private double loanAmount;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ShorteeAccount> shorteeAccounts = new ArrayList<ShorteeAccount>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<ShorteeAsset> shorteeAssets = new ArrayList<ShorteeAsset>();

    private String notes;

    @ManyToOne
    private User user;

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setMinimumPayment(int minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public int getTermOfLoan() {
        return termOfLoan;
    }

    public void setTermOfLoan(int termOfLoan) {
        this.termOfLoan = termOfLoan;
    }

    public int getGlClass() {
        return glClass;
    }

    public void setGlClass(int glClass) {
        this.glClass = glClass;
    }

    public String getGuarantorAccountNumber1() {
        return guarantorAccountNumber1;
    }

    public void setGuarantorAccountNumber1(String guarantorAccountNumber1) {
        this.guarantorAccountNumber1 = guarantorAccountNumber1;
    }

    public String getGuarantorAccountNumber2() {
        return guarantorAccountNumber2;
    }

    public void setGuarantorAccountNumber2(String guarantorAccountNumber2) {
        this.guarantorAccountNumber2 = guarantorAccountNumber2;
    }

    public String getGuarantorAccountNumber3() {
        return guarantorAccountNumber3;
    }

    public void setGuarantorAccountNumber3(String guarantorAccountNumber3) {
        this.guarantorAccountNumber3 = guarantorAccountNumber3;
    }

    public List<ShorteeAsset> getShorteeAssets() {
        return shorteeAssets;
    }

    public void setShorteeAssets(List<ShorteeAsset> shorteeAssets) {
        this.shorteeAssets = shorteeAssets;
    }

    public double getTotalInterestOnLoan() {
        return totalInterestOnLoan;
    }

    public void setTotalInterestOnLoan(double totalInterestOnLoan) {
        this.totalInterestOnLoan = totalInterestOnLoan;
    }

    public int getGuarantor1Amount1() {
        return guarantor1Amount1;
    }

    public void setGuarantor1Amount1(int guarantor1Amount1) {
        this.guarantor1Amount1 = guarantor1Amount1;
    }

    public int getGuarantor1Amount2() {
        return guarantor1Amount2;
    }

    public void setGuarantor1Amount2(int guarantor1Amount2) {
        this.guarantor1Amount2 = guarantor1Amount2;
    }

    public int getGuarantor1Amount3() {
        return guarantor1Amount3;
    }

    public void setGuarantor1Amount3(int guarantor1Amount3) {
        this.guarantor1Amount3 = guarantor1Amount3;
    }

    public void setInitiationFee(int initiationFee) {
        this.initiationFee = initiationFee;
    }

    public Date getLastPaymentDate() {
        return LastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        LastPaymentDate = lastPaymentDate;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public double getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(double minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

    public double getInitiationFee() {
        return initiationFee;
    }

    public void setInitiationFee(double initiationFee) {
        this.initiationFee = initiationFee;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getExtraFee() {
        return extraFee;
    }

    public void setExtraFee(double extraFee) {
        this.extraFee = extraFee;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isVatOption() {
        return vatOption;
    }

    public void setVatOption(boolean vatOption) {
        this.vatOption = vatOption;
    }

    public boolean isBlockBalanceQuarantor() {
        return blockBalanceQuarantor;
    }

    public void setBlockBalanceQuarantor(boolean blockBalanceQuarantor) {
        this.blockBalanceQuarantor = blockBalanceQuarantor;
    }

    public double getInterestOwed() {
        return interestOwed;
    }

    public void setInterestOwed(double interestOwed) {
        this.interestOwed = interestOwed;
    }

    public float getInterestRateTTC() {
        return interestRateTTC;
    }

    public void setInterestRateTTC(float interestRateTTC) {
        this.interestRateTTC = interestRateTTC;
    }

    public float getVatRate() {
        return vatRate;
    }

    public void setVatRate(float vatRate) {
        this.vatRate = vatRate;
    }
}
