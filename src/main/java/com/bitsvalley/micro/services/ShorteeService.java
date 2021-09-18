package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.ShorteeAccountRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.AccountStatus;
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


    @NotNull
    public LoanAccount createLoanAccount(User user, LoanAccount loanAccount,
                                                    SavingAccount savingAccountGuarantor) {
        Date createdDate = new Date();
        String loggedInUserName = getLoggedInUserName();
        ShorteeAccount shorteeAccount = new ShorteeAccount();
        SavingAccount shorteeSavingAccount = savingAccountService.findByAccountNumber(
                savingAccountGuarantor.getAccountNumber());
        shorteeAccount.setSavingAccount( shorteeSavingAccount );

        shorteeSavingAccount.setAccountMinBalance(
                shorteeSavingAccount.getAccountMinBalance() + loanAccount.getGuarantor1Amount1());
        shorteeSavingAccount.setLastUpdatedDate(createdDate);
        shorteeSavingAccount.setLastUpdatedBy(loggedInUserName);
        shorteeSavingAccount.setAccountStatus(AccountStatus.ACTIVE);
        shorteeSavingAccount.setAccountLocked(true);
        savingAccountService.save(shorteeSavingAccount);

        callCenterService.callCenterShorteeUpdate(shorteeSavingAccount, loanAccount.getGuarantor1Amount1());

        callCenterService.saveCallCenterLog(
                user.getFirstName(),user.getLastName(),loanAccount.getAccountNumber(),
                loanAccount.getAccountType().getName(),""
                );

        shorteeAccount.setAmountShortee(loanAccount.getGuarantor1Amount1());
        shorteeAccount.setCreatedDate(createdDate);
        shorteeAccount.setLastUpdatedDate(createdDate);

        shorteeAccount.setCreatedBy(loggedInUserName);
        shorteeAccount.setLastUpdatedBy(loggedInUserName);
        shorteeAccountRepository.save(shorteeAccount);

        ArrayList<ShorteeAccount> listShorteeAccount = new ArrayList<ShorteeAccount>();
        listShorteeAccount.add(shorteeAccount);
        loanAccount.setShorteeAccounts(listShorteeAccount);
        loanAccountService.createLoanAccount(loanAccount,user );

        return loanAccount;
    }

}
