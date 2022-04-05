package com.bitsvalley.micro.webdomain;

import com.bitsvalley.micro.domain.LedgerAccount;

import java.util.Date;

/**
 * @author Fru Chifen
 * 21.08.2021
 */
public class TrialBalanceWeb {

        private String type;
        private String code;
        private String name;
        private double debitTotal;
        private double creditTotal;
        private double totalDifference;
    private String classCode;


    public double getTotalDifference() {
        return totalDifference;
    }

    public void setTotalDifference(double totalDifference) {
        this.totalDifference = totalDifference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDebitTotal() {
        return debitTotal;
    }

    public void setDebitTotal(double debitTotal) {
        this.debitTotal = debitTotal;
    }

    public double getCreditTotal() {
        return creditTotal;
    }

    public void setCreditTotal(double creditTotal) {
        this.creditTotal = creditTotal;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassCode() {
        return classCode;
    }
}
