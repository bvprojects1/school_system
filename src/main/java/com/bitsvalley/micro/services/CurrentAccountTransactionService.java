package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.CurrentAccountTransaction;
import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import com.bitsvalley.micro.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentAccountTransactionService extends SuperService{

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private CurrentAccountTransactionRepository currentAccountTransactionRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

    private double minimumSaving;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    public Optional<CurrentAccountTransaction> findById(long id){
        Optional<CurrentAccountTransaction> currentAccountTransaction = currentAccountTransactionRepository.findById(id);
        return currentAccountTransaction;
    }

    public Optional<CurrentAccountTransaction> findByReference(String reference){
        Optional<CurrentAccountTransaction> currentAccountTransaction = currentAccountTransactionRepository.findByReference(reference);
        return currentAccountTransaction;
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


//    private com.bitsvalley.micro.domain.SavingAccountType insureAccountSavingTypeExists() {
//        com.bitsvalley.micro.domain.SavingAccountType savingAccountType = savingAccountTypeRepository.findByName(com.bitsvalley.micro.utils.SavingAccountType.MONTHLY_SAVING.name());
//        if( null == savingAccountType ){
//            savingAccountType = new com.bitsvalley.micro.domain.SavingAccountType();
//            savingAccountType.setName(com.bitsvalley.micro.utils.SavingAccountType.MONTHLY_SAVING.name());
//            savingAccountTypeRepository.save(savingAccountType);
//            return savingAccountType;
//        }
//        return savingAccountType;
//    }

}
