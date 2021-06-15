package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.SavingAccountTransaction;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.repositories.SavingAccountRepository;
import com.bitsvaley.micro.repositories.SavingAccountTransactionRepository;
import com.bitsvaley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvaley.micro.repositories.UserRepository;
import com.bitsvaley.micro.utils.SavingAccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SavingAccountService extends SuperService{

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private SavingAccountTypeRepository savingAccountTypeRepository;

    @Autowired
    private SavingAccountTransactionRepository savingAccountTransactionRepository;

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

    public void createSavingAccount(SavingAccount savingAccount) {
        User user = userService.findUserByUserName("admin");
        savingAccount.setAccountNumber(new String(""+new Random())); //Collision
        savingAccount.setCreatedBy(getLoggedInUserName());
        savingAccount.setCreatedDate(LocalDateTime.now());
        savingAccount.setLastUpdatedBy(getLoggedInUserName());
        savingAccount.setAccountLocked(false);
        savingAccount.setLastUpdatedDate(LocalDateTime.now());
        savingAccount.setSavingAccountType(insureAccountSavingsTypeExists());
        savingAccount.setUser(userService.findUserByUserName("admin")); //TODO:Add User
        savingAccountRepository.save(savingAccount);
        user.getSavingAccount().add(savingAccount);
        userService.saveUser(user);
    }

    public void createSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction) {
        //Get id of savingAccount transaction
        User user = userService.findUserByUserName("admin");
        savingAccountTransaction.setCreatedBy(getLoggedInUserName());
        savingAccountTransaction.setCreatedDate(LocalDateTime.now());
        savingAccountTransactionRepository.save(savingAccountTransaction);
//        savingAccount.getSavingAccount().add(savingAccount);
//        savingAccountRepository.get(savingAccountTransaction);
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
