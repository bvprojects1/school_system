package com.bitsvalley.micro.repositories;
import com.bitsvalley.micro.domain.LedgerAccount;
import com.bitsvalley.micro.domain.LoanAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LedgerAccountRepository extends CrudRepository<LedgerAccount, Long> {

    public LedgerAccount findByName(String name);

    @Query(value = "SELECT * FROM LEDGERACCOUNT la WHERE la.id != :id", nativeQuery = true)
    List<LedgerAccount>  findAllExcept(Long id);

//    @Query(value = "SELECT * FROM LEDGERACCOUNT la WHERE la.code = :code", nativeQuery = true)
    LedgerAccount  findByCode(String code);
//
}
