package com.bitsvaley.micro.forms;

import java.time.LocalDateTime;
import com.bitsvaley.micro.domain.User;

public class CustomerForm extends User {


    private long taxPayerIdentificationNumber;
    private long insuranceCardNumber;
    private String successorName;

    public String getSuccessorName() { return successorName; }

    public void setSuccessorName(String successorName) { this.successorName = successorName; }

    public long getInsuranceCardNumber() { return insuranceCardNumber; }

    public void setInsuranceCardNumber(long insuranceCardNumber) { this.insuranceCardNumber = insuranceCardNumber; }

    public long getTaxPayerIdentificationNumber() { return taxPayerIdentificationNumber; }

    public void setTaxPayerIdentificationNumber(long taxPayerIdentificationNumber) { this.taxPayerIdentificationNumber = taxPayerIdentificationNumber; }


}
