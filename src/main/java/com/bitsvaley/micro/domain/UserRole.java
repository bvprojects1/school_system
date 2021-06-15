package com.bitsvaley.micro.domain;

import javax.persistence.*;

/**
 * @author Fru Chifen
 * 09.07.2021
 */
@Entity
@Table(name = "userRole")
public class UserRole {

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
