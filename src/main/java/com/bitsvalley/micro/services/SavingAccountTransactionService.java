package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.SavingAccountRepository;
import com.bitsvalley.micro.repositories.SavingAccountTransactionRepository;
import com.bitsvalley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.BVMicroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

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

    private double minimumSaving;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    public Optional<SavingAccountTransaction> findById(long id){
        Optional<SavingAccountTransaction> savingAccountTransaction = savingAccountTransactionRepository.findById(id);
        return savingAccountTransaction;
    }

    public Optional<SavingAccountTransaction> findByReference(String reference){
        Optional<SavingAccountTransaction> savingAccountTransaction = savingAccountTransactionRepository.findByReference(reference);
        return savingAccountTransaction;
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
