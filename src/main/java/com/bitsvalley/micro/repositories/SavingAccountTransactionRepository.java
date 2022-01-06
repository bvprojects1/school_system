package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CurrentAccountTransaction;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SavingAccountTransactionRepository extends CrudRepository<SavingAccountTransaction, Long> {

    List<SavingAccountTransaction> findBySavingAccount(String savingAccount);

    Optional<SavingAccountTransaction> findByReference(String reference);

    @Query(value = "SELECT * FROM SAVING_ACCOUNT_TRANSACTION ca WHERE ca.created_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<SavingAccountTransaction> searchStartEndDate(String startDate, String endDate);

    @Query(value = "SELECT * FROM SAVING_ACCOUNT_TRANSACTION ca WHERE ca.created_date BETWEEN :startDate AND :endDate AND ca.created_by = :userName", nativeQuery = true)
    List<SavingAccountTransaction> searchStartEndDate(String startDate, String endDate, String userName);

}
