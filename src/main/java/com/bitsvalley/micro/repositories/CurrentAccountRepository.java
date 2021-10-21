package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CurrentAccount;
import com.bitsvalley.micro.domain.SavingAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CurrentAccountRepository extends CrudRepository<CurrentAccount, Long> {

    SavingAccount findByAccountNumber(String accountNumber);

    @Query("SELECT COUNT(*) AS numberOfSavingAccount FROM SavingAccount")
    int findAllCount();

}
