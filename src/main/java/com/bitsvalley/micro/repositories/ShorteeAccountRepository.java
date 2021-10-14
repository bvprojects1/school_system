package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.ShorteeAccount;
import com.bitsvalley.micro.domain.ShorteeAsset;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ShorteeAccountRepository extends CrudRepository<ShorteeAccount, Long> {


}
