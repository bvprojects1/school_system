package com.bitsvalley.micro.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Entity
@Table(name = "shorteeasset")
public class ShorteeAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int guarantorTypeAmount;
    private String guarantorType;
    private String guarantorDocumentPath;
    private Date createdDate;
    private Date lastUpdatedDate;
    private String notes;

    public int getGuarantorTypeAmount() {
        return guarantorTypeAmount;
    }

    public void setGuarantorTypeAmount(int guarantorTypeAmount) {
        this.guarantorTypeAmount = guarantorTypeAmount;
    }

    public String getGuarantorType() {
        return guarantorType;
    }

    public void setGuarantorType(String guarantorType) {
        this.guarantorType = guarantorType;
    }

    public String getGuarantorDocumentPath() {
        return guarantorDocumentPath;
    }

    public void setGuarantorDocumentPath(String guarantorDocumentPath) {
        this.guarantorDocumentPath = guarantorDocumentPath;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
