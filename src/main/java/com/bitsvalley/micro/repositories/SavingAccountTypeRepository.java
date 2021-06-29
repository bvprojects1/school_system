package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.SavingAccountType;
import org.springframework.data.repository.CrudRepository;

public interface SavingAccountTypeRepository extends CrudRepository<SavingAccountType, Long> {
    SavingAccountType findByName(String name);
}
