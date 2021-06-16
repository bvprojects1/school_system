package com.bitsvaley.micro.domain;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private String firstName;
    private String lastName;
    private String telephone1;
    private String telephone2;
    private String address;
    private String password;
    private String email;
    private String beneficiary;
    private String dateOfBirth;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<UserRole> userRole;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SavingAccount> savingAccount = new ArrayList<SavingAccount>();

    private LocalDateTime accountExpiredDate;
    private boolean accountLocked;

    private LocalDateTime accountBlockedDate;
    private boolean accountExpired;

    private LocalDateTime lastUpdated;
    private LocalDateTime created;

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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
