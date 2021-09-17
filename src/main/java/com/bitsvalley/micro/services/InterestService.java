package com.bitsvalley.micro.services;


import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class InterestService {


    public double calculateInterestAccruedMonthCompounded(int interestRate,
                                                           LocalDateTime createdDate,
                                                           int principalAmount){
        double interestPlusOne = (
                interestRate* .01*.0833333) + 1;
        double temp = Math.pow(interestPlusOne,getNumberOfMonths(createdDate));
        temp = temp - 1;
        return principalAmount * temp;
    }

    public double calculateInterestAccruedMonthCompounded(int interestRate,
                                                          int noOfMonths,
                                                          int principalAmount){
        double interestPlusOne = (
                interestRate* .01*.0833333) + 1;
        double temp = Math.pow(interestPlusOne,noOfMonths);
        temp = temp - 1;
        return principalAmount * temp;
    }

    private double getNumberOfMonths(LocalDateTime cretedDateInput) {
        double noOfMonths = 0.0;
        Duration diff = Duration.between(cretedDateInput, LocalDateTime.now() );
        noOfMonths = diff.toDays() / 30;
        return Math.floor(noOfMonths);
    }

}
