package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.LoanAccount;
import com.bitsvalley.micro.domain.SavingAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface LoanAccountRepository extends CrudRepository<LoanAccount, Long> {

    LoanAccount findByAccountNumber(String accountNumber);

    @Query("SELECT COUNT(*) AS numberOfLoanAccount FROM LoanAccount")
    int findAllCount();

}
