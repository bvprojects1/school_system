package com.bitsvalley.micro.webdomain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String students_surname;
    private String students_given_name;
    private String students_address;
    private String place_of_birth;
    private Date students_date_of_birth;
    private String gender;
    private Long students_tel;
    private String guardians_title;
    private String guardians_full_name;
    private String guardians_address;
    private Long guardians_tel;
    private String web_dev_check;
    private String software_test_check;
    private String app_dev_check;
    private String networking_check;
    private String comp_awareness_check;
    private Long receipt_ref;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudents_surname() {
        return students_surname;
    }

    public void setStudents_surname(String students_surname) {
        this.students_surname = students_surname;
    }

    public String getStudents_given_name() {
        return students_given_name;
    }

    public void setStudents_given_name(String students_given_name) {
        this.students_given_name = students_given_name;
    }

    public String getStudents_address() {
        return students_address;
    }

    public void setStudents_address(String students_address) {
        this.students_address = students_address;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
    }

    public Date getStudents_date_of_birth() {
        return students_date_of_birth;
    }

    public void setStudents_date_of_birth(Date students_date_of_birth) {
        this.students_date_of_birth = students_date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getStudents_tel() {
        return students_tel;
    }

    public void setStudents_tel(Long students_tel) {
        this.students_tel = students_tel;
    }

    public String getGuardians_title() {
        return guardians_title;
    }

    public void setGuardians_title(String guardians_title) {
        this.guardians_title = guardians_title;
    }

    public String getGuardians_full_name() {
        return guardians_full_name;
    }

    public void setGuardians_full_name(String guardians_full_name) {
        this.guardians_full_name = guardians_full_name;
    }

    public String getGuardians_address() {
        return guardians_address;
    }

    public void setGuardians_address(String guardians_address) {
        this.guardians_address = guardians_address;
    }

    public Long getGuardians_tel() {
        return guardians_tel;
    }

    public void setGuardians_tel(Long guardians_tel) {
        this.guardians_tel = guardians_tel;
    }

    public String getWeb_dev_check() {
        return web_dev_check;
    }

    public void setWeb_dev_check(String web_dev_check) {
        this.web_dev_check = web_dev_check;
    }

    public String getSoftware_test_check() {
        return software_test_check;
    }

    public void setSoftware_test_check(String software_test_check) {
        this.software_test_check = software_test_check;
    }

    public String getApp_dev_check() {
        return app_dev_check;
    }

    public void setApp_dev_check(String app_dev_check) {
        this.app_dev_check = app_dev_check;
    }

    public String getNetworking_check() {
        return networking_check;
    }

    public void setNetworking_check(String networking_check) {
        this.networking_check = networking_check;
    }

    public String getComp_awareness_check() {
        return comp_awareness_check;
    }

    public void setComp_awareness_check(String comp_awareness_check) {
        this.comp_awareness_check = comp_awareness_check;
    }

    public Long getReceipt_ref() {
        return receipt_ref;
    }

    public void setReceipt_ref(Long receipt_ref) {
        this.receipt_ref = receipt_ref;
    }
}
