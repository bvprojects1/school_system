package com.bitsvalley.micro.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Fru Chifen
 * 09.07.2021
 */
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String userName;
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private String profession;
    private String telephone1;
    private String telephone2;
    private String address;
    private String password;
    private String notes;
    private String email;
    private String dateOfBirth;
    private String idFilePath;
    private String identityCardNumber;
    @ManyToOne(cascade = CascadeType.ALL)
    private Branch branch;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<UserRole> userRole;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SavingAccount> savingAccount = new ArrayList<SavingAccount>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<CurrentAccount> currentAccount = new ArrayList<CurrentAccount>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Beneficiary> beneficiary = new ArrayList<Beneficiary>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<LoanAccount> loanAccount = new ArrayList<LoanAccount>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<ShareAccount> shareAccount = new ArrayList<ShareAccount>();

    private LocalDateTime accountExpiredDate;
    private boolean accountLocked;

    private LocalDateTime accountBlockedDate;
    private boolean accountExpired;
    private String identityCardExpiry;
    private Date lastUpdated;
    private Date created;
    private String createdBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public void setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserRole> getUserRole() {
        return userRole;
    }

    public void setUserRole(List<UserRole> userRole) {
        this.userRole = userRole;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<SavingAccount> getSavingAccount() {
        return savingAccount;
    }

    public void setSavingAccount(List<SavingAccount> savingAccount) {
        this.savingAccount = savingAccount;
    }

    public LocalDateTime getAccountExpiredDate() {
        return accountExpiredDate;
    }

    public void setAccountExpiredDate(LocalDateTime accountExpiredDate) {
        this.accountExpiredDate = accountExpiredDate;
    }

    public LocalDateTime getAccountBlockedDate() {
        return accountBlockedDate;
    }

    public void setAccountBlockedDate(LocalDateTime accountBlockedDate) {
        this.accountBlockedDate = accountBlockedDate;
    }

    public boolean isAccountExpired() {
        return accountExpired;
    }

    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }



    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getIdFilePath() {
        return idFilePath;
    }

    public void setIdFilePath(String idFilePath) {
        this.idFilePath = idFilePath;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public List<LoanAccount> getLoanAccount() {
        return loanAccount;
    }

    public void setLoanAccount(List<LoanAccount> loanAccount) {
        this.loanAccount = loanAccount;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public List<CurrentAccount> getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(List<CurrentAccount> currentAccount) {
        this.currentAccount = currentAccount;
    }

    public List<ShareAccount> getShareAccount() {
        return shareAccount;
    }

    public void setShareAccount(List<ShareAccount> shareAccount) {
        this.shareAccount = shareAccount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Beneficiary> getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(List<Beneficiary> beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getIdentityCardExpiry() {
        return identityCardExpiry;
    }

    public void setIdentityCardExpiry(String identityCardExpiry) {
        this.identityCardExpiry = identityCardExpiry;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
}
