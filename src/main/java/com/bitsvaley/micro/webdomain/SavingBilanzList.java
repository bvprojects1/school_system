package com.bitsvaley.micro.webdomain;

import java.util.ArrayList;
import java.util.List;

public class SavingBilanzList {

    List<SavingsBilanz> savingsBilanzList = new ArrayList<SavingsBilanz>();
    double totalSavingInterest = 100.0;
    String totalSaving = "";

    public List<SavingsBilanz> getSavingsBilanzList() {
        return savingsBilanzList;
    }

    public void setSavingsBilanzList(List<SavingsBilanz> savingsBilanzList) {
        this.savingsBilanzList = savingsBilanzList;
    }

    public double getTotalSavingInterest() {
        return totalSavingInterest;
    }

    public void setTotalSavingInterest(double totalSavingInterest) {
        this.totalSavingInterest = totalSavingInterest;
    }

    public String getTotalSaving() {
        return totalSaving;
    }

    public void setTotalSaving(String totalSaving) {
        this.totalSaving = totalSaving;
    }
}
