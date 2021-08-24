package com.bitsvalley.micro.domain;

import com.bitsvalley.micro.utils.GeneralLedgerType;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Fru Chifen
 * 21.08.2021
 */

@Entity
@Table(name = "generallegder")
public class GeneralLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private GeneralLedgerType type;
    private String reference;
    private String accountNumber;
    private String createdBy;
    private String lastUpdatedBy;
    private Date createdDate;
    private Date lastUpdatedDate;
    private int amount;
    private Date date;
    private String notes;

    public GeneralLedger() {
        super();
    }

    public GeneralLedger(String user, String desc, Date targetDate, boolean isDone) {
        super();
    }

    public GeneralLedgerType getType() {
        return type;
    }

    public void setType(GeneralLedgerType type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
