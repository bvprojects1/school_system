package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.AccountType;
import org.springframework.data.repository.CrudRepository;

public interface AccountTypeRepository extends CrudRepository<AccountType, Long> {
    AccountType findByName(String name);
    AccountType findByNumber(String number);
}
