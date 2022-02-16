package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.SavingAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SavingAccountRepository extends CrudRepository<SavingAccount, Long> {

    SavingAccount findByAccountNumber(String accountNumber);

    @Query(value = "SELECT COUNT(*) AS numberOfSavingAccount FROM SavingAccount sa where sa.branchCode = :branchCode")
    int countNumberOfProductsCreatedInBranch(String branchCode);

}
