package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CurrentAccountTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CurrentAccountTransactionRepository extends CrudRepository<CurrentAccountTransaction, Long> {

    List<CurrentAccountRepository> findByCurrentAccount(String currentAccount);

    Optional<CurrentAccountTransaction> findByReference(String reference);

    @Query(value = "SELECT * FROM CURRENT_ACCOUNT_TRANSACTION ca WHERE ca.created_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<CurrentAccountTransaction> searchStartEndDate(String startDate, String endDate);

    @Query(value = "SELECT * FROM CURRENT_ACCOUNT_TRANSACTION ca WHERE ca.created_date BETWEEN :startDate AND :endDate AND ca.created_by = :userName", nativeQuery = true)
    List<CurrentAccountTransaction> searchStartEndDate(String startDate, String endDate, String userName);

}
