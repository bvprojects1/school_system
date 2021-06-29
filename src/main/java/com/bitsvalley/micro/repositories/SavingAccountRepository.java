package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.SavingAccount;
import org.springframework.data.repository.CrudRepository;

public interface SavingAccountRepository extends CrudRepository<SavingAccount, Long> {

    SavingAccount findByAccountNumber(String accountNumber);

}
