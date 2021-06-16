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

import java.time.LocalDateTime;
import java.util.Optional;

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
    private UserRepository userRepository;

    private double minimumSavings;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    public void createSavingAccount(SavingAccount savingAccount) {

        User user = userService.findUserByUserName("admin");
//        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "CM"));
//        savingAccount.setAccountMinBalance(new Double(formatter.format(savingAccount.getAccountMinBalance())));

        savingAccount.setAccountNumber(BVMicroUtils.RandomStringUnbounded_thenCorrect()); //Collision
        savingAccount.setCreatedBy(getLoggedInUserName());
        savingAccount.setCreatedDate(LocalDateTime.now());
        savingAccount.setLastUpdatedBy(getLoggedInUserName());
        savingAccount.setAccountLocked(false);
        savingAccount.setLastUpdatedDate(LocalDateTime.now());
        savingAccount.setSavingAccountType(insureAccountSavingsTypeExists());
        savingAccount.setUser(user); //TODO:Add User
        savingAccountRepository.save(savingAccount);
        user = userRepository.findById(user.getId()).get();//TODO handle optional
        user.getSavingAccount().add(savingAccount);
        userService.saveUser(user);
    }

    public void createSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction,User user) {
        //Get id of savingAccount transaction

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

    public Optional<SavingAccount> findById(long id){
        Optional<SavingAccount> savingAccount = savingAccountRepository.findById(id);
        return savingAccount;
    }

}
