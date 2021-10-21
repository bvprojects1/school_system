package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.LoanAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LoanAccountRepository extends CrudRepository<LoanAccount, Long> {

    LoanAccount findByAccountNumber(String accountNumber);

    @Query("SELECT COUNT(*) AS numberOfLoanAccount FROM LoanAccount")
    int findAllCount();

    @Query(value = "SELECT *  FROM LOANACCOUNT la WHERE la.Account_Status != 0", nativeQuery = true)
    List<LoanAccount> findByStatusNotActive();

}
