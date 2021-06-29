package com.bitsvalley.micro.webdomain;

import java.util.ArrayList;
import java.util.List;

public class SavingBilanzList {

    List<SavingBilanz> savingBilanzList = new ArrayList<SavingBilanz>();
    double totalSavingInterest = 100.0;
    String totalSaving = "0";


    public double getTotalSavingInterest() {
        return totalSavingInterest;
    }

    public void setTotalSavingInterest(double totalSavingInterest) {
        this.totalSavingInterest = totalSavingInterest;
    }

    public List<SavingBilanz> getSavingBilanzList() {
        return savingBilanzList;
    }

    public void setSavingBilanzList(List<SavingBilanz> savingBilanzList) {
        this.savingBilanzList = savingBilanzList;
    }

    public String getTotalSaving() {
        return totalSaving;
    }

    public void setTotalSaving(String totalSaving) {
        this.totalSaving = totalSaving;
    }
}
