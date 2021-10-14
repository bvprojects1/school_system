package com.bitsvalley.micro.webdomain;

import com.bitsvalley.micro.domain.GeneralLedger;

import java.util.List;

public class GeneralLedgerBilanz {

    List<GeneralLedger> generalLedger;
    double creditTotal;
    double debitTotal;
    double total;

    public List<GeneralLedger> getGeneralLedger() {
        return generalLedger;
    }

    public void setGeneralLedger(List<GeneralLedger> generalLedger) {
        this.generalLedger = generalLedger;
    }

    public double getCreditTotal() {
        return creditTotal;
    }

    public void setCreditTotal(double creditTotal) {
        this.creditTotal = creditTotal;
    }

    public double getDebitTotal() {
        return debitTotal;
    }

    public void setDebitTotal(double debitTotal) {
        this.debitTotal = debitTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
