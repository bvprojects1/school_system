package com.bitsvaley.micro.repositories;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.SavingAccountTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SavingAccountTransactionRepository extends CrudRepository<SavingAccountTransaction, Long> {

    List<SavingAccountTransaction> findBySavingAccount(String savingAccount);

}
