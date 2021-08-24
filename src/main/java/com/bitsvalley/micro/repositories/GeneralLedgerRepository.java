package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.GeneralLedger;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeneralLedgerRepository extends CrudRepository<GeneralLedger, Long> {

    List<GeneralLedger> findByAccountNumber(String accountNumber);


}
