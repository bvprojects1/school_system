package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.ShorteeAccountRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.Amortization;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class ShorteeService extends SuperService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private CallCenterRepository callCenterRepository;

    @Autowired
    private SavingAccountService savingAccountService;

    @Autowired
    private LoanAccountService loanAccountService;

    @Autowired
    private CallCenterService callCenterService;

    @Autowired
    private ShorteeAccountRepository shorteeAccountRepository;

    @Autowired
    private InterestService interestService;





//    private int calculateMonthlyPayment(LoanAccount loanAccount) {
//        BigDecimal finalLoanAmount = new BigDecimal(
//                loanAccount.getTotalInterestOnLoan() +
//                loanAccount.getLoanAmount() + loanAccount.getInitiationFee());
//        BigDecimal monthlyRate = finalLoanAmount.divide(new BigDecimal(loanAccount.getTermOfLoan()));
//        BigDecimal rounded = monthlyRate.round(new MathContext(2, RoundingMode.HALF_EVEN));
//        return rounded.intValue();
//    }

}
