package com.bitsvalley.micro.services;

import com.bitsvalley.micro.repositories.AccountTypeRepository;
import com.bitsvalley.micro.domain.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountTypeService extends SuperService{

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public AccountType getAccountType(String name){
        AccountType byName = accountTypeRepository.findByName(name);
//        if(null == byName){
//            accountTypeRepository.save();
//        }
        return byName;
    }

}
