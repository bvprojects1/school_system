package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CurrentAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CurrentAccountRepository extends CrudRepository<CurrentAccount, Long> {

    CurrentAccount findByAccountNumber(String accountNumber);

    @Query("SELECT COUNT(*) AS numberOfSavingAccount FROM SavingAccount")
    int findAllCount();

}
