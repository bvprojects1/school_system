package com.bitsvalley.micro.webdomain;

import com.bitsvalley.micro.domain.GeneralLedger;

import java.util.List;

public class GeneralLedgerBilanz {

    List<GeneralLedger> generalLedger;
    int creditTotal;
    int debitTotal;
    int total;

    public List<GeneralLedger> getGeneralLedger() {
        return generalLedger;
    }

    public void setGeneralLedger(List<GeneralLedger> generalLedger) {
        this.generalLedger = generalLedger;
    }

    public int getCreditTotal() {
        return creditTotal;
    }

    public void setCreditTotal(int creditTotal) {
        this.creditTotal = creditTotal;
    }

    public int getDebitTotal() {
        return debitTotal;
    }

    public void setDebitTotal(int debitTotal) {
        this.debitTotal = debitTotal;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
