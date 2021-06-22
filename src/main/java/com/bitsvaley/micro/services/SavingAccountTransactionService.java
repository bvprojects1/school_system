package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.SavingAccountTransaction;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.repositories.SavingAccountRepository;
import com.bitsvaley.micro.repositories.SavingAccountTransactionRepository;
import com.bitsvaley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvaley.micro.repositories.UserRepository;
import com.bitsvaley.micro.utils.BVMicroUtils;
import com.bitsvaley.micro.utils.SavingAccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SavingAccountTransactionService extends SuperService{

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private SavingAccountTransactionRepository savingAccountTransactionRepository;

    @Autowired
    private SavingAccountTypeRepository savingAccountTypeRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

    private double minimumSavings;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

//    public void createSavingAccount(SavingAccount savingAccount, User user) {
////        User user = userService.findUserByUserName("admin");
//        savingAccount.setAccountNumber(new String(""+new Random())); //Collision
//        savingAccount.setCreatedBy(getLoggedInUserName());
//        savingAccount.setCreatedDate(LocalDateTime.now());
//        savingAccount.setLastUpdatedBy(getLoggedInUserName());
//        savingAccount.setAccountLocked(false);
//        savingAccount.setLastUpdatedDate(LocalDateTime.now());
//        savingAccount.setSavingAccountType(insureAccountSavingsTypeExists());
//        savingAccount.setUser(user); //TODO:Add User
//        savingAccountRepository.save(savingAccount);
//        user.getSavingAccount().add(savingAccount);
//        userService.saveUser(user);
//    }

    public void createSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        savingAccountTransaction.setCreatedBy(getLoggedInUserName());
        savingAccountTransaction.setCreatedDate(LocalDateTime.now());
        SavingAccount savingAccount = (SavingAccount)request.getSession().getAttribute("savingAccount");
        savingAccountTransaction.setSavingAccount(savingAccount);
        savingAccountTransactionRepository.save(savingAccountTransaction);
        userService.saveUser(user);
    }


    private com.bitsvaley.micro.domain.SavingAccountType insureAccountSavingsTypeExists() {
        com.bitsvaley.micro.domain.SavingAccountType savingAccountType = savingAccountTypeRepository.findByName(SavingAccountType.MONTHLY_SAVINGS.name());
        if( null == savingAccountType ){
            savingAccountType = new com.bitsvaley.micro.domain.SavingAccountType();
            savingAccountType.setName(SavingAccountType.MONTHLY_SAVINGS.name());
            savingAccountTypeRepository.save(savingAccountType);
            return savingAccountType;
        }
        return savingAccountType;
    }

}
