package com.bitsvalley.micro.domain;

import javax.persistence.*;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Entity
@Table(name = "ledgeraccount")
public class LedgerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<GeneralLedger> generalLedger;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String code;
    private String category;
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GeneralLedger> getGeneralLedger() {
        return generalLedger;
    }

    public void setGeneralLedger(List<GeneralLedger> generalLedger) {
        this.generalLedger = generalLedger;
    }
}
