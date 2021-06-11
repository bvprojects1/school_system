package com.bitsvaley.micro.repositories;

import com.bitsvaley.micro.domain.SavingAccountType;
import com.bitsvaley.micro.domain.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface SavingAccountTypeRepository extends CrudRepository<SavingAccountType, Long> {
    SavingAccountType findByName(String name);
}
