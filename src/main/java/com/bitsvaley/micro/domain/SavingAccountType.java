package com.bitsvaley.micro.domain;

import javax.persistence.*;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Entity
@Table(name = "savingAccountType")
public class SavingAccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
