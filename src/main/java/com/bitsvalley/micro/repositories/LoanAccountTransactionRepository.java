package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.LoanAccountTransaction;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LoanAccountTransactionRepository extends CrudRepository<LoanAccountTransaction, Long> {

    List<LoanAccountTransaction> findByLoanAccount(String loanAccount);

    Optional<LoanAccountTransaction> findByReference(String reference);

    @Query(value = "SELECT * FROM LOAN_ACCOUNT_TRANSACTION ca WHERE ca.created_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<LoanAccountTransaction> searchStartEndDate(String startDate, String endDate);

    @Query(value = "SELECT * FROM LOAN_ACCOUNT_TRANSACTION ca WHERE ca.created_date BETWEEN :startDate AND :endDate AND ca.created_by = :userName", nativeQuery = true)
    List<LoanAccountTransaction> searchStartEndDate(String startDate, String endDate, String userName);

}
