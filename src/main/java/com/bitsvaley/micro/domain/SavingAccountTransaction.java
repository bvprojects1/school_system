package com.bitsvaley.micro.domain;

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
//    private String name;
    
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

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

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
}
