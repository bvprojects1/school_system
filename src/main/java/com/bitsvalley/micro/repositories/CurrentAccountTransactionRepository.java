package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CurrentAccountTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CurrentAccountTransactionRepository extends CrudRepository<CurrentAccountTransaction, Long> {

    List<CurrentAccountRepository> findByCurrentAccount(String currentAccount);

    Optional<CurrentAccountTransaction> findByReference(String reference);

}
