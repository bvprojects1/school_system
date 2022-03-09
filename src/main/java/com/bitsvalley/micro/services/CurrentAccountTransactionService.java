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

}
