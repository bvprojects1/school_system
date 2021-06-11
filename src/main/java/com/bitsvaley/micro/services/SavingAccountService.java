package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.repositories.SavingAccountRepository;
import com.bitsvaley.micro.repositories.SavingAccountTypeRepository;
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
    private UserRoleService userRoleService;
    private double minimumSavings;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    public void createSavingAccount(SavingAccount savingAccount) {
        savingAccount.setAccountNumber(new Random().
                longs(100000000,999999999).toString()); //Collision
        savingAccount.setCreatedBy(getLoggedInUserName());
        savingAccount.setCreatedDate(LocalDateTime.now());
        savingAccount.setLastUpdatedBy(getLoggedInUserName());
        savingAccount.setAccountLocked(false);
        savingAccount.setLastUpdatedDate(LocalDateTime.now());
        savingAccount.setSavingAccountType(insureAccountSavingsTypeExists());

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
