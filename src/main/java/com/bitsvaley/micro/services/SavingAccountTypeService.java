package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.*;
import com.bitsvaley.micro.repositories.SavingAccountRepository;
import com.bitsvaley.micro.repositories.SavingAccountTransactionRepository;
import com.bitsvaley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvaley.micro.repositories.UserRepository;
import com.bitsvaley.micro.utils.BVMicroUtils;
import com.bitsvaley.micro.webdomain.SavingsBilanz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SavingAccountTypeService extends SuperService{

    @Autowired
    private SavingAccountTypeRepository savingAccountTypeRepository;

    public SavingAccountType getSavingAccountType(String name){
        SavingAccountType byName = savingAccountTypeRepository.findByName(name);
        return byName;
    }

}
