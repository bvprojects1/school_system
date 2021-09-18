package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.LoanAccount;
import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.AccountTypeRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class CallCenterService extends SuperService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private CallCenterRepository callCenterRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public List<CallCenter> findByAccountNumber(String accountNumber) {
        return callCenterRepository.findByAccountNumber(accountNumber);
    }

    public List<CallCenter> findUserByFirstLastname(String firstLastName) {
        return callCenterRepository.findByAccountHolderName(firstLastName);
    }

    public CallCenter saveCallCenterLog(String firstName, String lastName, String accountNumber, String accountType, String userName) {
        CallCenter callCenter = new CallCenter();
        callCenter.setAccountHolderName(firstName + " " + lastName);
        callCenter.setAccountNumber(" Tester tester ------ tester ");
        callCenter.setDate(new Date(System.currentTimeMillis()));
        callCenter.setNotes("Savings Account Created: " + accountNumber + " Savings Type: " + accountType);
        callCenter.setUserName(userName);
        return callCenter;
    }

    public void callCenterUpdate(SavingAccount savingAccount) {
        CallCenter callCenter = new CallCenter();
        callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " " + savingAccount.getUser().getFirstName());
        callCenter.setAccountNumber(savingAccount.getAccountNumber());
        callCenter.setDate(new Date(System.currentTimeMillis()));
        callCenter.setNotes("Savings Account Created: " + savingAccount.getAccountNumber() + " Savings Type: " + savingAccount.getAccountSavingType().getName());
        callCenter.setUserName(savingAccount.getUser().getUserName());
        callCenterRepository.save(callCenter);
    }

    public void callCenterShorteeUpdate(SavingAccount savingAccount, int guarantorAmount) {

//        TODO Add comment in statement for minbalance available raised with 0 transaction amount

        CallCenter callCenter = new CallCenter();
        callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " " +
                savingAccount.getUser().getFirstName());
        callCenter.setDate(new Date(System.currentTimeMillis()));
        callCenter.setNotes("Savings minimum balance added by: " + guarantorAmount);
        callCenter.setUserName(getLoggedInUserName());
        callCenterRepository.save(callCenter);

    }
}