package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.SavingAccountTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SavingAccountTransactionRepository extends CrudRepository<SavingAccountTransaction, Long> {

    List<SavingAccountTransaction> findBySavingAccount(String savingAccount);

    Optional<SavingAccountTransaction> findByReference(String reference);

}
