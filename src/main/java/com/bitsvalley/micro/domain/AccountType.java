package com.bitsvalley.micro.domain;

import javax.persistence.*;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Entity
@Table(name = "accountType")
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    String category;

    @Column(unique = true)
    String name;

    @Column(unique = true)
    String number;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
