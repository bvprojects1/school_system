package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.CurrentAccount;
import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.AccountTypeRepository;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.BVMicroUtils;
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

    public CallCenter saveCallCenterLog(String reference, String username, String accountNumber, String notes) {
        CallCenter callCenter = new CallCenter();
        callCenter.setReference(reference);
        callCenter.setAccountNumber(accountNumber);
        callCenter.setDate(new Date(System.currentTimeMillis()));
        callCenter.setNotes(notes);
        callCenter.setUserName(username);
        callCenterRepository.save(callCenter);
        return callCenter;
    }

//    public void callCenterUpdate(SavingAccount savingAccount) {
//        CallCenter callCenter = new CallCenter();
//        callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " " + savingAccount.getUser().getFirstName());
//        callCenter.setAccountNumber(savingAccount.getAccountNumber());
//        callCenter.setDate(new Date(System.currentTimeMillis()));
//        callCenter.setNotes("Savings Account Created: " + savingAccount.getAccountNumber() + " Savings Type: " + savingAccount.getAccountSavingType().getName());
//        callCenter.setUserName(savingAccount.getUser().getUserName());
//        callCenterRepository.save(callCenter);
//    }

    public void callCenterShorteeUpdate(SavingAccount savingAccount, int guarantorAmount) {

//        TODO Add comment in statement for minbalance available raised with 0 transaction amount

        CallCenter callCenter = new CallCenter();
        callCenter.setReference("");
        callCenter.setDate(new Date(System.currentTimeMillis()));
        callCenter.setNotes(BVMicroUtils.SAVINGS_MINIMUM_BALANCE_ADDED_BY + BVMicroUtils.formatCurrency(guarantorAmount));
        callCenter.setAccountNumber(savingAccount.getAccountNumber());
        callCenter.setUserName(getLoggedInUserName());
        callCenterRepository.save(callCenter);

    }

    public void callCenterSavingAccount(SavingAccount savingAccount) {
        saveCallCenterLog("", savingAccount.getUser().getUserName(),
                savingAccount.getAccountNumber(), BVMicroUtils.SAVING_ACCOUNT_CREATED);
    }

    public void callCenterCurrentAccount(CurrentAccount currentAccount) {
        saveCallCenterLog("", currentAccount.getUser().getUserName(),
                currentAccount.getAccountNumber(), BVMicroUtils.CURRENT_ACCOUNT_CREATED);
    }

    public void callCenterUserAccount(User user, String notes) {
        CallCenter callCenter = new CallCenter();
        callCenter.setReference("");
        callCenter.setNotes(notes);
        callCenter.setUserName(user.getUserName());
        callCenter.setDate(new Date());
        callCenterRepository.save(callCenter);
    }

}