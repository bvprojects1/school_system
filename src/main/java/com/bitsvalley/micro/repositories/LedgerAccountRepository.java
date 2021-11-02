package com.bitsvalley.micro.repositories;
import com.bitsvalley.micro.domain.LedgerAccount;
import com.bitsvalley.micro.domain.RuntimeProperties;
import org.springframework.data.repository.CrudRepository;

public interface LedgerAccountRepositoryRepository extends CrudRepository<LedgerAccount, Long> {

    public LedgerAccount findByCa(String name);

}
