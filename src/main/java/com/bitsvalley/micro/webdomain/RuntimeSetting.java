package com.bitsvalley.micro.webdomain;

public class RuntimeSetting {

    private String businessName;
    private String slogan;
    private String telephone;
    private String telephone2;
    private String logo;
    private String unionLogo;
    private String logoSize;
    private String themeColor;
    private String themeColor2;
    private String fax;
    private String email;
    private String website;
    private int noOfAccounts;
    private String address;
    private Double vatPercent;
    private String unitSharePrice;

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getNoOfAccounts() {
        return noOfAccounts;
    }

    public void setNoOfAccounts(int noOfAccounts) {
        this.noOfAccounts = noOfAccounts;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogoSize() {
        return logoSize;
    }

    public void setLogoSize(String logoSize) {
        this.logoSize = logoSize;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public String getThemeColor2() {
        return themeColor2;
    }

    public void setThemeColor2(String themeColor2) {
        this.themeColor2 = themeColor2;
    }

    public String getUnionLogo() {
        unionLogo.replace("\\", "/");
        unionLogo.replace("\\", "/");
        return unionLogo;
    }

    public void setUnionLogo(String unionLogo) {
        this.unionLogo = unionLogo;
    }

    public String getUnitSharePrice() { return unitSharePrice; }

    public void setUnitSharePrice(String unitSharePrice) { this.unitSharePrice = unitSharePrice; }

    public Double getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(Double vatPercent) {
        this.vatPercent = vatPercent;
    }
}
