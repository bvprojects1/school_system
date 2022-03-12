package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.GeneralLedger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface GeneralLedgerRepository extends CrudRepository<GeneralLedger, Long> {

    @Query(value = "SELECT DISTINCT created_By FROM GeneralLegder", nativeQuery = true)
    public ArrayList<String> findAllDistinctByCreatedBy();

    List<GeneralLedger> findByAccountNumber(String accountNumber);

    List<GeneralLedger> findByReference(String reference);

    List<GeneralLedger> findGLByType(String type);

    @Query(value = "SELECT * FROM GENERALLEGDER ORDER BY CREATED_DATE DESC ", nativeQuery = true)
    List<GeneralLedger> findAllOldestFirst();

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.created_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<GeneralLedger> searchCriteriaStartEndDate(String startDate, String endDate);

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.created_date BETWEEN :startDate AND :endDate AND created_by = :createdBy", nativeQuery = true)
    List<GeneralLedger> searchCriteriaWithCreatedBy(String startDate, String endDate, String createdBy);

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.created_date BETWEEN :startDate AND :endDate AND account_number = :accountNumber", nativeQuery = true)
    List<GeneralLedger> searchCriteriaWithAccountNumber(String startDate, String endDate, String accountNumber);

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.created_date BETWEEN :startDate AND :endDate AND created_by = :createdBy AND ledger_account_id = :ledgerAccount", nativeQuery = true)
    List<GeneralLedger> searchCriteriaWithCreatedByAndLedgerAccount(String startDate, String endDate, String createdBy, long ledgerAccount);

//    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.type = :type AND gl.created_date BETWEEN :startDate AND :endDate AND account_number = :accountNumber", nativeQuery = true)
//    List<GeneralLedger> searchCriteriaWithAccountNumberAndType(String type, String startDate, String endDate, String accountNumber);

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.created_date BETWEEN :startDate AND :endDate AND ledger_account_id = :ledgerAccount", nativeQuery = true)
    List<GeneralLedger> searchCriteriaLedger(String startDate, String endDate, long ledgerAccount);

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.created_date BETWEEN :startDate AND :endDate AND account_number = :accountNumber AND ledger_account_id = :ledgerAccount", nativeQuery = true)
    List<GeneralLedger> searchCriteriaWithAccountNumberLedger(String startDate, String endDate, String accountNumber, long ledgerAccount);

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.type = :type AND gl.created_date BETWEEN :startDate AND :endDate AND account_number = :accountNumber AND ledger_account_id = :ledgerAccount", nativeQuery = true)
    List<GeneralLedger> searchCriteriaWithAccountNumberAndTypeLedger(String type, String startDate, String endDate, String accountNumber, long ledgerAccount);

    @Query(value = "SELECT SUM(amount) FROM GENERALLEGDER gl WHERE gl.created_date BETWEEN :startDate AND :endDate AND gl.ledger_account_id = :ledgerAccount AND gl.type = :debit", nativeQuery = true)
    Double searchCriteriaLedgerType( String startDate, String endDate, long ledgerAccount, String debit );

}
