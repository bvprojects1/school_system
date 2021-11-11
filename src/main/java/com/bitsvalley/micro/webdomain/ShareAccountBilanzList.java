package com.bitsvalley.micro.webdomain;

import java.util.ArrayList;
import java.util.List;

public class ShareAccountBilanzList {

    List<ShareAccountBilanz> shareAccountBilanz = new ArrayList<ShareAccountBilanz>();

    String totalCurrent = "0";
    String currentShareBalance = "";
    String numberOfShareAccounts = "0";

    public List<ShareAccountBilanz> getShareAccountBilanz() {
        return shareAccountBilanz;
    }

    public void setShareAccountBilanz(List<ShareAccountBilanz> shareAccountBilanz) {
        this.shareAccountBilanz = shareAccountBilanz;
    }

    public String getTotalCurrent() {
        return totalCurrent;
    }

    public void setTotalCurrent(String totalCurrent) {
        this.totalCurrent = totalCurrent;
    }

    public String getNumberOfLoanAccounts() {
        return numberOfShareAccounts;
    }

}
