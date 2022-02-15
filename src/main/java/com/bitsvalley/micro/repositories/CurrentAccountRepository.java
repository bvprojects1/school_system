package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CurrentAccount;
import com.bitsvalley.micro.domain.GeneralLedger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrentAccountRepository extends CrudRepository<CurrentAccount, Long> {

    CurrentAccount findByAccountNumber(String accountNumber);

    @Query(value = "SELECT COUNT(*) AS numberOfCurrentAccount FROM CurrentAccount ca where ca.branchCode = :branchCode")
    int countNumberOfProductsCreatedInBranch(String branchCode);

}
