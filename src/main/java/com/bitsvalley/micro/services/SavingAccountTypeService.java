package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvalley.micro.domain.SavingAccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SavingAccountTypeService extends SuperService{

    @Autowired
    private SavingAccountTypeRepository savingAccountTypeRepository;

    public SavingAccountType getSavingAccountType(String name){
        SavingAccountType byName = savingAccountTypeRepository.findByName(name);
        return byName;
    }

}
