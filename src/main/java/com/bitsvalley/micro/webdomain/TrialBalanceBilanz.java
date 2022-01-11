package com.bitsvalley.micro.webdomain;

import java.util.List;

public class TrialBalanceBilanz {

    List<TrialBalanceWeb> trialBalanceWeb;
    double creditTotal;
    double debitTotal;
    double totalDifference;

    public List<TrialBalanceWeb> getTrialBalanceWeb() {
        return trialBalanceWeb;
    }

    public void setTrialBalanceWeb(List<TrialBalanceWeb> trialBalanceWeb) {
        this.trialBalanceWeb = trialBalanceWeb;
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

    public double getTotalDifference() {
        return totalDifference;
    }

    public void setTotalDifference(double totalDifference) {
        this.totalDifference = totalDifference;
    }

}
