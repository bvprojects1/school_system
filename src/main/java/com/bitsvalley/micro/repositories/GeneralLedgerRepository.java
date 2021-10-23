package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.GeneralLedger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeneralLedgerRepository extends CrudRepository<GeneralLedger, Long> {

    List<GeneralLedger> findByAccountNumber(String accountNumber);
    List<GeneralLedger> findGLByType(String type);

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.created_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<GeneralLedger> searchCriteria(String startDate, String endDate);

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.created_date BETWEEN :startDate AND :endDate AND account_number = :accountNumber", nativeQuery = true)
    List<GeneralLedger> searchCriteriaWithAccountNumber(String startDate, String endDate, String accountNumber);

    @Query(value = "SELECT * FROM GENERALLEGDER gl WHERE gl.type = :type AND gl.created_date BETWEEN :startDate AND :endDate AND account_number = :accountNumber", nativeQuery = true)
    List<GeneralLedger> searchCriteriaWithAccountNumberAndType(String type, String startDate, String endDate, String accountNumber);


}
