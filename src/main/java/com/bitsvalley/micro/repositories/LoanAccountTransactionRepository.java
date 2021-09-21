package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.LoanAccountTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LoanAccountTransactionRepository extends CrudRepository<LoanAccountTransaction, Long> {

    List<LoanAccountTransaction> findByLoanAccount(String loanAccount);

    Optional<LoanAccountTransaction> findByReference(String reference);

}
