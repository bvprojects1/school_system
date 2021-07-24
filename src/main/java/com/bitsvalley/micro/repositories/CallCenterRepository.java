package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface CallCenterRepository extends CrudRepository<CallCenter, Long> {

    List<CallCenter> findByAccountNumber(String accountNumber);

    List<CallCenter> findByAccountHolderName(String accountNumber);

}
