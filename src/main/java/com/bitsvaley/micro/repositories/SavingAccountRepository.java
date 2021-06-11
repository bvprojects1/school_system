package com.bitsvaley.micro.repositories;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface SavingAccountRepository extends CrudRepository<SavingAccount, Long> {

    SavingAccount findByAccountNumber(String accountNumber);

}
